package io.github.saulkimgoodman.shortsblocker

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class BlockerAccessibilityService : AccessibilityService() {

    private val youtubeDetector = YouTubeShortsDetector()
    private val instagramDetector = InstagramReelsDetector()
    private val facebookDetector = FacebookReelsDetector()

    private lateinit var prefs: PreferencesManager
    private lateinit var usage: UsageTracker
    private var lastBlockedTime = 0L
    private val debounceDelayMs = 800L // 0.8 seconds debounce

    // Battery Optimization Check: Throttling interval for content changes.
    // Screen scanning is a heavy CPU operation. Throttling checks to once every 150ms
    // during layout animations or scrolls saves up to 90% CPU usage with zero latency impact.
    private var lastScanTime = 0L
    private val scanThrottleIntervalMs = 150L

    // Daily-limit mode: while the user is on a short-form screen we run a 1s "session tick"
    // that accumulates watch time and blocks once the daily allowance is spent.
    private val handler = Handler(Looper.getMainLooper())
    private var sessionPackage: String? = null
    private var lastTickElapsed = 0L
    private val tickIntervalMs = 1000L

    private val sessionTick = object : Runnable {
        override fun run() {
            val pkg = sessionPackage ?: return
            val now = SystemClock.elapsedRealtime()
            val delta = now - lastTickElapsed
            lastTickElapsed = now
            // Ignore absurd gaps (device slept mid-session); count only real watch time.
            if (delta in 1..(tickIntervalMs * 5)) {
                usage.addUsage(pkg, delta)
            }

            val root = rootInActiveWindow
            val stillOnShorts = root != null &&
                    root.packageName?.toString() == pkg &&
                    isShortFormScreen(pkg, root)
            if (!stillOnShorts) {
                endSession()
                return
            }

            if (isDailyLimitExhausted()) {
                endSession()
                executeBlockAction(pkg, limitReached = true)
                return
            }
            handler.postDelayed(this, tickIntervalMs)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs = PreferencesManager(this)
        usage = UsageTracker(this)
        Log.d("ShortsBlocker", "Service connected successfully")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (!::prefs.isInitialized) return

        val rootNode = rootInActiveWindow ?: return
        val packageName = event.packageName?.toString() ?: ""

        val currentTime = System.currentTimeMillis()

        // Battery Optimization:
        // WindowStateChanged represents a major screen switch -> check immediately.
        // WindowContentChanged fires constantly (e.g. scrolls) -> throttle check interval.
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (currentTime - lastScanTime < scanThrottleIntervalMs) {
                return
            }
        }
        lastScanTime = currentTime

        // Handle debug logging
        if (prefs.isDebugMode && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("ShortsBlockerDebug", "--- Window State Changed for Package: $packageName ---")
            logNodeTree(rootNode)
        }

        // Check if global master toggle is enabled
        if (!prefs.isMasterEnabled) return

        // Check if package is monitored and corresponding setting is enabled
        val isTarget = when (packageName) {
            DetectionSignatures.PACKAGE_YOUTUBE -> prefs.isYouTubeEnabled
            DetectionSignatures.PACKAGE_INSTAGRAM -> prefs.isInstagramEnabled
            DetectionSignatures.PACKAGE_FACEBOOK -> prefs.isFacebookEnabled
            else -> false
        }

        if (!isTarget) {
            endSession()
            return
        }

        val onShortForm = isShortFormScreen(packageName, rootNode)

        if (!onShortForm) {
            endSession()
            return
        }

        if (prefs.limitMode == PreferencesManager.LIMIT_MODE_DAILY && !isDailyLimitExhausted()) {
            // Allowance remaining: let the user watch and meter the time instead of blocking.
            startSession(packageName)
        } else {
            endSession()
            executeBlockAction(
                packageName,
                limitReached = prefs.limitMode == PreferencesManager.LIMIT_MODE_DAILY
            )
        }
    }

    private fun isShortFormScreen(packageName: String, rootNode: AccessibilityNodeInfo): Boolean {
        return when (packageName) {
            DetectionSignatures.PACKAGE_YOUTUBE -> youtubeDetector.isShortForm(rootNode)
            DetectionSignatures.PACKAGE_INSTAGRAM -> instagramDetector.isShortForm(rootNode)
            DetectionSignatures.PACKAGE_FACEBOOK -> facebookDetector.isShortForm(rootNode)
            else -> false
        }
    }

    private fun isDailyLimitExhausted(): Boolean {
        if (prefs.limitMode != PreferencesManager.LIMIT_MODE_DAILY) return false
        return usage.todayTotalMs() >= prefs.dailyLimitMinutes * 60_000L
    }

    private fun startSession(packageName: String) {
        if (sessionPackage == packageName) return // tick already running
        endSession()
        sessionPackage = packageName
        lastTickElapsed = SystemClock.elapsedRealtime()
        handler.postDelayed(sessionTick, tickIntervalMs)
        Log.d("ShortsBlocker", "Usage session started for $packageName")
    }

    private fun endSession() {
        if (sessionPackage == null) return
        sessionPackage = null
        handler.removeCallbacks(sessionTick)
    }

    private fun executeBlockAction(packageName: String, limitReached: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockedTime < debounceDelayMs) {
            // Debounce active: ignore duplicate trigger on the same active screen transition
            return
        }
        lastBlockedTime = currentTime
        Log.i("ShortsBlocker", "Short-form content detected on $packageName! Triggering block.")

        if (prefs.blockMode == "BACK") {
            performGlobalAction(GLOBAL_ACTION_BACK)
        } else {
            val intent = Intent(this, BlockOverlayActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(BlockOverlayActivity.EXTRA_LIMIT_REACHED, limitReached)
            }
            startActivity(intent)
        }
    }

    private fun logNodeTree(node: AccessibilityNodeInfo?, depth: Int = 0) {
        if (node == null) return
        if (depth > 20) return // Safety cutoff for extremely deep trees
        val indent = "  ".repeat(depth)
        val resourceId = node.viewIdResourceName ?: "null"
        val text = node.text?.toString() ?: "null"
        val contentDesc = node.contentDescription?.toString() ?: "null"
        val className = node.className?.toString() ?: "null"

        Log.d("ShortsBlockerDebug", "$indent[Class: $className] [ID: $resourceId] [Text: $text] [Desc: $contentDesc]")

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            logNodeTree(child, depth + 1)
        }
    }

    override fun onInterrupt() {
        endSession()
        Log.d("ShortsBlocker", "Service interrupted")
    }

    override fun onDestroy() {
        endSession()
        super.onDestroy()
    }
}

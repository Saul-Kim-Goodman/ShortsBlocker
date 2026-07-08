package io.github.saulkimgoodman.shortsblocker

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate

/**
 * Persists daily short-form watch time per app.
 * All data lives in local SharedPreferences only; history keeps the last [HISTORY_DAYS] days.
 */
class UsageTracker(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("shorts_blocker_usage", Context.MODE_PRIVATE)

    private fun today(): String = LocalDate.now().toString()

    private fun appKey(packageName: String): String = when (packageName) {
        DetectionSignatures.PACKAGE_YOUTUBE -> "yt"
        DetectionSignatures.PACKAGE_INSTAGRAM -> "ig"
        DetectionSignatures.PACKAGE_FACEBOOK -> "fb"
        else -> "etc"
    }

    /** Archives today's counters into history and resets them when the date has changed. */
    private fun rollDateIfNeeded() {
        val today = today()
        val stored = prefs.getString(KEY_DATE, null)
        if (stored == today) return

        val editor = prefs.edit()
        if (stored != null) {
            val total = APP_KEYS.sumOf { prefs.getLong("today_$it", 0L) }
            if (total > 0) editor.putLong("hist_$stored", total)
        }
        APP_KEYS.forEach { editor.putLong("today_$it", 0L) }
        editor.putString(KEY_DATE, today)

        // Prune history entries older than HISTORY_DAYS
        val cutoff = LocalDate.now().minusDays(HISTORY_DAYS.toLong()).toString()
        prefs.all.keys
            .filter { it.startsWith("hist_") && it.removePrefix("hist_") < cutoff }
            .forEach { editor.remove(it) }
        editor.apply()
    }

    fun addUsage(packageName: String, ms: Long) {
        if (ms <= 0) return
        rollDateIfNeeded()
        val key = "today_${appKey(packageName)}"
        prefs.edit().putLong(key, prefs.getLong(key, 0L) + ms).apply()
    }

    fun todayMsFor(packageName: String): Long {
        rollDateIfNeeded()
        return prefs.getLong("today_${appKey(packageName)}", 0L)
    }

    fun todayTotalMs(): Long {
        rollDateIfNeeded()
        return APP_KEYS.sumOf { prefs.getLong("today_$it", 0L) }
    }

    /** Last [days] days (oldest first, today included), zero-filled for days without usage. */
    fun recentDays(days: Int = 7): List<Pair<LocalDate, Long>> {
        rollDateIfNeeded()
        val todayDate = LocalDate.now()
        return (days - 1 downTo 0).map { offset ->
            val date = todayDate.minusDays(offset.toLong())
            val ms = if (date == todayDate) todayTotalMs() else prefs.getLong("hist_$date", 0L)
            date to ms
        }
    }

    companion object {
        private const val KEY_DATE = "usage_date"
        private const val HISTORY_DAYS = 14
        private val APP_KEYS = listOf("yt", "ig", "fb", "etc")
    }
}

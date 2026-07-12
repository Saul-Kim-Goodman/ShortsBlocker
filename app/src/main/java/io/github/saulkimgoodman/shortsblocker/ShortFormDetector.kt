package io.github.saulkimgoodman.shortsblocker

import android.view.accessibility.AccessibilityNodeInfo

interface ShortFormDetector {
    fun isShortForm(rootNode: AccessibilityNodeInfo): Boolean
}

abstract class BaseShortFormDetector : ShortFormDetector {
    protected fun traverseAndFind(
        node: AccessibilityNodeInfo?,
        depth: Int = 0,
        matchCriteria: (AccessibilityNodeInfo) -> Boolean
    ): Boolean {
        if (node == null) return false
        
        // Optimization 1: Max depth limit pruning.
        // Short-form container markers can sit deep in the tree — e.g. YouTube's
        // reel_recycler / reel_watch_fragment_root live around depth 17-19 in recent
        // app versions. Cutting off too early (the old limit of 10) meant the markers
        // were never reached and nothing was blocked. Keep a generous cap that still
        // bounds pathological trees while comfortably covering real layouts.
        if (depth > 30) return false
        
        // Optimization 2: Visibility pruning. Skip invisible subtrees completely.
        if (!node.isVisibleToUser) {
            return false
        }
        
        if (matchCriteria(node)) {
            return true
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (traverseAndFind(child, depth + 1, matchCriteria)) {
                return true
            }
        }
        return false
    }
}

class YouTubeShortsDetector : BaseShortFormDetector() {
    override fun isShortForm(rootNode: AccessibilityNodeInfo): Boolean {
        return traverseAndFind(rootNode) { node ->
            val resourceId = node.viewIdResourceName
            
            // Optimization 3: Fast-path exclusions for tab/navigation bars using String references
            if (resourceId != null && (
                resourceId.contains("tab", ignoreCase = true) || 
                resourceId.contains("bar", ignoreCase = true) || 
                resourceId.contains("nav", ignoreCase = true)
            )) {
                return@traverseAndFind false
            }

            val text = node.text
            val contentDesc = node.contentDescription
            val className = node.className

            // 1. Check Resource IDs
            val matchesResourceId = resourceId != null && DetectionSignatures.YOUTUBE_RESOURCE_IDS.any { id ->
                resourceId.contains(id, ignoreCase = true)
            }

            // 2. Check Content Description (Exact match)
            val matchesContentDesc = contentDesc != null && className != null && 
                DetectionSignatures.YOUTUBE_CONTENT_DESCRIPTIONS.any { desc ->
                    contentDesc.toString().equals(desc, ignoreCase = true) && 
                    (className.contains("RelativeLayout") || className.contains("FrameLayout") || className.contains("ViewGroup"))
                }

            // 3. Check Text patterns (Exact match)
            val matchesText = text != null && className != null && 
                DetectionSignatures.YOUTUBE_TEXT_PATTERNS.any { txt ->
                    text.toString().equals(txt, ignoreCase = true) && 
                    (className.contains("Player") || className.contains("Reel") || className.contains("Video"))
                }

            matchesResourceId || matchesContentDesc || matchesText
        }
    }
}

class InstagramReelsDetector : BaseShortFormDetector() {
    override fun isShortForm(rootNode: AccessibilityNodeInfo): Boolean {
        return traverseAndFind(rootNode) { node ->
            val resourceId = node.viewIdResourceName
            
            // Optimization 3: Fast-path exclusions for tab/navigation bars
            if (resourceId != null && (
                resourceId.contains("tab", ignoreCase = true) || 
                resourceId.contains("bar", ignoreCase = true) || 
                resourceId.contains("nav", ignoreCase = true)
            )) {
                return@traverseAndFind false
            }

            val text = node.text
            val contentDesc = node.contentDescription
            val className = node.className

            // 1. Check Resource IDs (Clips viewer container)
            val matchesResourceId = resourceId != null && DetectionSignatures.INSTAGRAM_RESOURCE_IDS.any { id ->
                resourceId.contains(id, ignoreCase = true)
            }

            // 2. Check Content Description ("Reels" or "릴스" - exact match)
            val matchesContentDesc = contentDesc != null && className != null && 
                DetectionSignatures.INSTAGRAM_CONTENT_DESCRIPTIONS.any { desc ->
                    contentDesc.toString().equals(desc, ignoreCase = true) &&
                    (className.contains("ViewPager") || className.contains("FrameLayout") || className.contains("RecyclerView"))
                }

            // 3. Check Text patterns
            val matchesText = text != null && className != null && resourceId != null && 
                DetectionSignatures.INSTAGRAM_TEXT_PATTERNS.any { txt ->
                    text.toString().contains(txt, ignoreCase = true) &&
                    (className.contains("TextView") && resourceId.contains("clips"))
                }

            matchesResourceId || matchesContentDesc || matchesText
        }
    }
}

class FacebookReelsDetector : BaseShortFormDetector() {
    override fun isShortForm(rootNode: AccessibilityNodeInfo): Boolean {
        return traverseAndFind(rootNode) { node ->
            val resourceId = node.viewIdResourceName
            
            // Optimization 3: Fast-path exclusions for tab/navigation bars
            if (resourceId != null && (
                resourceId.contains("tab", ignoreCase = true) || 
                resourceId.contains("bar", ignoreCase = true) || 
                resourceId.contains("nav", ignoreCase = true)
            )) {
                return@traverseAndFind false
            }

            val text = node.text
            val contentDesc = node.contentDescription
            val className = node.className

            // 1. Check Resource IDs
            val matchesResourceId = resourceId != null && DetectionSignatures.FACEBOOK_RESOURCE_IDS.any { id ->
                resourceId.contains(id, ignoreCase = true)
            }

            // 2. Check Text patterns
            val matchesText = text != null && className != null && 
                DetectionSignatures.FACEBOOK_TEXT_PATTERNS.any { txt ->
                    text.toString().contains(txt, ignoreCase = true) &&
                    (className.contains("TextView") || className.contains("Button"))
                }

            // 3. Check Content Description
            val matchesContentDesc = contentDesc != null && 
                DetectionSignatures.FACEBOOK_CONTENT_DESCRIPTIONS.any { desc ->
                    contentDesc.toString().equals(desc, ignoreCase = true)
                }

            matchesResourceId || matchesText || matchesContentDesc
        }
    }
}

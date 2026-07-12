package io.github.saulkimgoodman.shortsblocker

object DetectionSignatures {
    // Packages
    const val PACKAGE_YOUTUBE = "com.google.android.youtube"
    const val PACKAGE_INSTAGRAM = "com.instagram.android"
    const val PACKAGE_FACEBOOK = "com.facebook.katana"

    // YouTube Shorts detection signatures.
    // Only match resource IDs that belong to the immersive Shorts *player*. The home
    // feed's "Shorts" shelf and the bottom-nav Shorts tab expose a ViewGroup/Button with
    // content-desc "Shorts" and a TextView with text "Shorts" — matching on those text /
    // content-desc values caused the block to fire on the normal home feed (false
    // positive). These player resource IDs are absent everywhere except an open Short.
    val YOUTUBE_RESOURCE_IDS = listOf(
        "reel_watch_fragment_root",
        "reel_recycler",
        "reel_player_page_container",
        "reel_watch_player",
        "reel_player_underlay",
        "reel_player_view_layout",
        "shorts_player"
    )
    // Intentionally empty — see note above. Detection is resource-ID only for YouTube.
    val YOUTUBE_TEXT_PATTERNS = emptyList<String>()
    val YOUTUBE_CONTENT_DESCRIPTIONS = emptyList<String>()

    // Instagram Reels detection signatures.
    // Same reasoning: "Reels"/"릴스" text and content-desc also appear on the Reels tab and
    // in-feed Reels shelves, so we match only the clips (Reels) viewer container.
    val INSTAGRAM_RESOURCE_IDS = listOf(
        "com.instagram.android:id/clips_viewer_container",
        "clips_viewer"
    )
    val INSTAGRAM_CONTENT_DESCRIPTIONS = emptyList<String>()
    val INSTAGRAM_TEXT_PATTERNS = emptyList<String>()

    // Facebook Reels detection signatures.
    // Same reasoning: match only the Reels viewer, not the "Reels" tab / shelf labels.
    val FACEBOOK_RESOURCE_IDS = listOf(
        "com.facebook.katana:id/reels_viewer_activity",
        "com.facebook.katana:id/reels_viewer",
        "reels_viewer"
    )
    val FACEBOOK_TEXT_PATTERNS = emptyList<String>()
    val FACEBOOK_CONTENT_DESCRIPTIONS = emptyList<String>()
}

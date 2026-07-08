package io.github.saulkimgoodman.shortsblocker

object DetectionSignatures {
    // Packages
    const val PACKAGE_YOUTUBE = "com.google.android.youtube"
    const val PACKAGE_INSTAGRAM = "com.instagram.android"
    const val PACKAGE_FACEBOOK = "com.facebook.katana"

    // YouTube Shorts detection signatures
    val YOUTUBE_RESOURCE_IDS = listOf(
        "com.google.android.youtube:id/shorts_player",
        "com.google.android.youtube:id/reel_recycler",
        "com.google.android.youtube:id/reel_player_view_layout",
        "shorts_player",
        "reel_"
    )
    val YOUTUBE_TEXT_PATTERNS = listOf(
        "Shorts",
        "쇼츠"
    )
    val YOUTUBE_CONTENT_DESCRIPTIONS = listOf(
        "Shorts",
        "쇼츠"
    )

    // Instagram Reels detection signatures
    val INSTAGRAM_RESOURCE_IDS = listOf(
        "com.instagram.android:id/clips_viewer_container",
        "clips_viewer"
    )
    val INSTAGRAM_CONTENT_DESCRIPTIONS = listOf(
        "Reels",
        "릴스"
    )
    val INSTAGRAM_TEXT_PATTERNS = listOf(
        "Reels",
        "릴스"
    )

    // Facebook Reels detection signatures
    val FACEBOOK_RESOURCE_IDS = listOf(
        "com.facebook.katana:id/reels_viewer_activity",
        "com.facebook.katana:id/reels_viewer",
        "reels_viewer"
    )
    val FACEBOOK_TEXT_PATTERNS = listOf(
        "Reels",
        "릴스",
        "릴스 만들기",
        "Reels 플레이어"
    )
    val FACEBOOK_CONTENT_DESCRIPTIONS = listOf(
        "Reels",
        "릴스",
        "Reels 플레이어"
    )
}

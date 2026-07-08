package io.github.saulkimgoodman.shortsblocker

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shorts_blocker_prefs", Context.MODE_PRIVATE)

    var isMasterEnabled: Boolean
        get() = prefs.getBoolean("master_enabled", true)
        set(value) = prefs.edit().putBoolean("master_enabled", value).apply()

    var isYouTubeEnabled: Boolean
        get() = prefs.getBoolean("yt_enabled", true)
        set(value) = prefs.edit().putBoolean("yt_enabled", value).apply()

    var isInstagramEnabled: Boolean
        get() = prefs.getBoolean("ig_enabled", true)
        set(value) = prefs.edit().putBoolean("ig_enabled", value).apply()

    var isFacebookEnabled: Boolean
        get() = prefs.getBoolean("fb_enabled", true)
        set(value) = prefs.edit().putBoolean("fb_enabled", value).apply()

    var blockMode: String
        get() = prefs.getString("block_mode", "BACK") ?: "BACK"
        set(value) = prefs.edit().putString("block_mode", value).apply()

    var customMessage: String
        get() = prefs.getString("custom_message", "집중할 시간입니다! 숏폼 시청이 제한되었습니다.") ?: "집중할 시간입니다! 숏폼 시청이 제한되었습니다."
        set(value) = prefs.edit().putString("custom_message", value).apply()

    var isDebugMode: Boolean
        get() = prefs.getBoolean("debug_mode", false)
        set(value) = prefs.edit().putBoolean("debug_mode", value).apply()
}

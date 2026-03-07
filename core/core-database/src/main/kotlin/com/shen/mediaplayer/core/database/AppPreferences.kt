package com.shen.mediaplayer.core.database

import com.shen.mediaplayer.core.database.dao.AppConfigDao
import com.shen.mediaplayer.core.database.entity.AppConfigEntity

class AppPreferences(
    private val appConfigDao: AppConfigDao
) {

    companion object {
        const val THEME_SYSTEM = 0
        const val THEME_LIGHT = 1
        const val THEME_DARK = 2

        private const val KEY_PLAYBACK_SPEED = "playback_speed"
        private const val KEY_AUTO_PLAY_NEXT = "auto_play_next"
        private const val KEY_RESUME_ON_STARTUP = "resume_on_startup"
        private const val KEY_SLEEP_TIMER_MINUTES = "sleep_timer_minutes"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLORS_ENABLED = "dynamic_colors_enabled"
        private const val KEY_PRIVATE_FOLDER_PASSWORD = "private_folder_password"
        private const val KEY_BIOMETRICS_ENABLED = "biometrics_enabled"
        private const val KEY_ONLINE_FEATURES_ENABLED = "online_features_enabled"
    }

    // Playback Speed
    val playbackSpeed: Float get() = getFloat(KEY_PLAYBACK_SPEED, 1.0f)
    suspend fun savePlaybackSpeed(speed: Float) = saveFloat(KEY_PLAYBACK_SPEED, speed)

    // Auto Play Next
    val autoPlayNext: Boolean get() = getBoolean(KEY_AUTO_PLAY_NEXT, true)
    suspend fun saveAutoPlayNext(enabled: Boolean) = saveBoolean(KEY_AUTO_PLAY_NEXT, enabled)

    // Resume Playback On Startup
    val resumePlaybackOnStartup: Boolean get() = getBoolean(KEY_RESUME_ON_STARTUP, true)
    suspend fun saveResumeOnStartup(enabled: Boolean) = saveBoolean(KEY_RESUME_ON_STARTUP, enabled)

    // Sleep Timer
    val sleepTimerMinutes: Int get() = getInt(KEY_SLEEP_TIMER_MINUTES, 0)
    suspend fun saveSleepTimerMinutes(minutes: Int) = saveInt(KEY_SLEEP_TIMER_MINUTES, minutes)

    // Theme Mode
    val themeMode: Int get() = getInt(KEY_THEME_MODE, THEME_SYSTEM)
    suspend fun saveThemeMode(mode: Int) = saveInt(KEY_THEME_MODE, mode)

    // Dynamic Colors
    val dynamicColorsEnabled: Boolean get() = getBoolean(KEY_DYNAMIC_COLORS_ENABLED, false)
    suspend fun saveDynamicColorsEnabled(enabled: Boolean) = saveBoolean(KEY_DYNAMIC_COLORS_ENABLED, enabled)

    // Private Folder Password
    val privateFolderPassword: String? get() = getString(KEY_PRIVATE_FOLDER_PASSWORD, null)
    suspend fun savePrivateFolderPassword(password: String?) = saveString(KEY_PRIVATE_FOLDER_PASSWORD, password)

    // Biometrics Enabled
    val biometricsEnabled: Boolean get() = getBoolean(KEY_BIOMETRICS_ENABLED, false)
    suspend fun saveBiometricsEnabled(enabled: Boolean) = saveBoolean(KEY_BIOMETRICS_ENABLED, enabled)

    // Online Features Enabled
    val onlineFeaturesEnabled: Boolean get() = getBoolean(KEY_ONLINE_FEATURES_ENABLED, false)
    suspend fun saveOnlineFeaturesEnabled(enabled: Boolean) = saveBoolean(KEY_ONLINE_FEATURES_ENABLED, enabled)

    private suspend fun getString(key: String, defaultValue: String?): String? {
        return appConfigDao.getByKey(key)?.value ?: defaultValue
    }

    private suspend fun saveString(key: String, value: String?) {
        if (value == null) {
            appConfigDao.deleteByKey(key)
        } else {
            appConfigDao.insert(AppConfigEntity(key, value))
        }
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return getString(key, null)?.toBooleanStrictOrNull() ?: defaultValue
    }

    private suspend fun saveBoolean(key: String, value: Boolean) {
        saveString(key, value.toString())
    }

    private fun getInt(key: String, defaultValue: Int): Int {
        return getString(key, null)?.toIntOrNull() ?: defaultValue
    }

    private suspend fun saveInt(key: String, value: Int) {
        saveString(key, value.toString())
    }

    private fun getFloat(key: String, defaultValue: Float): Float {
        return getString(key, null)?.toFloatOrNull() ?: defaultValue
    }

    private suspend fun saveFloat(key: String, value: Float) {
        saveString(key, value.toString())
    }
}

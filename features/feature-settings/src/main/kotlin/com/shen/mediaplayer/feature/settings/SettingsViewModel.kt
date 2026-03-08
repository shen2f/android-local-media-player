package com.shen.mediaplayer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.database.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences
) : ViewModel() {

    enum class ThemeMode {
        System, Light, Dark
    }

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _autoPlayNext = MutableStateFlow(true)
    val autoPlayNext: StateFlow<Boolean> = _autoPlayNext

    private val _resumeOnStartup = MutableStateFlow(true)
    val resumeOnStartup: StateFlow<Boolean> = _resumeOnStartup

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes

    private val _themeMode = MutableStateFlow(ThemeMode.System)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _dynamicColorsEnabled = MutableStateFlow(false)
    val dynamicColorsEnabled: StateFlow<Boolean> = _dynamicColorsEnabled

    private val _privateFolderPasswordEnabled = MutableStateFlow(false)
    val privateFolderPasswordEnabled: StateFlow<Boolean> = _privateFolderPasswordEnabled

    private val _biometricsEnabled = MutableStateFlow(false)
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled

    private val _onlineFeaturesEnabled = MutableStateFlow(false)
    val onlineFeaturesEnabled: StateFlow<Boolean> = _onlineFeaturesEnabled

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            _playbackSpeed.value = preferences.getPlaybackSpeed()
            _autoPlayNext.value = preferences.getAutoPlayNext()
            _resumeOnStartup.value = preferences.getResumePlaybackOnStartup()
            _sleepTimerMinutes.value = preferences.getSleepTimerMinutes()
            _themeMode.value = getStoredThemeMode()
            _dynamicColorsEnabled.value = preferences.getDynamicColorsEnabled()
            _privateFolderPasswordEnabled.value = !preferences.getPrivateFolderPassword().isNullOrEmpty()
            _biometricsEnabled.value = preferences.getBiometricsEnabled()
            _onlineFeaturesEnabled.value = preferences.getOnlineFeaturesEnabled()
        }
    }

    private suspend fun getStoredThemeMode(): ThemeMode {
        return when (preferences.getThemeMode()) {
            AppPreferences.THEME_SYSTEM -> ThemeMode.System
            AppPreferences.THEME_LIGHT -> ThemeMode.Light
            AppPreferences.THEME_DARK -> ThemeMode.Dark
            else -> ThemeMode.System
        }
    }

    fun updatePlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        viewModelScope.launch {
            preferences.savePlaybackSpeed(speed)
        }
    }

    fun updateAutoPlayNext(enabled: Boolean) {
        _autoPlayNext.value = enabled
        viewModelScope.launch {
            preferences.saveAutoPlayNext(enabled)
        }
    }

    fun updateResumeOnStartup(enabled: Boolean) {
        _resumeOnStartup.value = enabled
        viewModelScope.launch {
            preferences.saveResumeOnStartup(enabled)
        }
    }

    fun updateSleepTimer(minutes: Int) {
        _sleepTimerMinutes.value = minutes
        viewModelScope.launch {
            preferences.saveSleepTimerMinutes(minutes)
        }
    }

    fun updateThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        val storedMode = when (mode) {
            ThemeMode.System -> AppPreferences.THEME_SYSTEM
            ThemeMode.Light -> AppPreferences.THEME_LIGHT
            ThemeMode.Dark -> AppPreferences.THEME_DARK
        }
        viewModelScope.launch {
            preferences.saveThemeMode(storedMode)
        }
    }

    fun updateDynamicColorsEnabled(enabled: Boolean) {
        _dynamicColorsEnabled.value = enabled
        viewModelScope.launch {
            preferences.saveDynamicColorsEnabled(enabled)
        }
    }

    fun updatePrivateFolderPassword(password: String?) {
        _privateFolderPasswordEnabled.value = !password.isNullOrEmpty()
        viewModelScope.launch {
            preferences.savePrivateFolderPassword(password)
        }
    }

    fun updateBiometricsEnabled(enabled: Boolean) {
        _biometricsEnabled.value = enabled
        viewModelScope.launch {
            preferences.saveBiometricsEnabled(enabled)
        }
    }

    fun updateOnlineFeaturesEnabled(enabled: Boolean) {
        _onlineFeaturesEnabled.value = enabled
        viewModelScope.launch {
            preferences.saveOnlineFeaturesEnabled(enabled)
        }
    }

    suspend fun verifyPassword(input: String): Boolean {
        return preferences.getPrivateFolderPassword() == input
    }

    suspend fun getCurrentPassword(): String? {
        return preferences.getPrivateFolderPassword()
    }
}

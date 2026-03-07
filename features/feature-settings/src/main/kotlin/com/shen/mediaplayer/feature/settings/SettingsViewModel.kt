package com.shen.mediaplayer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shen.mediaplayer.core.common.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences
) : ViewModel() {

    sealed interface ThemeMode {
        object System : ThemeMode
        object Light : ThemeMode
        object Dark : ThemeMode
    }

    private val _playbackSpeed = MutableStateFlow(preferences.playbackSpeed)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _autoPlayNext = MutableStateFlow(preferences.autoPlayNext)
    val autoPlayNext: StateFlow<Boolean> = _autoPlayNext

    private val _resumeOnStartup = MutableStateFlow(preferences.resumePlaybackOnStartup)
    val resumeOnStartup: StateFlow<Boolean> = _resumeOnStartup

    private val _sleepTimerMinutes = MutableStateFlow(preferences.sleepTimerMinutes)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes

    private val _themeMode = MutableStateFlow(getStoredThemeMode())
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _dynamicColorsEnabled = MutableStateFlow(preferences.dynamicColorsEnabled)
    val dynamicColorsEnabled: StateFlow<Boolean> = _dynamicColorsEnabled

    private val _privateFolderPasswordEnabled = MutableStateFlow(!preferences.privateFolderPassword.isNullOrEmpty())
    val privateFolderPasswordEnabled: StateFlow<Boolean> = _privateFolderPasswordEnabled

    private val _biometricsEnabled = MutableStateFlow(preferences.biometricsEnabled)
    val biometricsEnabled: StateFlow<Boolean> = _biometricsEnabled

    private val _onlineFeaturesEnabled = MutableStateFlow(preferences.onlineFeaturesEnabled)
    val onlineFeaturesEnabled: StateFlow<Boolean> = _onlineFeaturesEnabled

    private fun getStoredThemeMode(): ThemeMode {
        return when (preferences.themeMode) {
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

    fun verifyPassword(input: String): Boolean {
        return preferences.privateFolderPassword == input
    }

    fun getCurrentPassword(): String? {
        return preferences.privateFolderPassword
    }
}

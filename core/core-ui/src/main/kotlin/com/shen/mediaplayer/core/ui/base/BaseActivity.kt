package com.shen.mediaplayer.core.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shen.mediaplayer.core.database.AppPreferences
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.runBlocking

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    
    abstract fun inflateBinding(): VB
    
    abstract fun onCreated(savedInstanceState: Bundle?)
    
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppPreferencesEntryPoint {
        fun appPreferences(): AppPreferences
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
        _binding = inflateBinding()
        setContentView(binding.root)
        onCreated(savedInstanceState)
    }
    
    private fun applyTheme() {
        val entryPoint = EntryPointAccessors.fromApplication(application, AppPreferencesEntryPoint::class.java)
        val preferences = entryPoint.appPreferences()

        // Get theme mode synchronously from shared preferences or use runBlocking since this is needed before setContentView
        val nightMode = when (runBlocking { preferences.getThemeMode() }) {
            AppPreferences.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            AppPreferences.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

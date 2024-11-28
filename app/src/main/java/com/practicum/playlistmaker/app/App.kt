package com.practicum.playlistmaker.app

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.common.constants.AppConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants

class App : Application() {

    private val sharedPreferences by lazy {
        getSharedPreferences(PrefsConstants.PREFS_NAME, MODE_PRIVATE)
    }

    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    private fun setTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun applyTheme() {
        val isDarkTheme = getCurrentTheme()
        setTheme(isDarkTheme)
    }

    fun switchTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PrefsConstants.KEY_IS_DARK_THEME, isDark).apply()
        setTheme(isDark)
    }

    fun getCurrentTheme(): Boolean {
        return if (sharedPreferences.contains(PrefsConstants.KEY_IS_DARK_THEME)) {
            sharedPreferences.getBoolean(PrefsConstants.KEY_IS_DARK_THEME, AppConstants.DARK_THEME_DEF_STATE)
        } else {
            val uiMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            uiMode == Configuration.UI_MODE_NIGHT_YES
        }
    }
}
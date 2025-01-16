package com.practicum.playlistmaker.common.data.manager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.common.constants.AppConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.domain.manager.AppThemeManager

class AppThemeManagerImpl(
    private val sharedPreferences: SharedPreferences,
    private val context: Context,
) : AppThemeManager {

    override fun switchTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(PrefsConstants.KEY_IS_DARK_THEME, isDark).apply()
        setTheme(isDark)
    }

    override fun getCurrentTheme(): Boolean {
        return if (sharedPreferences.contains(PrefsConstants.KEY_IS_DARK_THEME)) {
            sharedPreferences.getBoolean(
                PrefsConstants.KEY_IS_DARK_THEME,
                AppConstants.DARK_THEME_DEF_STATE
            )
        } else {
            val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            uiMode == Configuration.UI_MODE_NIGHT_YES
        }
    }

    private fun setTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
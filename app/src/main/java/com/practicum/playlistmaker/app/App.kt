package com.practicum.playlistmaker.app

import android.app.Application
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.manager.AppThemeManager

class App : Application() {

    private val sharedPreferences by lazy {
        getSharedPreferences(PrefsConstants.PREFS_NAME, MODE_PRIVATE)
    }
    private val appThemeManager: AppThemeManager by lazy {
        AppDependencyCreator.provideThemeManager(sharedPreferences, this)
    }

    override fun onCreate() {
        super.onCreate()
        applyTheme()
    }

    private fun applyTheme() {
        val isDarkTheme = appThemeManager.getCurrentTheme()
        appThemeManager.switchTheme(isDarkTheme)
    }

    fun getThemeManager(): AppThemeManager {
        return appThemeManager
    }
}
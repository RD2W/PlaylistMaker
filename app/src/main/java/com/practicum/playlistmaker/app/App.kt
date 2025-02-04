package com.practicum.playlistmaker.app

import android.app.Application
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor

class App : Application() {

    private val appThemeInteractor: AppThemeInteractor by lazy {
        AppDependencyCreator.provideThemeInteractor()
    }

    override fun onCreate() {
        super.onCreate()
        AppDependencyCreator.initApplication(this)
        applyTheme()
    }

    private fun applyTheme() {
        val isDarkTheme = appThemeInteractor.getCurrentTheme()
        appThemeInteractor.switchTheme(isDarkTheme)
    }
}
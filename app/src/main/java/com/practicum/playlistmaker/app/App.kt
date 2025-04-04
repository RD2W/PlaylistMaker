package com.practicum.playlistmaker.app

import android.app.Application
import com.practicum.playlistmaker.BuildConfig
import com.practicum.playlistmaker.common.di.appModule
import com.practicum.playlistmaker.common.di.dataModule
import com.practicum.playlistmaker.common.di.domainModule
import com.practicum.playlistmaker.common.di.sourceModule
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    private val appThemeInteractor: AppThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            if (BuildConfig.DEBUG) androidLogger(Level.DEBUG)
            modules(
                appModule,
                dataModule,
                domainModule,
                sourceModule,
            )
        }
        applyTheme()
    }

    private fun applyTheme() {
        val isDarkTheme = appThemeInteractor.getCurrentTheme()
        appThemeInteractor.switchTheme(isDarkTheme)
    }
}

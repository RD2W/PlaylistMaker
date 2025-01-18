package com.practicum.playlistmaker.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.common.domain.interactor.impl.AppThemeInteractorImpl
import com.practicum.playlistmaker.common.domain.repository.AppThemeRepository

object AppDependencyCreator {
    private lateinit var application: Application

    fun initApplication(app: Application) {
        application = app
    }

    private fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PrefsConstants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getThemeRepository(): AppThemeRepository {
        return AppThemeRepositoryImpl(application, getSharedPreferences())
    }

    fun provideThemeInteractor(): AppThemeInteractor {
        return AppThemeInteractorImpl(getThemeRepository())
    }
}
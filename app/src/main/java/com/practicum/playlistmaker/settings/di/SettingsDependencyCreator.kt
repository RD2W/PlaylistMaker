package com.practicum.playlistmaker.settings.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.interactor.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

object SettingsDependencyCreator {
    private fun getSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}
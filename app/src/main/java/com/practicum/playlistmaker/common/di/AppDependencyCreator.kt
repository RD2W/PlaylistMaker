package com.practicum.playlistmaker.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.common.domain.interactor.impl.AppThemeInteractorImpl
import com.practicum.playlistmaker.common.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.interactor.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

object AppDependencyCreator {
    private lateinit var application: Application

    fun initApplication(app: Application) {
        application = app
    }

    private fun getExoPlayer(): ExoPlayer {
        return ExoPlayer.Builder(application).build()
    }

    private fun getGson(): Gson {
        return Gson()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PrefsConstants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getThemeRepository(): AppThemeRepository {
        return AppThemeRepositoryImpl(application, getSharedPreferences())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(application, RetrofitClient)
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSharedPreferences(), getGson())
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(application)
    }

    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(application, getExoPlayer())
    }

    fun provideThemeInteractor(): AppThemeInteractor {
        return AppThemeInteractorImpl(getThemeRepository())
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
}
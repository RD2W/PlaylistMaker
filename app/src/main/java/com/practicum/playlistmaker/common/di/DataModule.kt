package com.practicum.playlistmaker.common.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.common.constants.ApiConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.common.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.source.local.LocalClient
import com.practicum.playlistmaker.search.data.source.local.sprefs.SharedPreferencesClient
import com.practicum.playlistmaker.search.data.source.remote.ITunesApiService
import com.practicum.playlistmaker.search.data.source.remote.NetworkClient
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        androidApplication().getSharedPreferences(
            PrefsConstants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    single {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesApiService> {
        get<Retrofit>().create(ITunesApiService::class.java)
    }

    factory { ExoPlayer.Builder(androidApplication()).build() }

    singleOf(::Gson)
    singleOf(::AppThemeRepositoryImpl) { bind<AppThemeRepository>() }
    factoryOf(::PlayerRepositoryImpl) { bind<PlayerRepository>() }
    singleOf(::RetrofitClient) { bind<NetworkClient>() }
    singleOf(::SharedPreferencesClient) { bind<LocalClient>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::SearchHistoryRepositoryImpl) { bind<SearchHistoryRepository>() }
    singleOf(::TracksRepositoryImpl) { bind<TracksRepository>() }
}
package com.practicum.playlistmaker.common.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.common.constants.ApiConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.search.data.source.local.LocalClient
import com.practicum.playlistmaker.search.data.source.local.sprefs.SharedPreferencesClient
import com.practicum.playlistmaker.search.data.source.remote.ITunesApiService
import com.practicum.playlistmaker.search.data.source.remote.NetworkClient
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val sourceModule = module {
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

    singleOf(::Gson)
    singleOf(::RetrofitClient) { bind<NetworkClient>() }
    singleOf(::SharedPreferencesClient) { bind<LocalClient>() }
}
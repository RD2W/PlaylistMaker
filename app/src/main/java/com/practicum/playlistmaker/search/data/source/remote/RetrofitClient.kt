package com.practicum.playlistmaker.search.data.source.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val iTunesApiService: ITunesApiService by lazy {
        retrofit.create(ITunesApiService::class.java)
    }
}
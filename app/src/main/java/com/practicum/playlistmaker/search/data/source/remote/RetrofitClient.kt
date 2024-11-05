package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.common.constants.ApiConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val iTunesApiService: ITunesApiService by lazy {
        retrofit.create(ITunesApiService::class.java)
    }
}
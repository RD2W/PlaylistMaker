package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.common.constants.ApiConstants
import com.practicum.playlistmaker.search.data.model.Response
import com.practicum.playlistmaker.search.data.model.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient : NetworkClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val iTunesApiService: ITunesApiService by lazy {
        retrofit.create(ITunesApiService::class.java)
    }

    override suspend fun doRequest(dto: Any): Response {
        return withContext(Dispatchers.IO) {
            if (dto is TracksSearchRequest) {
                try {
                    val resp = iTunesApiService.searchTracks(dto.expression)
                    val body = resp.body() ?: Response()
                    body.apply { resultCode = resp.code() }
                } catch (e: Exception) {
                    Response().apply { resultCode = 500 }
                }
            } else {
                Response().apply { resultCode = 400 }
            }
        }
    }
}
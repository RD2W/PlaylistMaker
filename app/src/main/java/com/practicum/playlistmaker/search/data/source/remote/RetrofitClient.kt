package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.search.data.model.Response
import com.practicum.playlistmaker.search.data.model.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClient(private val iTunesApiService: ITunesApiService) : NetworkClient {
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
package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.common.constants.ResponseCode.BAD_REQUEST
import com.practicum.playlistmaker.common.constants.ResponseCode.INTERNAL_SERVER_ERROR
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
                    Response().apply { resultCode = INTERNAL_SERVER_ERROR }
                }
            } else {
                Response().apply { resultCode = BAD_REQUEST }
            }
        }
    }
}

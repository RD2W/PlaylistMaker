package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.search.data.model.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}
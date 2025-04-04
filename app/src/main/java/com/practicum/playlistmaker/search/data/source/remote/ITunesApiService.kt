package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.search.data.model.TracksSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
    ): Response<TracksSearchResponse>
}

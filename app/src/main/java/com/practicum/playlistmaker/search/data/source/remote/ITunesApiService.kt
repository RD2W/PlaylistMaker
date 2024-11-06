package com.practicum.playlistmaker.search.data.source.remote

import com.practicum.playlistmaker.search.data.model.TrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): TrackResponse
}
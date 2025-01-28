package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.Resource

interface TracksRepository {
    suspend fun searchTracks(expression: String): Resource<List<Track>>
}
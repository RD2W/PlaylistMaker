package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track

interface TracksRepository {
    suspend fun searchTracks(expression: String): List<Track>
}
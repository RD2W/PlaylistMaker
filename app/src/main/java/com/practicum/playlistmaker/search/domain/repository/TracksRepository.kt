package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.NetworkRequestResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<NetworkRequestResult<List<Track>>>
}

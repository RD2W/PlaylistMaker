package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.model.NetworkRequestResult
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<NetworkRequestResult<List<Track>>>
}
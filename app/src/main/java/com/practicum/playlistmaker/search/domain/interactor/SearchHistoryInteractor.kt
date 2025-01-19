package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface SearchHistoryInteractor {
    fun getHistory(): StateFlow<List<Track>>
    fun addTrack(track: Track)
    fun clearHistory()
}
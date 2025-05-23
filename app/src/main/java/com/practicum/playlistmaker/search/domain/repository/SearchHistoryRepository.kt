package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}

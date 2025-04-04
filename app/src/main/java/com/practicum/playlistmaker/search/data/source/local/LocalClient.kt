package com.practicum.playlistmaker.search.data.source.local

import com.practicum.playlistmaker.common.domain.model.Track

interface LocalClient {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}

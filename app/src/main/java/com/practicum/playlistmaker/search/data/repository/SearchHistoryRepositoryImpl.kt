package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.data.source.local.sprefs.SharedPreferencesClient
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val sharedPreferencesClient: SharedPreferencesClient,
) : SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        return sharedPreferencesClient.getHistory()
    }

    override fun addTrack(track: Track) {
        sharedPreferencesClient.addTrack(track)
    }

    override fun clearHistory() {
        sharedPreferencesClient.clearHistory()
    }
}
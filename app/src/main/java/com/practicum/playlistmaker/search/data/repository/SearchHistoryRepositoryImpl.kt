package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.data.source.local.LocalClient
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val localClient: LocalClient,
) : SearchHistoryRepository {
    override fun addTrack(track: Track) = localClient.addTrack(track)
    override fun getHistory(): List<Track> = localClient.getHistory()
    override fun clearHistory() = localClient.clearHistory()
}
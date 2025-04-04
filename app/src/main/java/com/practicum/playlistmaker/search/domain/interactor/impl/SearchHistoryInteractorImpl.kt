package com.practicum.playlistmaker.search.domain.interactor.impl

import android.util.Log
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    private val historyFlow = MutableStateFlow<List<Track>>(emptyList())

    init {
        loadHistory()
    }

    private fun loadHistory() {
        try {
            historyFlow.value = repository.getHistory()
        } catch (e: Exception) {
            Log.e(LogTags.SEARCH_HISTORY, "Error loading history: ${e.message}", e)
        }
    }

    override fun getHistory(): StateFlow<List<Track>> = historyFlow

    override fun addTrack(track: Track) {
        try {
            repository.addTrack(track)
            loadHistory()
        } catch (e: Exception) {
            Log.e(LogTags.SEARCH_HISTORY, "Error adding a track: ${e.message}", e)
        }
    }

    override fun clearHistory() {
        try {
            repository.clearHistory()
            historyFlow.value = emptyList()
        } catch (e: Exception) {
            Log.e(LogTags.SEARCH_HISTORY, "Error clearing history: ${e.message}", e)
        }
    }
}

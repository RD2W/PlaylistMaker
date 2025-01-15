package com.practicum.playlistmaker.search.domain.interactor.impl

import android.util.Log
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.data.model.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    private val _historyFlow = MutableStateFlow<List<Track>>(emptyList())
    val historyFlow: StateFlow<List<Track>> get() = _historyFlow

    init {
        loadHistory()
    }

    private fun loadHistory() {
        try {
            _historyFlow.value = repository.getHistory()
        } catch (e: Exception) {
            Log.e(LogTags.SEARCH_HISTORY, "Error loading history: ${e.message}", e)
        }
    }

    override fun getHistory(): StateFlow<List<Track>> {
        return historyFlow
    }

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
            _historyFlow.value = emptyList()
        } catch (e: Exception) {
            Log.e(LogTags.SEARCH_HISTORY, "Error clearing history: ${e.message}", e)
        }
    }
}
package com.practicum.playlistmaker.search.domain.interactor.impl

import com.practicum.playlistmaker.common.constants.LogTags.SEARCH_HISTORY
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

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
            Timber.tag(SEARCH_HISTORY).e(e, "Error loading history: ${e.message}")
        }
    }

    override fun getHistory(): StateFlow<List<Track>> = historyFlow

    override fun addTrack(track: Track) {
        try {
            repository.addTrack(track)
            loadHistory()
        } catch (e: Exception) {
            Timber.tag(SEARCH_HISTORY).e(e, "Error adding a track: ${e.message}")
        }
    }

    override fun clearHistory() {
        try {
            repository.clearHistory()
            historyFlow.value = emptyList()
        } catch (e: Exception) {
            Timber.tag(SEARCH_HISTORY).e(e, "Error clearing history: ${e.message}")
        }
    }
}

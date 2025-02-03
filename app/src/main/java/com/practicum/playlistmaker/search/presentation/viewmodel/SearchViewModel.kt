package com.practicum.playlistmaker.search.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.presentation.state.SearchScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val _searchScreenState = MutableLiveData<SearchScreenState>(SearchScreenState.Idle)
    val searchScreenState: LiveData<SearchScreenState> get() = _searchScreenState

    private val handler = Handler(Looper.getMainLooper())
    private val isClickAllowed = AtomicBoolean(true)
    private var latestSearchText: String = DEFAULT_INPUT_TEXT
    private val searchRunnable = Runnable { searchForTracks(latestSearchText) }

    init {
        observeSearchHistory()
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.getHistory().collect { history ->
                _searchScreenState.value = SearchScreenState.SearchHistory(history)
            }
        }
    }

    private fun searchForTracks(term: String) {
        if (term.isBlank()) return
        _searchScreenState.value = SearchScreenState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            tracksInteractor.searchTracks(term) { foundTracks, _, isConnected ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (isConnected) {
                        handleTrackResponse(foundTracks)
                    } else {
                        _searchScreenState.value = SearchScreenState.NetworkError
                    }
                }
            }
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    fun researchDebounce() {
        if (latestSearchText.isNotBlank()) {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
        }
    }

    fun clickDebounce(): Boolean {
        return if (isClickAllowed.get()) {
            isClickAllowed.set(false)
            handler.postDelayed({ isClickAllowed.set(true) }, CLICK_DEBOUNCE_DELAY_MILLIS)
            true
        } else false
    }

    fun addTrackToHistory(track: Track) {
        viewModelScope.launch {
            searchHistoryInteractor.addTrack(track)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryInteractor.clearHistory()
        }
    }

    private fun handleTrackResponse(foundTracks: List<Track>) {
        if (foundTracks.isNotEmpty()) {
            _searchScreenState.value = SearchScreenState.Content(foundTracks)
            Log.d(LogTags.API_RESPONSE, "Tracks found: ${foundTracks.size}")
        } else {
            _searchScreenState.value = SearchScreenState.NotFound
            Log.d(LogTags.API_RESPONSE, "No tracks found")
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    companion object {
        const val DEFAULT_INPUT_TEXT = ""
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val tracksInteractor = AppDependencyCreator.provideTrackInteractor()
                val searchHistoryInteractor = AppDependencyCreator.provideSearchHistoryInteractor()
                SearchViewModel(tracksInteractor, searchHistoryInteractor)
            }
        }
    }
}
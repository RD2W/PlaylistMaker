package com.practicum.playlistmaker.search.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.presentation.state.SearchScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val _searchScreenState = MutableLiveData<SearchScreenState>(SearchScreenState.Idle)
    val searchScreenState: LiveData<SearchScreenState> get() = _searchScreenState

    private val _clickEvent = MutableSharedFlow<TrackParcel>()
    val clickEvent: SharedFlow<TrackParcel> get() = _clickEvent

    private var isClickAllowed = true
    private var latestSearchText: String = DEFAULT_INPUT_TEXT
    private val handler = Handler(Looper.getMainLooper())
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

    fun clickDebounce(track: Track) {
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
            viewModelScope.launch {
                val trackParcel = TrackMapperImpl.toParcel(track)
                _clickEvent.emit(trackParcel)
            }
        }
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
    }
}
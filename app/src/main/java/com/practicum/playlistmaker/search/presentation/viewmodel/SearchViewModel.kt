package com.practicum.playlistmaker.search.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.SEARCH_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags.API_RESPONSE
import com.practicum.playlistmaker.common.constants.LogTags.CLICK_DEBOUNCE
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.NetworkRequestResult
import com.practicum.playlistmaker.search.presentation.state.SearchScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private val _searchScreenState = MutableLiveData<SearchScreenState>(SearchScreenState.Idle)
    val searchScreenState: LiveData<SearchScreenState> get() = _searchScreenState

    private val _clickEvent = MutableSharedFlow<TrackParcel>()
    val clickEvent: SharedFlow<TrackParcel> get() = _clickEvent

    private var latestSearchText: String = DEFAULT_INPUT_TEXT
    private val isClickAllowed = AtomicBoolean(true)

    private val searchDebounced = debounce<String>(
        delayMillis = SEARCH_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = true,
    ) { term ->
        searchForTracks(term)
    }

    private val clickDebounced = debounce<Unit>(
        delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = false,
    ) {
        isClickAllowed.set(true)
    }

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
            tracksInteractor.searchTracks(term).collect { result ->
                withContext(Dispatchers.Main) {
                    when (result) {
                        is NetworkRequestResult.Success -> {
                            handleTrackResponse(result.data)
                        }
                        is NetworkRequestResult.Error -> {
                            _searchScreenState.value = SearchScreenState.NetworkError
                        }
                        is NetworkRequestResult.NoConnection -> {
                            _searchScreenState.value = SearchScreenState.NetworkError
                        }
                    }
                }
            }
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText
        searchDebounced(changedText)
    }

    fun researchDebounce() {
        if (latestSearchText.isNotBlank()) {
            searchDebounced(latestSearchText)
        }
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed.compareAndSet(true, false)) {
            viewModelScope.launch {
                try {
                    val trackParcel = TrackMapperImpl.toParcel(track)
                    _clickEvent.emit(trackParcel)
                } catch (e: Exception) {
                    Timber.tag(CLICK_DEBOUNCE).e("ClickEvent error: $e")
                }
            }
            clickDebounced(Unit) // Используем debounce для сброса флага isClickAllowed
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
            Timber.tag(API_RESPONSE).d("Tracks found: ${foundTracks.size}")
        } else {
            _searchScreenState.value = SearchScreenState.NotFound
            Timber.tag(API_RESPONSE).d("No tracks found")
        }
    }

    companion object {
        const val DEFAULT_INPUT_TEXT = ""
    }
}

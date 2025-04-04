package com.practicum.playlistmaker.media.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.media.domain.usecase.GetFavoriteTracksUseCase
import com.practicum.playlistmaker.media.presentation.state.FavoriteScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class FavoritesViewModel(
    private val getFavoriteTracksUseCase: GetFavoriteTracksUseCase,
) : ViewModel() {

    private val _state = MutableLiveData<FavoriteScreenState>()
    val state: LiveData<FavoriteScreenState> get() = _state

    private val _clickEvent = MutableSharedFlow<TrackParcel>()
    val clickEvent: SharedFlow<TrackParcel> get() = _clickEvent

    private val isClickAllowed = AtomicBoolean(true)
    private val clickDebounced = debounce<Unit>(
        delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = false,
    ) {
        isClickAllowed.set(true)
    }

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        _state.value = FavoriteScreenState.Loading
        viewModelScope.launch {
            getFavoriteTracksUseCase()
                .catch { e ->
                    _state.value = FavoriteScreenState.Error
                }
                .collect { tracks ->
                    _state.value = if (tracks.isEmpty()) {
                        FavoriteScreenState.Empty
                    } else {
                        FavoriteScreenState.Content(tracks)
                    }
                }
        }
    }

    fun clickDebounce(track: Track) {
        if (isClickAllowed.compareAndSet(true, false)) {
            viewModelScope.launch {
                try {
                    val trackParcel = TrackMapperImpl.toParcel(track)
                    _clickEvent.emit(trackParcel)
                } catch (e: Exception) {
                    Log.e(LogTags.CLICK_DEBOUNCE, "ClickEvent error: $e")
                }
            }
            clickDebounced(Unit)
        }
    }
}

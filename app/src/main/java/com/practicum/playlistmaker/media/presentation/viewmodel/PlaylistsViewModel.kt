package com.practicum.playlistmaker.media.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags.CLICK_DEBOUNCE
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLISTS
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsUseCase
import com.practicum.playlistmaker.media.presentation.state.PlaylistsScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PlaylistsViewModel(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
) : ViewModel() {
    private val _state = MutableLiveData<PlaylistsScreenState>()
    val state: LiveData<PlaylistsScreenState> get() = _state

    private val _clickEvent = MutableSharedFlow<Long>()
    val clickEvent: SharedFlow<Long> get() = _clickEvent

    private val isClickAllowed = AtomicBoolean(true)
    private val clickDebounced = debounce<Unit>(
        delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = false,
    ) {
        isClickAllowed.set(true)
    }

    init {
        loadAllPlaylists()
    }

    private fun loadAllPlaylists() {
        _state.value = PlaylistsScreenState.Loading
        viewModelScope.launch {
            getPlaylistsUseCase()
                .catch { e ->
                    _state.value = PlaylistsScreenState.Error
                    Log.e(PLAYLISTS, "Load playlists error: $e")
                }
                .collect { playlists ->
                    _state.value = if (playlists.isEmpty()) {
                        PlaylistsScreenState.Empty
                    } else {
                        PlaylistsScreenState.Content(playlists)
                    }
                }
        }
    }

    fun clickDebounce(playlistId: Long) {
        if (isClickAllowed.compareAndSet(true, false)) {
            viewModelScope.launch {
                try {
                    _clickEvent.emit(playlistId)
                } catch (e: Exception) {
                    Log.e(CLICK_DEBOUNCE, "ClickEvent error: $e")
                }
            }
            clickDebounced(Unit)
        }
    }
}

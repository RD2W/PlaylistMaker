package com.practicum.playlistmaker.playlist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags.CLICK_DEBOUNCE
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLIST
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.media.domain.usecase.DeletePlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistByIdUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.practicum.playlistmaker.playlist.domain.usecase.SharePlaylistUseCase
import com.practicum.playlistmaker.playlist.presentation.state.PlaylistScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class PlaylistViewModel(
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val sharePlaylistUseCase: SharePlaylistUseCase,
    private val removeTrackFromPlaylistUseCase: RemoveTrackFromPlaylistUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<PlaylistScreenState>(PlaylistScreenState.Loading)
    val state: StateFlow<PlaylistScreenState> get() = _state

    private val _clickEvent = MutableSharedFlow<TrackParcel>()
    val clickEvent: SharedFlow<TrackParcel> get() = _clickEvent

    private lateinit var currentPlaylist: Playlist

    private val isClickAllowed = AtomicBoolean(true)
    private val clickDebounced = debounce<Unit>(
        delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = false,
    ) {
        isClickAllowed.set(true)
    }

    private fun initPlaylist(playlist: Playlist) {
        currentPlaylist = playlist
    }

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            getPlaylistByIdUseCase(playlistId)
                .catch { e ->
                    _state.value = PlaylistScreenState.Error
                    Timber.tag(PLAYLIST).e("Load playlist error: $e")
                }
                .collect { playlist ->
                    playlist?.let {
                        initPlaylist(it)
                        _state.value = PlaylistScreenState.Content(it)
                    } ?: run {
                        _state.value = PlaylistScreenState.Error
                    }
                }
        }
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            sharePlaylistUseCase(currentPlaylist)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            deletePlaylistUseCase(currentPlaylist.playlistId, currentPlaylist.coverFilePath)
                .onSuccess {
                    _state.value = PlaylistScreenState.Deleted
                }
                .onFailure { e ->
                    _state.value = PlaylistScreenState.Error
                    Timber.tag(PLAYLIST).e("Delete playlist error: $e")
                }
        }
    }

    fun removeFromPlaylist(trackId: Int) {
        viewModelScope.launch {
            removeTrackFromPlaylistUseCase(currentPlaylist.playlistId, trackId)
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
            clickDebounced(Unit)
        }
    }
}

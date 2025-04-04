package com.practicum.playlistmaker.player.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.practicum.playlistmaker.common.constants.AppConstants.CLICK_DEBOUNCE_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.PROGRESS_BAR_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags.CLICK_DEBOUNCE
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.common.utils.debounce
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToFavoritesUseCase
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsNotContainingTrackUseCase
import com.practicum.playlistmaker.media.domain.usecase.IsTrackFavoriteUseCase
import com.practicum.playlistmaker.media.domain.usecase.IsTrackInAnyPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromFavoritesUseCase
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.state.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.state.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val addToFavoritesUseCase: AddTrackToFavoritesUseCase,
    private val removeFromFavoritesUseCase: RemoveTrackFromFavoritesUseCase,
    private val isFavoriteUseCase: IsTrackFavoriteUseCase,
    private val addToPlaylistUseCase: AddTrackToPlaylistUseCase,
    private val getPlaylistsUseCase: GetPlaylistsNotContainingTrackUseCase,
    private val isTrackInAnyPlaylistUseCase: IsTrackInAnyPlaylistUseCase,
) : ViewModel() {
    private val _state = MutableLiveData(PlayerState())
    val state: LiveData<PlayerState> get() = _state

    private var currentTrack: Track? = null

    init {
        setupPlayerListener()
    }

    private var isPlayerReady = false
    private var lastTrackPosition = 0L

    private var updateJob: Job? = null
    private val updatePositionDebounced = debounce<String>(
        delayMillis = PROGRESS_BAR_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = true,
    ) { position ->
        updateState { it.copy(currentPosition = position) }
    }

    private val isClickAllowed = AtomicBoolean(true)
    private val clickDebounced = debounce<Unit>(
        delayMillis = CLICK_DEBOUNCE_DELAY_MILLIS,
        coroutineScope = viewModelScope,
        useLastParam = false,
    ) {
        isClickAllowed.set(true)
    }

    private fun preparePlayer(track: Track) {
        playerInteractor.preparePlayer(track, {
            isPlayerReady = true
            updateState { it.copy(screenState = PlayerScreenState.Ready) }
        }, { error ->
            updateState { it.copy(screenState = PlayerScreenState.NotReady(error)) }
        })
    }

    fun controlPlayer() {
        if (isPlaying()) {
            pauseTrack()
        } else {
            if (getPlaybackState() == Player.STATE_ENDED) playerInteractor.seekTo(0)
            playTrack()
        }
    }

    private fun playTrack() {
        if (isPlayerReady) {
            playerInteractor.seekTo(lastTrackPosition)
            playerInteractor.play()
            updateState { it.copy(screenState = PlayerScreenState.Playing) }
            startUpdatingDuration()
        }
    }

    fun pauseTrack() {
        playerInteractor.pause()
        updateState { it.copy(screenState = PlayerScreenState.Paused) }
        lastTrackPosition = playerInteractor.getCurrentPosition()
        stopUpdatingDuration()
    }

    private fun stopTrack() {
        playerInteractor.stop()
        updateState { it.copy(screenState = PlayerScreenState.Stopped) }
        stopUpdatingDuration()
    }

    private fun getCurrentPosition(): String {
        return formatDurationToMMSS(playerInteractor.getCurrentPosition())
    }

    fun getTrack(trackParcel: TrackParcel) {
        val trackValue = trackParcel.let { TrackMapperImpl.toDomain(it) }
        currentTrack = trackValue
        updateState { it.copy(track = trackValue) }
        preparePlayer(trackValue)
        checkIfTrackIsFavorite(trackValue.trackId)
        checkIfTrackInAnyPlaylist(trackValue.trackId)
        loadAvailablePlaylists(trackValue.trackId)
    }

    private fun checkIfTrackIsFavorite(trackId: Int) {
        viewModelScope.launch {
            isFavoriteUseCase(trackId).collect { isFavorite ->
                updateState { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    private fun checkIfTrackInAnyPlaylist(trackId: Int) {
        viewModelScope.launch {
            isTrackInAnyPlaylistUseCase(trackId).collect { isInPlaylist ->
                updateState { it.copy(isInPlaylist = isInPlaylist) }
            }
        }
    }

    private fun loadAvailablePlaylists(trackId: Int) {
        viewModelScope.launch {
            getPlaylistsUseCase(trackId, onlyNonEmpty = false)
                .collect { playlists ->
                    updateState { it.copy(availablePlaylists = playlists) }
                }
        }
    }

    private fun isPlaying(): Boolean {
        return playerInteractor.isPlaying()
    }

    private fun getPlaybackState(): Int {
        return playerInteractor.getPlaybackState()
    }

    private fun setupPlayerListener() {
        playerInteractor.addPlayerListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        // Обработка состояния, когда плеер не готов к воспроизведению.
                    }

                    Player.STATE_BUFFERING -> {
                        // Обработка состояния буферизации (можно показать индикатор загрузки).
                    }

                    Player.STATE_READY -> {
                        isPlayerReady = true
                        if (playerInteractor.isPlaying()) {
                            updateState { it.copy(screenState = PlayerScreenState.Playing) }
                        } else {
                            updateState { it.copy(screenState = PlayerScreenState.Paused) }
                        }
                    }

                    Player.STATE_ENDED -> {
                        playerInteractor.seekTo(0)
                        playerInteractor.pause()
                        lastTrackPosition = playerInteractor.getCurrentPosition()
                        updateState { it.copy(screenState = PlayerScreenState.Stopped) }
                    }
                }
            }
        })
    }

    private fun releasePlayer() {
        stopTrack()
        playerInteractor.release()
    }

    private fun startUpdatingDuration() {
        updateJob = viewModelScope.launch {
            while (true) {
                val currentPosition = getCurrentPosition()
                updatePositionDebounced(currentPosition)
                delay(PROGRESS_BAR_DELAY_MILLIS)
            }
        }
    }

    private fun stopUpdatingDuration() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun updateState(block: (PlayerState) -> PlayerState) {
        val currentState = _state.value ?: PlayerState()
        _state.value = block(currentState)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val isFavorite = currentTrack?.trackId?.let { isFavoriteUseCase(it).first() } == true
            if (isFavorite) {
                currentTrack?.trackId?.let { removeFromFavoritesUseCase(it) }
            } else {
                currentTrack?.let { addToFavoritesUseCase(it) }
            }
        }
    }

    fun addToPlaylist(playlistId: Long) {
        if (isClickAllowed.compareAndSet(true, false)) {
            viewModelScope.launch {
                try {
                    currentTrack?.let {
                        addToPlaylistUseCase(playlistId, it)
                        // Можно обновить состояние или показать уведомление об успешном добавлении
                    }
                } catch (e: Exception) {
                    Log.e(CLICK_DEBOUNCE, "Add to playlist error: $e")
                }
            }
            clickDebounced(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }
}

package com.practicum.playlistmaker.player.presentation.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.Player
import com.practicum.playlistmaker.common.constants.AppConstants.PROGRESS_BAR_DELAY_MILLIS
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.state.PlayerScreenState

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> get() = _screenState

    private val _currentPosition = MutableLiveData<String>()
    val currentPosition: LiveData<String> get() = _currentPosition

    init {
        setupPlayerListener()
    }

    private var isPlayerReady = false
    private var lastTrackPosition = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            _currentPosition.value = getCurrentPosition()
            handler.postDelayed(this, PROGRESS_BAR_DELAY_MILLIS)
        }
    }

    fun preparePlayer(track: Track) {
        playerInteractor.preparePlayer(track, {
            isPlayerReady = true
            _screenState.value = PlayerScreenState.Ready
        }, {
            _screenState.value = PlayerScreenState.NotReady
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

    fun playTrack() {
        if (isPlayerReady) {
            playerInteractor.seekTo(lastTrackPosition)
            playerInteractor.play()
            _screenState.value = PlayerScreenState.Playing
            startUpdatingDuration()
        }
    }

    fun pauseTrack() {
        playerInteractor.pause()
        _screenState.value = PlayerScreenState.Paused
        lastTrackPosition = playerInteractor.getCurrentPosition()
        stopUpdatingDuration()
    }

    fun stopTrack() {
        playerInteractor.stop()
        _screenState.value = PlayerScreenState.Stopped
        stopUpdatingDuration()
    }

    private fun getCurrentPosition(): String {
        return formatDurationToMMSS(playerInteractor.getCurrentPosition())
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
                        // Здесь можно обработать состояние, когда плеер не готов к воспроизведению
                    }

                    Player.STATE_BUFFERING -> {
                        // Здесь можно обработать состояние буферизации
                        // Например, можно показать индикатор загрузки
                    }

                    Player.STATE_READY -> {
                        isPlayerReady = true
                        if (playerInteractor.isPlaying()) {
                            _screenState.value = PlayerScreenState.Playing
                        } else {
                            _screenState.value = PlayerScreenState.Paused
                        }
                    }

                    Player.STATE_ENDED -> {
                        playerInteractor.seekTo(0)
                        playerInteractor.pause()
                        lastTrackPosition = playerInteractor.getCurrentPosition()
                        _screenState.value = PlayerScreenState.Stopped
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
        handler.post(updateRunnable)
    }

    private fun stopUpdatingDuration() {
        handler.removeCallbacks(updateRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playerInteractor = AppDependencyCreator.providePlayerInteractor()
                PlayerViewModel(playerInteractor)
            }
        }
    }
}

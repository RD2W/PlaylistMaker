package com.practicum.playlistmaker.player.domain.interactor

import androidx.media3.common.Player
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.player.domain.model.ErrorType

interface PlayerInteractor {
    fun preparePlayer(track: Track, onPrepared: () -> Unit, onError: (ErrorType) -> Unit)
    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seekTo(position: Long)
    fun getCurrentPosition(): Long
    fun getPlaybackState(): Int
    fun isPlaying(): Boolean
    fun addPlayerListener(listener: Player.Listener)
}

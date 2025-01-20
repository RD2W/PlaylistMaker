package com.practicum.playlistmaker.player.domain.repository

import androidx.media3.common.Player
import com.practicum.playlistmaker.common.domain.model.Track

interface PlayerRepository {
    fun preparePlayer(track: Track, onPrepared: () -> Unit, onError: () -> Unit)
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
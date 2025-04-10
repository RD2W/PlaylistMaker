package com.practicum.playlistmaker.player.domain.interactor.impl

import androidx.media3.common.Player
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.model.ErrorType
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onError: (ErrorType) -> Unit) {
        playerRepository.preparePlayer(track, onPrepared, onError)
    }

    override fun play() {
        playerRepository.play()
    }

    override fun pause() {
        playerRepository.pause()
    }

    override fun stop() {
        playerRepository.stop()
    }

    override fun release() {
        playerRepository.release()
    }

    override fun seekTo(position: Long) {
        playerRepository.seekTo(position)
    }

    override fun getCurrentPosition(): Long {
        return playerRepository.getCurrentPosition()
    }

    override fun getPlaybackState(): Int {
        return playerRepository.getPlaybackState()
    }

    override fun isPlaying(): Boolean {
        return playerRepository.isPlaying()
    }

    override fun addPlayerListener(listener: Player.Listener) {
        playerRepository.addPlayerListener(listener)
    }
}

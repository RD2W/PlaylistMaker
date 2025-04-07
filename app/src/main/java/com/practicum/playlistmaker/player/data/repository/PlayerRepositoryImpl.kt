package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.practicum.playlistmaker.common.constants.LogTags.EXO_PLAYER
import com.practicum.playlistmaker.common.constants.LogTags.NETWORK_UTILS
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.NetworkUtils
import com.practicum.playlistmaker.player.domain.model.ErrorType
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import timber.log.Timber

class PlayerRepositoryImpl(
    private val context: Context,
    private val exoPlayer: ExoPlayer,
) : PlayerRepository {
    override fun preparePlayer(
        track: Track,
        onPrepared: () -> Unit,
        onError: (ErrorType) -> Unit,
    ) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            Timber.tag(NETWORK_UTILS).e("No internet connection")
            onError(ErrorType.NoInternet)
            return
        }
        try {
            val mediaItem = MediaItem.fromUri(Uri.parse(track.previewUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            onPrepared()
        } catch (e: Exception) {
            Timber.tag(EXO_PLAYER).e(e, "Exception during preparePlayer: ${e.message}")
            onError(ErrorType.PlayerException)
        }
    }

    override fun play() {
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun release() {
        exoPlayer.release()
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    override fun getCurrentPosition(): Long {
        return exoPlayer.currentPosition
    }

    override fun getPlaybackState(): Int {
        return exoPlayer.playbackState
    }

    override fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    override fun addPlayerListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }
}

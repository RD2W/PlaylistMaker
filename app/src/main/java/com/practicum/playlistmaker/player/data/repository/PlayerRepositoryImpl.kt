package com.practicum.playlistmaker.player.data.repository

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.practicum.playlistmaker.common.utils.NetworkUtils
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerRepositoryImpl(
    private val context: Context,
    private val exoPlayer: ExoPlayer,
) : PlayerRepository {

    override fun preparePlayer(track: Track, onPrepared: () -> Unit, onError: () -> Unit) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            onError()
            return
        }

        (context as? AppCompatActivity)?.lifecycleScope?.launch(Dispatchers.IO) {
            val mediaItem = MediaItem.fromUri(Uri.parse(track.previewUrl))
            withContext(Dispatchers.Main) {
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                onPrepared()
            }
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
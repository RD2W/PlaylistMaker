package com.practicum.playlistmaker.player.presentation.view

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.NOT_AVAILABLE
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.utils.formatDateToYear
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.data.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var exoPlayer: ExoPlayer

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDuration()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupPlayerListener()
    }

    private fun updateDuration() {
        val currentPosition = exoPlayer.currentPosition
        binding.playerListenedTrackTime.text = formatDurationToMMSS(currentPosition)
    }

    private fun setupPlayer(track: Track) {
        exoPlayer = ExoPlayer.Builder(this).build()

        lifecycleScope.launch(Dispatchers.IO) {
            val mediaItem = MediaItem.fromUri(Uri.parse(track.previewUrl))
            withContext(Dispatchers.Main) {
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
        }
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
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
                        if (exoPlayer.isPlaying) {
                            binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
                        } else {
                            binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                        }
                    }

                    Player.STATE_ENDED -> {
                        binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                        exoPlayer.seekTo(0)
                        exoPlayer.pause()
                    }
                }
            }
        })
    }

    private fun playTrack() {
        exoPlayer.play()
        binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
    }

    private fun pauseTrack() {
        exoPlayer.pause()
        binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
    }

    private fun setupUI() {
        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_SHARE_KEY, Track::class.java)
        } else {
            intent.getParcelableExtra(TRACK_SHARE_KEY)
        }

        if (track != null) {
            with(binding) {
                playerTrackName.text = track.trackName
                playerArtistName.text = track.artistName
                playerTrackDuration.text =
                    track.trackTime?.let { formatDurationToMMSS(it) } ?: NOT_AVAILABLE
                playerTrackYear.text =
                    track.releaseDate?.let { formatDateToYear(it) } ?: NOT_AVAILABLE
                playerTrackGenre.text = track.primaryGenreName
                playerTrackCountry.text = track.country

                if (track.collectionName.isNullOrBlank()) {
                    groupCollectionName.visibility = View.GONE
                } else playerTrackAlbum.text = track.collectionName

                Glide.with(playerTrackCover.context)
                    .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                    .placeholder(R.drawable.ic_track_placeholder)
                    .fitCenter()
                    .centerCrop()
                    .into(playerTrackCover)
            }
            setupPlayer(track)
        }
        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            playerPlayButton.setOnClickListener {
                if (exoPlayer.isPlaying) {
                    pauseTrack()
                } else {
                    if (exoPlayer.playbackState == Player.STATE_ENDED) exoPlayer.seekTo(0)
                    playTrack()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        handler.post(updateRunnable)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
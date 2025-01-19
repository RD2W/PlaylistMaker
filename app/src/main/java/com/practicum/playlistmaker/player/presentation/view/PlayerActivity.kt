package com.practicum.playlistmaker.player.presentation.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.PROGRESS_BAR_DELAY_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val playerInteractor: PlayerInteractor by lazy {
        AppDependencyCreator.providePlayerInteractor()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateDuration()
            handler.postDelayed(this, PROGRESS_BAR_DELAY_MILLIS)
        }
    }

    private var isPlayerReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupPlayerListener()
    }

    private fun updateDuration() {
        val currentPosition = playerInteractor.getCurrentPosition()
        binding.playerListenedTrackTime.text = formatDurationToMMSS(currentPosition)
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
                            binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
                        } else {
                            binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                        }
                    }

                    Player.STATE_ENDED -> {
                        binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                        playerInteractor.seekTo(0)
                        playerInteractor.pause()
                    }
                }
            }
        })
    }

        private fun preparePlayer(track: Track) {
        playerInteractor.preparePlayer(track, {
            isPlayerReady = true
        }, {
            Toast.makeText(
                this,
                getString(R.string.player_no_internet_connection_toast),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun playTrack() {
        playerInteractor.play()
        binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
    }

    private fun pauseTrack() {
        playerInteractor.pause()
        binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
    }

    private fun stopTrack() {
        playerInteractor.stop()
        binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
    }

    private fun setupUI() {
        val trackParcel: TrackParcel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_SHARE_KEY, TrackParcel::class.java)
        } else {
            intent.getParcelableExtra(TRACK_SHARE_KEY)
        }

        if (trackParcel != null) {
            val track = TrackMapperImpl.toDomain(trackParcel)
            with(binding) {
                playerTrackName.text = track.trackName
                playerArtistName.text = track.artistName
                playerTrackDuration.text = track.trackTime
                playerTrackYear.text = track.releaseDate
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
            preparePlayer(track)
        }
        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            playerPlayButton.setOnClickListener {
                if (!isPlayerReady) return@setOnClickListener // Если плеер не готов, ничего не делаем
                if (playerInteractor.isPlaying()) {
                    pauseTrack()
                } else {
                    if (playerInteractor.getPlaybackState() == Player.STATE_ENDED) playerInteractor.seekTo(
                        0
                    )
                    playTrack()
                }
            }
        }
    }

    private fun startUpdatingDuration() {
        handler.post(updateRunnable)
    }

    private fun stopUpdatingDuration() {
        handler.removeCallbacks(updateRunnable)
    }

    override fun onStart() {
        super.onStart()
        startUpdatingDuration()
    }

    override fun onStop() {
        super.onStop()
        pauseTrack()
        stopUpdatingDuration()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTrack()
        playerInteractor.release()
    }
}
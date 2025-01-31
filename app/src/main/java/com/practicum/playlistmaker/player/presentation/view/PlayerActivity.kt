package com.practicum.playlistmaker.player.presentation.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.START_TRACK_POSITION
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.di.AppDependencyCreator
import com.practicum.playlistmaker.common.domain.mapper.impl.TrackMapperImpl
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.common.presentation.model.TrackParcel
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.presentation.state.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModelFactory
import kotlin.getValue

class PlayerActivity : AppCompatActivity() {

    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding
        get() = requireNotNull(_binding) { "Binding wasn't initiliazed!" }

    private val playerInteractor: PlayerInteractor by lazy {
        AppDependencyCreator.providePlayerInteractor()
    }

    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(playerInteractor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModel.screenState.observe(this) { state ->
            when (state) {
                is PlayerScreenState.Ready -> {
                    binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                    binding.playerPlayButton.isEnabled = true // Кнопка кликабельна
                }

                is PlayerScreenState.NotReady -> {
                    binding.playerPlayButton.isEnabled = false // Кнопка не кликабельна
                    Toast.makeText(
                        this,
                        getString(R.string.player_no_internet_connection_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PlayerScreenState.Playing -> {
                    binding.playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
                    binding.playerPlayButton.isEnabled = true
                }

                is PlayerScreenState.Paused -> {
                    binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                    binding.playerPlayButton.isEnabled = true
                }

                is PlayerScreenState.Stopped -> {
                    binding.playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
                    binding.playerPlayButton.isEnabled = true
                }
            }
        }
    }

    private fun observeCurrentPosition() {
        viewModel.currentPosition.observe(this) { position ->
            binding.playerListenedTrackTime.text = position
        }
    }

    private fun setupUI() {
        val track = getTrack()
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

            playerListenedTrackTime.text = START_TRACK_POSITION

            Glide.with(playerTrackCover.context)
                .load(track.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
                .placeholder(R.drawable.ic_track_placeholder)
                .fitCenter()
                .centerCrop()
                .into(playerTrackCover)
        }
        viewModel.preparePlayer(track)
        observeCurrentPosition()
        setupButtons()
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            playerPlayButton.setOnClickListener {
                viewModel.controlPlayer()
            }
        }
    }

    private fun getTrack(): Track {
        val trackParcel: TrackParcel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_SHARE_KEY, TrackParcel::class.java)
        } else {
            intent.getParcelableExtra(TRACK_SHARE_KEY)
        }
        return trackParcel?.let { TrackMapperImpl.toDomain(it) }
            ?: throw IllegalArgumentException("TrackParcel is null")
    }

    override fun onStop() {
        super.onStop()
        viewModel.pauseTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
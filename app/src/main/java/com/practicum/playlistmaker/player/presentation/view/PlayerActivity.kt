package com.practicum.playlistmaker.player.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.domain.model.ErrorType
import com.practicum.playlistmaker.player.presentation.state.PlayerScreenState
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerActivity : AppCompatActivity() {
    private var _binding: ActivityPlayerBinding? = null
    private val binding: ActivityPlayerBinding
        get() = requireNotNull(_binding) { "Binding wasn't initialized!" }
    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getTrack(intent)
        observePlayerState()
        setupButtons()
    }

    private fun observePlayerState() {
        viewModel.state.observe(this) { state ->
            state.track?.let { track ->
                updateUIWithTrack(track)
            }
            state.screenState.let { screenState ->
                when (screenState) {
                    is PlayerScreenState.NotReady -> {
                        disablePlayButton()
                        showToast(screenState)
                    }
                    is PlayerScreenState.Ready -> showPlayButton()
                    is PlayerScreenState.Playing -> showPauseButton()
                    is PlayerScreenState.Paused -> showPlayButton()
                    is PlayerScreenState.Stopped -> showPlayButton()
                }
            }
            binding.playerListenedTrackTime.text = state.currentPosition
        }
    }

    private fun updateUIWithTrack(track: Track) {
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
    }

    private fun showPlayButton() {
        with(binding) {
            playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
            playerPlayButton.isEnabled = true
        }
    }

    private fun showPauseButton() {
        with(binding) {
            playerPlayButton.setImageResource(R.drawable.ic_player_pause_button)
            playerPlayButton.isEnabled = true
        }
    }

    private fun disablePlayButton() {
        with(binding) {
            playerPlayButton.setImageResource(R.drawable.ic_player_play_button)
            playerPlayButton.isEnabled = false
        }
    }

    private fun showToast(state: PlayerScreenState.NotReady) {
        val errorMessage = when (state.error) {
            is ErrorType.NoInternet -> getString(R.string.player_no_internet_connection_toast)
            is ErrorType.PlayerException -> getString(R.string.player_error_exception_toast)
            is ErrorType.Unknown -> getString(R.string.player_error_unknown_toast)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun setupButtons() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
//                onBackPressedDispatcher.onBackPressed()
                finish()
            }
            playerPlayButton.setOnClickListener {
                viewModel.controlPlayer()
            }
        }
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
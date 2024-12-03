package com.practicum.playlistmaker.player.presentation.view

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.NOT_AVAILABLE
import com.practicum.playlistmaker.common.constants.AppConstants.TRACK_SHARE_KEY
import com.practicum.playlistmaker.common.utils.formatDateToYear
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.data.model.Track

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupUI() {
        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_SHARE_KEY, Track::class.java)
        } else {
            intent.getParcelableExtra(TRACK_SHARE_KEY)
        }

        with (binding) {
            playerTrackName.text = track?.trackName
            playerArtistName.text = track?.artistName
            playerTrackDuration.text = track?.trackTime?.let { formatDurationToMMSS(it) } ?: NOT_AVAILABLE
            playerTrackAlbum.text = track?.collectionName ?: NOT_AVAILABLE
            playerTrackYear.text = track?.releaseDate?.let { formatDateToYear(it) } ?: NOT_AVAILABLE
            playerTrackGenre.text = track?.primaryGenreName
            playerTrackCountry.text = track?.country
        }

        Glide.with(binding.playerTrackCover.context)
            .load(track?.artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .into(binding.playerTrackCover)
    }

}
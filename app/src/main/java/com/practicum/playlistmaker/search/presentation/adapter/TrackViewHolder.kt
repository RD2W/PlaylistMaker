package com.practicum.playlistmaker.search.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.data.model.Track

class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {

        with (binding) {
            foundTrackName.text = track.trackName
            foundTrackDetails.text = String.format("%s • %s", track.artistName, formatDurationToMMSS(track.trackTime))
        }

        Glide.with(binding.foundTrackCover.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .into(binding.foundTrackCover)
    }
}
package com.practicum.playlistmaker.media.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.TrackItemBinding

class FavoriteTrackViewHolder(private val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {
        with(binding) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = trackTime.context.getString(R.string.bullet_prefix, track.trackTime)
        }

        Glide.with(binding.trackCover.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .into(binding.trackCover)
    }
}

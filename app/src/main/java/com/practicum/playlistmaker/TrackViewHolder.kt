package com.practicum.playlistmaker

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.databinding.TrackItemBinding

class TrackViewHolder(private val binding: TrackItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track) {

        binding.apply {
            foundTrackName.text = track.trackName
            foundTrackDetails.text = String.format("%s • %s", track.artistName, track.trackTime)
        }

        Glide.with(binding.foundTrackCover.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .into(binding.foundTrackCover)
    }
}
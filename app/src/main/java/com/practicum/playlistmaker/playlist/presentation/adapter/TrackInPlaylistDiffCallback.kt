package com.practicum.playlistmaker.playlist.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.common.domain.model.Track

class TrackInPlaylistDiffCallback : DiffUtil.ItemCallback<Track>() {
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.trackId == newItem.trackId
    }

    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}
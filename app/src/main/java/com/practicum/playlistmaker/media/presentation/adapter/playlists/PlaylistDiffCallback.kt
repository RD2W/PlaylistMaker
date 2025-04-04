package com.practicum.playlistmaker.media.presentation.adapter.playlists

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.common.domain.model.Playlist

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.playlistId == newItem.playlistId
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}

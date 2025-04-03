package com.practicum.playlistmaker.player.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.databinding.ItemPlaylistLinearBinding

class PlaylistLinearAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : ListAdapter<Playlist, PlaylistLinearViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistLinearViewHolder {
        val binding = ItemPlaylistLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistLinearViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistLinearViewHolder, position: Int) {
        val playlist = getItem(position)
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onPlaylistClick(playlist)
        }
    }
}
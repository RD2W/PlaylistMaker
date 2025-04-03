package com.practicum.playlistmaker.player.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.databinding.ItemPlaylistLinearBinding

class PlaylistLinearViewHolder(
    private val binding: ItemPlaylistLinearBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {

        with(binding) {
            tvPlaylistName.text = playlist.name
            tvPlaylistCount.text = root.context.resources.getQuantityString(
                R.plurals.tracks_plurals,
                playlist.trackCount,      // Число для выбора формы
                playlist.trackCount       // Число, которое подставляется в строку
            )
        }

        Glide.with(binding.ivPlaylistCover.context)
            .load(playlist.coverFilePath)
            .placeholder(R.drawable.ic_track_placeholder)
            .fitCenter()
            .centerCrop()
            .into(binding.ivPlaylistCover)
    }
}

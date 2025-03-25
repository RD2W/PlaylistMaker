package com.practicum.playlistmaker.media.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.TrackItemBinding

class FavoriteTrackAdapter(
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, FavoriteTrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteTrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteTrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteTrackViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
        holder.itemView.setOnClickListener {
            onTrackClick(track)
        }
    }
}
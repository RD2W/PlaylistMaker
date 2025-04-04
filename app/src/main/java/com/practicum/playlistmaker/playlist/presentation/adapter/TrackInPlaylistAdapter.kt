package com.practicum.playlistmaker.playlist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.databinding.TrackItemBinding

class TrackInPlaylistAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit,
) : ListAdapter<Track, TrackInPlaylistViewHolder>(TrackInPlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackInPlaylistViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackInPlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackInPlaylistViewHolder, position: Int) {
        val track = getItem(position)
        with(holder) {
            bind(track)

            itemView.setOnClickListener {
                onTrackClick(track)
            }

            itemView.setOnLongClickListener {
                onTrackLongClick(track)
                true // Возвращаем true, чтобы показать, что событие обработано
            }
        }
    }
}

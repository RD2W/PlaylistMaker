package com.practicum.playlistmaker.search.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.data.model.Track

class TrackAdapter(
    private val tracks: MutableList<Track>,
    private val onTrackClick: (Track) -> Unit // Лямбда для обработки клика по треку
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track) // Используем метод bind из TrackViewHolder
        holder.itemView.setOnClickListener { // Установка обработчика клика
            onTrackClick(track)
        }
    }

    override fun getItemCount() = tracks.size
}

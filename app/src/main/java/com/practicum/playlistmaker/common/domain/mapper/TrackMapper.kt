package com.practicum.playlistmaker.common.domain.mapper

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel

interface TrackMapper {
    fun toParcel(track: Track): TrackParcel
    fun toDomain(trackParcel: TrackParcel): Track
}
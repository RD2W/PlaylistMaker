package com.practicum.playlistmaker.common.domain.mapper.impl

import com.practicum.playlistmaker.common.domain.mapper.TrackMapper
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.presentation.model.TrackParcel

object TrackMapperImpl : TrackMapper {
    override fun toParcel(track: Track) = TrackParcel(
        trackId = track.trackId,
        trackName = track.trackName,
        artistName = track.artistName,
        trackTime = track.trackTime,
        trackTimeMillis = track.trackTimeMillis,
        artworkUrl100 = track.artworkUrl100,
        collectionName = track.collectionName,
        releaseDate = track.releaseDate,
        primaryGenreName = track.primaryGenreName,
        country = track.country,
        previewUrl = track.previewUrl,
    )

    override fun toDomain(trackParcel: TrackParcel) = Track(
        trackId = trackParcel.trackId,
        trackName = trackParcel.trackName,
        artistName = trackParcel.artistName,
        trackTime = trackParcel.trackTime,
        trackTimeMillis = trackParcel.trackTimeMillis,
        artworkUrl100 = trackParcel.artworkUrl100,
        collectionName = trackParcel.collectionName,
        releaseDate = trackParcel.releaseDate,
        primaryGenreName = trackParcel.primaryGenreName,
        country = trackParcel.country,
        previewUrl = trackParcel.previewUrl,
    )
}

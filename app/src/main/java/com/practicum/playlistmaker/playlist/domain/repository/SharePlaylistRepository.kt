package com.practicum.playlistmaker.playlist.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track

interface SharePlaylistRepository {
    fun setPlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        tracksCount: Int,
        tracksList: List<Track>,
    )
}
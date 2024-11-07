package com.practicum.playlistmaker.search.data.model

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)

package com.practicum.playlistmaker.common.domain.model

import com.practicum.playlistmaker.common.utils.getCurrentYearAsString

data class Playlist(
    val playlistId: Long = 0L,
    val name: String,
    val description: String = "",
    val creationDate: String = getCurrentYearAsString(),
    val coverFilePath: String?,
    val trackCount: Int = 0,
    val totalDurationMillis: Long = 0L,
    val trackList: List<Track> = emptyList(),
)
package com.practicum.playlistmaker.common.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackParcel(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    val trackTime: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
) : Parcelable

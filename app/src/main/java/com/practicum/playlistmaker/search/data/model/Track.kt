package com.practicum.playlistmaker.search.data.model

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String
)

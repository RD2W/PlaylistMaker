package com.practicum.playlistmaker.media.data.source.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Int,

    @ColumnInfo(name = "track_name")
    val trackName: String,

    @ColumnInfo(name = "artist_name")
    val artistName: String,

    @ColumnInfo(name = "track_time")
    val trackTime: String,

    @ColumnInfo(name = "track_time_millis")
    val trackTimeMillis: Long,

    @ColumnInfo(name = "artwork_url_100")
    val artworkUrl100: String,

    @ColumnInfo(name = "collection_name")
    val collectionName: String,

    @ColumnInfo(name = "release_date")
    val releaseDate: String,

    @ColumnInfo(name = "primary_genre_name")
    val primaryGenreName: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "preview_url")
    val previewUrl: String,
)

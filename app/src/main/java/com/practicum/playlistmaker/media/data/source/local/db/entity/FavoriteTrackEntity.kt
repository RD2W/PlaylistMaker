package com.practicum.playlistmaker.media.data.source.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: Int,

    @ColumnInfo(name = "added_timestamp")
    val addedTimestamp: Long = System.currentTimeMillis(),
)

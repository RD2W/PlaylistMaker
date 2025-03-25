package com.practicum.playlistmaker.media.data.source.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class PlaylistInfoEntity(
    @Embedded
    val playlist: PlaylistEntity,

    @ColumnInfo(name = "track_count")
    val trackCount: Int,

    @ColumnInfo(name = "total_duration")
    val totalDurationMillis: Long?,
)
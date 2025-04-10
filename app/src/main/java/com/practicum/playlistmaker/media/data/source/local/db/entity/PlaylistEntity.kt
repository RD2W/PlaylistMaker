package com.practicum.playlistmaker.media.data.source.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "creation_date")
    val creationDate: String,

    @ColumnInfo(name = "cover_file_path")
    val coverFilePath: String?,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "edited_at")
    val editedAt: Long = 0L,
)

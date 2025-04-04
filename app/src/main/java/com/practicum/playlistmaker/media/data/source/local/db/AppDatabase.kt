package com.practicum.playlistmaker.media.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.media.data.source.local.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.PlaylistTrackCrossRefDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.PlaylistsDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.TracksDao
import com.practicum.playlistmaker.media.data.source.local.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistTrackCrossRef
import com.practicum.playlistmaker.media.data.source.local.db.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackDao(): TracksDao
    abstract fun favoriteTrackDao(): FavoriteTracksDao
    abstract fun playlistDao(): PlaylistsDao
    abstract fun playlistTrackDao(): PlaylistTrackCrossRefDao
}

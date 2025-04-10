package com.practicum.playlistmaker.media.data.mapper

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.data.source.local.db.entity.FavoriteTrackEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistInfoEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.TrackEntity

fun Track.toFavoriteTrackEntity() = FavoriteTrackEntity(
    trackId = this.trackId,
    addedTimestamp = System.currentTimeMillis(),
)

fun Track.toTrackEntity() = TrackEntity(
    trackId = this.trackId,
    trackName = this.trackName.orEmpty(),
    artistName = this.artistName.orEmpty(),
    trackTime = this.trackTime.orEmpty(),
    trackTimeMillis = this.trackTimeMillis,
    artworkUrl100 = this.artworkUrl100.orEmpty(),
    collectionName = this.collectionName.orEmpty(),
    releaseDate = this.releaseDate.orEmpty(),
    primaryGenreName = this.primaryGenreName.orEmpty(),
    country = this.country.orEmpty(),
    previewUrl = this.previewUrl.orEmpty(),
)

fun TrackEntity.toTrack() = Track(
    trackId = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = this.trackTime,
    trackTimeMillis = this.trackTimeMillis,
    artworkUrl100 = this.artworkUrl100,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    primaryGenreName = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl,
)

fun List<TrackEntity>.toTrackList(): List<Track> {
    return this.map { trackEntity -> trackEntity.toTrack() }
}

fun Playlist.toPlaylistEntity() = PlaylistEntity(
    name = this.name,
    description = this.description,
    creationDate = this.creationDate,
    coverFilePath = this.coverFilePath,
)

fun PlaylistInfoEntity.toPlaylist() = Playlist(
    playlistId = playlist.playlistId,
    name = playlist.name,
    description = playlist.description,
    creationDate = playlist.creationDate,
    coverFilePath = playlist.coverFilePath,
    totalDurationMillis = totalDurationMillis ?: 0L,
    trackCount = trackCount,
    trackList = emptyList(),
)

fun PlaylistEntity.toPlaylist(tracks: List<TrackEntity>): Playlist {
    val trackList = tracks.map { it.toTrack() }
    return Playlist(
        playlistId = this.playlistId,
        name = this.name,
        description = this.description,
        creationDate = this.creationDate,
        coverFilePath = this.coverFilePath,
        totalDurationMillis = trackList.sumOf { it.trackTimeMillis },
        trackCount = trackList.size,
        trackList = trackList,
    )
}

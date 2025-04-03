package com.practicum.playlistmaker.playlist.presentation.state

import com.practicum.playlistmaker.common.domain.model.Playlist

sealed class PlaylistScreenState {
    data object Loading : PlaylistScreenState()
    data class Content(val playlist: Playlist) : PlaylistScreenState()
    data object Error : PlaylistScreenState()
    data object Deleted : PlaylistScreenState()
}
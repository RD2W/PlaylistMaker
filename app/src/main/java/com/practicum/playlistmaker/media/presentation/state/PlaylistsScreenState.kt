package com.practicum.playlistmaker.media.presentation.state

import com.practicum.playlistmaker.common.domain.model.Playlist

sealed class PlaylistsScreenState {
    data object Loading : PlaylistsScreenState()
    data class Content(val playlists: List<Playlist>) : PlaylistsScreenState()
    data object Empty : PlaylistsScreenState()
    data object Error : PlaylistsScreenState()
}

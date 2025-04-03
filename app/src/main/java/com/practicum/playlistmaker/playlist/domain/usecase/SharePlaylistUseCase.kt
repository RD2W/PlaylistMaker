package com.practicum.playlistmaker.playlist.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Playlist

interface SharePlaylistUseCase {
    suspend operator fun invoke(playlist: Playlist)
}
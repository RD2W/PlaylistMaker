package com.practicum.playlistmaker.playlist.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.playlist.domain.repository.SharePlaylistRepository
import com.practicum.playlistmaker.playlist.domain.usecase.SharePlaylistUseCase

class SharePlaylistUseCaseImpl(
    private val sharePlaylistRepository: SharePlaylistRepository,
) : SharePlaylistUseCase {
    override suspend fun invoke(playlist: Playlist) {
        sharePlaylistRepository.setPlaylistInfo(
            playlist.name,
            playlist.description,
            playlist.trackCount,
            playlist.trackList,
        )
    }
}
package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.CreatePlaylistUseCase

class CreatePlaylistUseCaseImpl(private val repository: PlaylistsRepository) :
    CreatePlaylistUseCase {
    override suspend operator fun invoke(playlist: Playlist) = repository.createPlaylist(playlist)
}
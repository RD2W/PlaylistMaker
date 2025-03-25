package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.DeletePlaylistUseCase

class DeletePlaylistUseCaseImpl(private val repository: PlaylistsRepository) :
    DeletePlaylistUseCase {
    override suspend operator fun invoke(playlistId: Long) = repository.deletePlaylist(playlistId)
}
package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.EditPlaylistUseCase

class EditPlaylistUseCaseImpl(private val repository: PlaylistsRepository) : EditPlaylistUseCase {
    override suspend fun invoke(
        playlistId: Long,
        name: String?,
        description: String?,
        coverFilePath: String?
    ) {
        repository.editPlaylist(
            playlistId = playlistId,
            name = name,
            description = description,
            coverFilePath = coverFilePath,
        )
    }
}
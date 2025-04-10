package com.practicum.playlistmaker.listmaker.domain.usecase.impl

import com.practicum.playlistmaker.listmaker.domain.repository.PlaylistCoverRepository
import com.practicum.playlistmaker.listmaker.domain.usecase.DeletePlaylistCoverUseCase

class DeletePlaylistCoverUseCaseImpl(
    private val coverRepository: PlaylistCoverRepository,
) : DeletePlaylistCoverUseCase {
    override suspend fun invoke(imagePath: String) = coverRepository.deleteCoverImage(imagePath)
}

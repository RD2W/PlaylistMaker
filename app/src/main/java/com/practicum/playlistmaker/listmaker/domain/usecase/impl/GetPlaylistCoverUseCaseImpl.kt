package com.practicum.playlistmaker.listmaker.domain.usecase.impl

import com.practicum.playlistmaker.listmaker.domain.repository.PlaylistCoverRepository
import com.practicum.playlistmaker.listmaker.domain.usecase.GetPlaylistCoverUseCase
import java.io.File

class GetPlaylistCoverUseCaseImpl(
    private val coverRepository: PlaylistCoverRepository,
) : GetPlaylistCoverUseCase {
    override suspend fun invoke(imagePath: String): File? = coverRepository.getCoverImage(imagePath)
}
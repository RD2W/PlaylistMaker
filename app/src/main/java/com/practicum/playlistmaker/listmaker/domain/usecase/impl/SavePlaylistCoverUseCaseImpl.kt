package com.practicum.playlistmaker.listmaker.domain.usecase.impl

import android.net.Uri
import com.practicum.playlistmaker.listmaker.domain.repository.PlaylistCoverRepository
import com.practicum.playlistmaker.listmaker.domain.usecase.SavePlaylistCoverUseCase

class SavePlaylistCoverUseCaseImpl(
    private val coverRepository: PlaylistCoverRepository,
) : SavePlaylistCoverUseCase {
    override suspend fun invoke(imageUri: Uri): String = coverRepository.saveCoverImage(imageUri)
}

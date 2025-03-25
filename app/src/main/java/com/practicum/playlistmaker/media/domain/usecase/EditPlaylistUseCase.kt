package com.practicum.playlistmaker.media.domain.usecase

interface EditPlaylistUseCase {
    suspend operator fun invoke(
        playlistId: Long,
        name: String? = null,
        description: String? = null,
        coverFilePath: String? = null,
    )
}
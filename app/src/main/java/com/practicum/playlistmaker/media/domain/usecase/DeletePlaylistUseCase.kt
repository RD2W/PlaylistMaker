package com.practicum.playlistmaker.media.domain.usecase

interface DeletePlaylistUseCase {
    suspend operator fun invoke(playlistId: Long, coverImagePath: String?): Result<Unit>
}

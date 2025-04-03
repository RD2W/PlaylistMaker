package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.listmaker.domain.usecase.DeletePlaylistCoverUseCase
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.DeletePlaylistUseCase

class DeletePlaylistUseCaseImpl(
    private val playlistsRepository: PlaylistsRepository,
    private val deleteCoverUseCase: DeletePlaylistCoverUseCase,
) : DeletePlaylistUseCase {
    override suspend operator fun invoke(playlistId: Long, coverImagePath: String?): Result<Unit> {
        return try {
            coverImagePath?.let { deleteCoverUseCase(it) }
            playlistsRepository.clearTracksInPlaylist(playlistId)
            playlistsRepository.deletePlaylist(playlistId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
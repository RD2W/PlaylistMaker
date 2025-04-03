package com.practicum.playlistmaker.media.domain.usecase.impl

import android.net.Uri
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.utils.getCurrentYearAsString
import com.practicum.playlistmaker.listmaker.domain.usecase.DeletePlaylistCoverUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SavePlaylistCoverUseCase
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.CreatePlaylistUseCase

class CreatePlaylistUseCaseImpl(
    private val repository: PlaylistsRepository,
    private val saveCoverUseCase: SavePlaylistCoverUseCase,
    private val deleteCoverUseCase: DeletePlaylistCoverUseCase,
) : CreatePlaylistUseCase {
    override suspend fun invoke(
        name: String,
        description: String,
        coverImageUri: Uri?,
    ): Result<Long> {
        return try {
            val coverPath = saveCoverIfNeeded(coverImageUri)
            createPlaylist(name, description, coverPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveCoverIfNeeded(coverImageUri: Uri?): String? {
        return coverImageUri?.let { saveCoverUseCase(it) }
    }

    private suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    ): Result<Long> {
        return try {
            val playlistId = repository.createPlaylist(
                Playlist(
                    name = name,
                    description = description,
                    creationDate = getCurrentYearAsString(),
                    coverFilePath = coverPath
                )
            )
            Result.success(playlistId)
        } catch (e: Exception) {
            coverPath?.let { deleteCoverUseCase(it) }
            throw e
        }
    }
}
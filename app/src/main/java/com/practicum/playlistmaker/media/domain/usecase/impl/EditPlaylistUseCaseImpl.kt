package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.constants.AppConstants.REMOVE_COVER
import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.listmaker.domain.usecase.DeletePlaylistCoverUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SavePlaylistCoverUseCase
import com.practicum.playlistmaker.media.domain.model.CoverAction
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.EditPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistByIdUseCase
import kotlinx.coroutines.flow.first

class EditPlaylistUseCaseImpl(
    private val repository: PlaylistsRepository,
    private val getPlaylistUseCase: GetPlaylistByIdUseCase,
    private val saveCoverUseCase: SavePlaylistCoverUseCase,
    private val deleteCoverUseCase: DeletePlaylistCoverUseCase,
) : EditPlaylistUseCase {
    override suspend fun invoke(
        playlistId: Long,
        name: String?,
        description: String?,
        coverAction: CoverAction,
    ): Result<Long> {
        return try {
            val currentPlaylist = getCurrentPlaylist(playlistId)

            val newCoverPath = processCoverAction(
                currentCoverPath = currentPlaylist.coverFilePath,
                coverAction = coverAction,
            )

            performEditPlaylist(
                playlistId = playlistId,
                currentName = currentPlaylist.name,
                newName = name,
                currentDescription = currentPlaylist.description,
                newDescription = description,
                newCoverPath = newCoverPath,
            )

            Result.success(playlistId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCurrentPlaylist(playlistId: Long): Playlist {
        return getPlaylistUseCase(playlistId).first()
            ?: throw IllegalArgumentException("Playlist with id $playlistId not found")
    }

    private suspend fun processCoverAction(
        currentCoverPath: String?,
        coverAction: CoverAction,
    ): String? = when (coverAction) {
        is CoverAction.Update -> {
            currentCoverPath?.let { deleteCoverUseCase(it) }
            saveCoverUseCase(coverAction.uri)
        }
        CoverAction.Remove -> {
            currentCoverPath?.let { deleteCoverUseCase(it) }
            REMOVE_COVER
        }
        CoverAction.Keep -> null // null означает оставить без изменений
    }

    private suspend fun performEditPlaylist(
        playlistId: Long,
        currentName: String,
        newName: String?,
        currentDescription: String?,
        newDescription: String?,
        newCoverPath: String?,
    ) {
        val finalName = if (newName != null && newName != currentName) newName else null
        val finalDescription = if (newDescription != null && newDescription != currentDescription) {
            newDescription
        } else {
            null
        }

        repository.editPlaylist(
            playlistId = playlistId,
            name = finalName,
            description = finalDescription,
            coverFilePath = newCoverPath,
        )
    }
}

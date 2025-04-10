package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistByIdUseCase
import kotlinx.coroutines.flow.Flow

class GetPlaylistByIdUseCaseImpl(private val repository: PlaylistsRepository) :
    GetPlaylistByIdUseCase {
    override suspend operator fun invoke(playlistId: Long): Flow<Playlist?> =
        repository.getPlaylistById(playlistId)
}

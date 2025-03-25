package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsUseCase
import kotlinx.coroutines.flow.Flow

class GetPlaylistsUseCaseImpl(private val repository: PlaylistsRepository) : GetPlaylistsUseCase {
    override operator fun invoke(): Flow<List<Playlist>> = repository.getPlaylistsInfo()
}
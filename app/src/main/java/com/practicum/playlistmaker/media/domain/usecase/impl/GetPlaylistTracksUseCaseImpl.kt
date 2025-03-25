package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistTracksUseCase
import kotlinx.coroutines.flow.Flow

class GetPlaylistTracksUseCaseImpl(private val repository: PlaylistsRepository) : GetPlaylistTracksUseCase {
    override operator fun invoke(playlistId: Long): Flow<List<Track>> = repository.getTracksInPlaylist(playlistId)
}
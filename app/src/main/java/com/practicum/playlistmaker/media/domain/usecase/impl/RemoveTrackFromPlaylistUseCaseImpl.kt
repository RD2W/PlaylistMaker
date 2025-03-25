package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromPlaylistUseCase

class RemoveTrackFromPlaylistUseCaseImpl(private val repository: PlaylistsRepository) :
    RemoveTrackFromPlaylistUseCase {
    override suspend operator fun invoke(playlistId: Long, trackId: Int) =
        repository.removeTrackFromPlaylist(playlistId, trackId)
}
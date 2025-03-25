package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToPlaylistUseCase

class AddTrackToPlaylistUseCaseImpl(private val repository: PlaylistsRepository) :
    AddTrackToPlaylistUseCase {
    override suspend operator fun invoke(playlistId: Long, track: Track) =
        repository.addTrackToPlaylist(playlistId, track)
}
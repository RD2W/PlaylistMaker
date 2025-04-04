package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.IsTrackInAnyPlaylistUseCase

class IsTrackInAnyPlaylistUseCaseImpl(private val repository: PlaylistsRepository) :
    IsTrackInAnyPlaylistUseCase {
    override operator fun invoke(trackId: Int) = repository.isTrackInAnyPlaylist(trackId)
}

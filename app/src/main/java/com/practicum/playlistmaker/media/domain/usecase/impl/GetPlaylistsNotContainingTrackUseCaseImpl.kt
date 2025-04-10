package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsNotContainingTrackUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class GetPlaylistsNotContainingTrackUseCaseImpl(
    private val playlistsRepository: PlaylistsRepository,
) : GetPlaylistsNotContainingTrackUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(
        trackId: Int,
        onlyNonEmpty: Boolean,
    ): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylistsNotContainingTrack(trackId, onlyNonEmpty)
    }
}

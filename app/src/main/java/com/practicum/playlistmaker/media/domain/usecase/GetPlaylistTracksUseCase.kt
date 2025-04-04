package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface GetPlaylistTracksUseCase {
    operator fun invoke(playlistId: Long): Flow<List<Track>>
}

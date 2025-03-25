package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface GetFavoriteTracksUseCase {
    operator fun invoke(): Flow<List<Track>>
}
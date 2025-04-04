package com.practicum.playlistmaker.media.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IsTrackFavoriteUseCase {
    operator fun invoke(trackId: Int): Flow<Boolean>
}

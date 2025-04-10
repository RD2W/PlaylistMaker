package com.practicum.playlistmaker.media.domain.usecase

import kotlinx.coroutines.flow.Flow

interface IsTrackInAnyPlaylistUseCase {
    operator fun invoke(trackId: Int): Flow<Boolean>
}

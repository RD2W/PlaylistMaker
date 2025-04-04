package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Track

interface AddTrackToFavoritesUseCase {
    suspend operator fun invoke(track: Track)
}

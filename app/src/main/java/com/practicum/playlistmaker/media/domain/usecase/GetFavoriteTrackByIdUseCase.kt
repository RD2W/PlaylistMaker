package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Track

interface GetFavoriteTrackByIdUseCase {
    suspend operator fun invoke(id: Int): Track?
}
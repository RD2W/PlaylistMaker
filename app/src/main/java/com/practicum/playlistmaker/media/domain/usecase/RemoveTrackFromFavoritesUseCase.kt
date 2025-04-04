package com.practicum.playlistmaker.media.domain.usecase

interface RemoveTrackFromFavoritesUseCase {
    suspend operator fun invoke(trackId: Int)
}

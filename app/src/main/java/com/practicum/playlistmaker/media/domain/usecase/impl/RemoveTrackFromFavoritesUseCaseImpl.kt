package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromFavoritesUseCase

class RemoveTrackFromFavoritesUseCaseImpl(private val repository: FavoriteTracksRepository) :
    RemoveTrackFromFavoritesUseCase {
    override suspend operator fun invoke(trackId: Int) = repository.removeFromFavorites(trackId)
}

package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToFavoritesUseCase

class AddTrackToFavoritesUseCaseImpl(private val repository: FavoriteTracksRepository) :
    AddTrackToFavoritesUseCase {
    override suspend operator fun invoke(track: Track) = repository.addToFavorites(track)
}

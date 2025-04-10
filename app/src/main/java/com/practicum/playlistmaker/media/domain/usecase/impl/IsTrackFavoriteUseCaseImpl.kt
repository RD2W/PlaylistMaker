package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.usecase.IsTrackFavoriteUseCase

class IsTrackFavoriteUseCaseImpl(private val repository: FavoriteTracksRepository) :
    IsTrackFavoriteUseCase {
    override operator fun invoke(trackId: Int) = repository.isTrackFavorite(trackId)
}

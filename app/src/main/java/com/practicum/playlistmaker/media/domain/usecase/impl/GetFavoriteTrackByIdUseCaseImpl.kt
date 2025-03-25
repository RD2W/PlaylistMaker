package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.usecase.GetFavoriteTrackByIdUseCase

class GetFavoriteTrackByIdUseCaseImpl(private val repository: FavoriteTracksRepository) :
    GetFavoriteTrackByIdUseCase {
    override suspend operator fun invoke(id: Int) = repository.getFavoriteTrackById(id)
}
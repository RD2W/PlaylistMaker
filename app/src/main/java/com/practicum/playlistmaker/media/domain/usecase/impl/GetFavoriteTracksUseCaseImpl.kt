package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.usecase.GetFavoriteTracksUseCase
import kotlinx.coroutines.flow.Flow

class GetFavoriteTracksUseCaseImpl(private val repository: FavoriteTracksRepository) :
    GetFavoriteTracksUseCase {
    override operator fun invoke(): Flow<List<Track>> = repository.getFavoriteTracks()
}
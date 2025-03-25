package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface GetPlaylistsUseCase {
    operator fun invoke(): Flow<List<Playlist>>
}
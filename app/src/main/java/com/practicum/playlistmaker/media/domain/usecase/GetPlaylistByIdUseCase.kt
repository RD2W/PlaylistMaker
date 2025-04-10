package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface GetPlaylistByIdUseCase {
    suspend operator fun invoke(playlistId: Long): Flow<Playlist?>
}

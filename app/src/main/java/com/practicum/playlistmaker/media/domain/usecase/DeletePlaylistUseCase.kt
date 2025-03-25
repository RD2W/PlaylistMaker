package com.practicum.playlistmaker.media.domain.usecase

interface DeletePlaylistUseCase {
    suspend operator fun invoke(playlistId: Long)
}
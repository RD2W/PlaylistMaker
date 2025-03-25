package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Playlist

interface CreatePlaylistUseCase {
    suspend operator fun invoke(playlist: Playlist): Long
}
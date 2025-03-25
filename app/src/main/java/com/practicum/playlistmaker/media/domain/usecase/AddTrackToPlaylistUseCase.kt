package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Track

interface AddTrackToPlaylistUseCase {
    suspend operator fun invoke(playlistId: Long, track: Track)
}
package com.practicum.playlistmaker.media.domain.usecase

interface RemoveTrackFromPlaylistUseCase {
    suspend operator fun invoke(playlistId: Long, trackId: Int)
}
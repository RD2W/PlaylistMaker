package com.practicum.playlistmaker.listmaker.domain.usecase

interface DeletePlaylistCoverUseCase {
    suspend operator fun invoke(imagePath: String)
}

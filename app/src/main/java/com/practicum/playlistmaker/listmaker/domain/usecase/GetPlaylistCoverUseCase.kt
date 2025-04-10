package com.practicum.playlistmaker.listmaker.domain.usecase

import java.io.File

interface GetPlaylistCoverUseCase {
    suspend operator fun invoke(imagePath: String): File?
}

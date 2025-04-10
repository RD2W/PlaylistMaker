package com.practicum.playlistmaker.media.domain.usecase

import android.net.Uri

interface CreatePlaylistUseCase {
    suspend operator fun invoke(
        name: String,
        description: String,
        coverImageUri: Uri?,
    ): Result<Long>
}

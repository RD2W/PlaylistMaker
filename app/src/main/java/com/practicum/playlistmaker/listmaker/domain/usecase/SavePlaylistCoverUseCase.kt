package com.practicum.playlistmaker.listmaker.domain.usecase

import android.net.Uri

interface SavePlaylistCoverUseCase {
    suspend operator fun invoke(imageUri: Uri): String
}

package com.practicum.playlistmaker.listmaker.domain.usecase

import android.net.Uri
import java.io.File

interface SaveTempImageUseCase {
    suspend operator fun invoke(uri: Uri): File?
}
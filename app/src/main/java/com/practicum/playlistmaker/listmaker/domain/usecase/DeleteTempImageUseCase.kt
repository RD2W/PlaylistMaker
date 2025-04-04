package com.practicum.playlistmaker.listmaker.domain.usecase

import java.io.File

interface DeleteTempImageUseCase {
    suspend operator fun invoke(file: File)
}

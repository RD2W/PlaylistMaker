package com.practicum.playlistmaker.listmaker.domain.usecase.impl

import android.net.Uri
import com.practicum.playlistmaker.listmaker.domain.repository.ImageRepository
import com.practicum.playlistmaker.listmaker.domain.usecase.SaveTempImageUseCase
import java.io.File

class SaveTempImageUseCaseImpl(
    private val repository: ImageRepository,
) : SaveTempImageUseCase {
    override suspend fun invoke(uri: Uri): File? = repository.saveTempImage(uri)
}
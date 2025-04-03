package com.practicum.playlistmaker.listmaker.domain.usecase.impl

import com.practicum.playlistmaker.listmaker.domain.repository.ImageRepository
import com.practicum.playlistmaker.listmaker.domain.usecase.DeleteTempImageUseCase
import java.io.File

class DeleteTempImageUseCaseImpl(
    private val repository: ImageRepository,
) : DeleteTempImageUseCase {
    override suspend fun invoke(file: File) = repository.deleteTempImage(file)
}
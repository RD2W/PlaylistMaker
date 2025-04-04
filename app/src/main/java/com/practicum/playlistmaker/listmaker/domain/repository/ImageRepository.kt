package com.practicum.playlistmaker.listmaker.domain.repository

import android.net.Uri
import java.io.File

interface ImageRepository {
    suspend fun saveTempImage(uri: Uri): File?
    suspend fun deleteTempImage(file: File)
}

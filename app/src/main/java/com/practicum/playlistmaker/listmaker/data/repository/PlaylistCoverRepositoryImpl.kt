package com.practicum.playlistmaker.listmaker.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLIST_COVER
import com.practicum.playlistmaker.listmaker.domain.repository.PlaylistCoverRepository
import java.io.File
import java.io.FileOutputStream

class PlaylistCoverRepositoryImpl(
    private val context: Context,
) : PlaylistCoverRepository {
    override suspend fun saveCoverImage(imageUri: Uri): String {
        val directory = File(context.filesDir, "covers").apply { mkdirs() }
        val fileName = "cover_${System.currentTimeMillis()}.jpg"
        val outputFile = File(directory, fileName)

        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return outputFile.absolutePath
    }

    override suspend fun deleteCoverImage(imagePath: String) {
        try {
            File(imagePath).takeIf { it.exists() }?.delete()
        } catch (e: Exception) {
            Log.e(PLAYLIST_COVER, "Failed to delete cover image", e)
        }
    }

    override suspend fun getCoverImage(imagePath: String): File? {
        return File(imagePath).takeIf { it.exists() }
    }
}
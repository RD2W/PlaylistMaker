package com.practicum.playlistmaker.listmaker.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLIST_COVER
import com.practicum.playlistmaker.listmaker.domain.repository.ImageRepository
import java.io.File
import java.io.FileOutputStream

class ImageRepositoryImpl(
    private val context: Context,
) : ImageRepository {
    override suspend fun saveTempImage(uri: Uri): File? {
        return try {
            File.createTempFile(
                TEMP_FILE_PREFIX,
                TEMP_FILE_SUFFIX,
                context.cacheDir
            ).apply {
                when {
                    uri.toString().contains("com.google.android.apps.photos.contentprovider") -> {
                        // Для Google Фото открываем InputStream напрямую
                        copyUriToFile(uri, this)
                    }
                    else -> {
                        // Для других провайдеров также используем InputStream
                        copyUriToFile(uri, this)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(PLAYLIST_COVER, "Error saving temp image", e)
            null
        }
    }

    override suspend fun deleteTempImage(file: File) {
        try {
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            Log.e(PLAYLIST_COVER, "Error deleting temp file", e)
        }
    }

    private fun copyUriToFile(uri: Uri, file: File) {
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    companion object {
        private const val TEMP_FILE_PREFIX = "temp_cover_"
        private const val TEMP_FILE_SUFFIX = ".jpg"
    }
}
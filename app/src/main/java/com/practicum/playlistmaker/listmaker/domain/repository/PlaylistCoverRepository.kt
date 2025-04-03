package com.practicum.playlistmaker.listmaker.domain.repository

import android.net.Uri
import java.io.File

/**
 * Репозиторий для работы с обложками плейлистов.
 * Обеспечивает операции сохранения, удаления и получения изображений обложек,
 * а также проверку необходимых разрешений.
 */
interface PlaylistCoverRepository {
    /**
     * Сохраняет изображение обложки из Uri во внутреннее хранилище приложения.
     *
     * @param imageUri Uri изображения для сохранения
     * @return Абсолютный путь к сохраненному файлу
     *
     * @throws SecurityException если отсутствуют необходимые разрешения
     * @throws IOException при ошибках чтения/записи файла
     * @throws IllegalArgumentException если Uri не может быть обработан
     */
    suspend fun saveCoverImage(imageUri: Uri): String

    /**
     * Удаляет файл обложки по указанному пути.
     *
     * @param imagePath Абсолютный путь к файлу обложки
     *
     * @note Метод подавляет все исключения и просто завершается, если файл не существует
     * или не может быть удален. Ошибки логируются.
     */
    suspend fun deleteCoverImage(imagePath: String)

    /**
     * Получает файл обложки по указанному пути.
     *
     * @param imagePath Абсолютный путь к файлу обложки
     * @return File объект или null, если файл не существует
     */
    suspend fun getCoverImage(imagePath: String): File?
}
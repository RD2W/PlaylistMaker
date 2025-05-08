package com.practicum.playlistmaker.media.domain.repository

interface TracksDataBaseRepository {
    /**
     * Удаляет трек из базы данных.
     *
     * Логика работы:
     * - Ищет трек в избранном по trackId
     * - Удаляет соответствующую запись из базы данных
     * - Если трек не найден в избранном, операция игнорируется
     *
     * @param trackId ID трека для удаления
     */
    suspend fun deleteFromDataBase(trackId: Int)
}

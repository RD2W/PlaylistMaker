package com.practicum.playlistmaker.media.domain.repository

interface TracksDataBaseRepository {
    /**
     * Удаляет трек из базы данных.
     *
     * Логика работы:
     * - Ищет трек в базе данных по trackId
     * - Удаляет соответствующую запись из базы данных
     * - Если трек не найден в базе данных, операция игнорируется
     *
     * @param trackId ID трека для удаления
     */
    suspend fun deleteFromDataBase(trackId: Int)
}

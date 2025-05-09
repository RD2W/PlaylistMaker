package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.media.data.source.local.db.dao.TracksDao
import com.practicum.playlistmaker.media.domain.repository.TracksDataBaseRepository

/**
 * Реализация репозитория для работы с треками в базе данных.
 *
 * Описание:
 * - Обеспечивает доступ к данным треков, хранящимся в локальной базе данных.
 * - Делегирует операции DAO (Data Access Object) для выполнения запросов к БД.
 * - Реализует контракт [TracksDataBaseRepository], предоставляя конкретную реализацию методов.
 *
 * Пример использования:
 * ```kotlin
 * val tracksDao: TracksDao = ... // инициализация DAO
 * val repository: TracksDataBaseRepository = TracksDataBaseRepositoryImpl(tracksDao)
 * viewModelScope.launch {
 *     repository.deleteFromDataBase(123)
 * }
 * ```
 *
 * @property tracksDao DAO для выполнения операций с треками в базе данных.
 */
class TracksDataBaseRepositoryImpl(private val tracksDao: TracksDao) : TracksDataBaseRepository {

    /**
     * Удаляет трек из базы данных по его идентификатору.
     *
     * Логика работы:
     * - Вызывает метод [TracksDao.deleteTrackById] для удаления трека.
     * - Если трек с указанным ID не существует, операция завершается без ошибок.
     * - Метод является suspend-функцией, поэтому должен вызываться из корутины.
     *
     * @param trackId Уникальный идентификатор трека для удаления.
     */
    override suspend fun deleteFromDataBase(trackId: Int) = tracksDao.deleteTrackById(trackId)
}

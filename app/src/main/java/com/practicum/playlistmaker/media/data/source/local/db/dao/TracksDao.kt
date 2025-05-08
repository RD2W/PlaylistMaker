package com.practicum.playlistmaker.media.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.source.local.db.entity.TrackEntity

@Dao
interface TracksDao {

    /**
     * Вставляет или обновляет трек в таблице `tracks`.
     *
     * Логика работы:
     * - Если трек с таким же `trackId` уже существует, он будет проигнорирован (стратегия `OnConflictStrategy.IGNORE`).
     * - В случае конфликта (дублирования `trackId`) новый трек не будет добавлен.
     * - Метод является suspend-функцией, поэтому должен вызываться из корутины или другой suspend-функции.
     *
     * @param track Объект трека типа `TrackEntity` для вставки в базу данных.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TrackEntity)

    /**
     * Возвращает трек по его идентификатору (`trackId`).
     *
     * Логика работы:
     * - Выполняет SQL-запрос для поиска трека в таблице `tracks`.
     * - Если трек найден, возвращает объект `TrackEntity`, иначе — `null`.
     * - Метод является suspend-функцией, поэтому выполняется асинхронно и должен вызываться из корутины.
     * - Запрос выполняется в отдельном потоке (реализация зависит от Room).
     *
     * @param trackId Идентификатор трека для поиска.
     * @return TrackEntity? Найденный трек или null, если трек не существует.
     */
    @Query("SELECT * FROM tracks WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Int): TrackEntity?

    /**
     * Удаляет трек из таблицы `tracks` по его идентификатору.
     *
     * Логика работы:
     * - Выполняет SQL-запрос для удаления записи с указанным `trackId`.
     * - Если трек с таким `trackId` не существует, метод завершится без ошибки.
     * - Метод является suspend-функцией, поэтому должен вызываться из корутины.
     *
     * @param trackId Идентификатор трека для удаления.
     */
    @Query("DELETE FROM tracks WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Int)
}

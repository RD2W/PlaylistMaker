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
     * - Если трек с таким же `trackId` уже существует, он будет заменен (стратегия `OnConflictStrategy.REPLACE`).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    /**
     * Возвращает трек по его идентификатору (`trackId`).
     *
     * Логика работы:
     * - Выполняет SQL-запрос для поиска трека в таблице `tracks`.
     * - Если трек найден, возвращает объект `TrackEntity`, иначе — `null`.
     */
    @Query("SELECT * FROM tracks WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Int): TrackEntity?
}
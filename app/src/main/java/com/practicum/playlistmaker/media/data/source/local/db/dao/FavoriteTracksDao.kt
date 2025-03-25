package com.practicum.playlistmaker.media.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.source.local.db.entity.TrackEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {

    /**
     * Добавляет трек в таблицу `favorite_tracks`.
     *
     * Логика работы:
     * - Если запись с таким же `trackId` уже существует, она будет заменена (стратегия `OnConflictStrategy.REPLACE`).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteTrack(favoriteTrack: FavoriteTrackEntity)

    /**
     * Удаляет трек из таблицы `favorite_tracks`.
     *
     * Логика работы:
     * - Выполняет SQL-запрос для удаления записи с указанным `trackId`.
     */
    @Query("DELETE FROM favorite_tracks WHERE track_id = :trackId")
    suspend fun removeFromFavorites(trackId: Int)

    /**
     * Возвращает избранный трек по его идентификатору (`trackId`).
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который проверяет, существует ли `trackId` в таблице `favorite_tracks`.
     * - Если трек найден, возвращает объект `TrackEntity`, иначе — `null`.
     */
    @Query("SELECT * FROM tracks WHERE track_id = :trackId AND track_id IN (SELECT track_id FROM favorite_tracks)")
    suspend fun getFavoriteTrackById(trackId: Int): TrackEntity?

    /**
     * Возвращает поток списка избранных треков.
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который выбирает все треки из таблицы `tracks`,
     *   чьи `trackId` присутствуют в таблице `favorite_tracks`.
     * - Треки сортируются по времени добавления (`addedTimestamp`) в порядке убывания (последний добавленный трек будет первым).
     */
    @Query(
        """
        SELECT tracks.* FROM tracks
        INNER JOIN favorite_tracks ON tracks.track_id = favorite_tracks.track_id
        ORDER BY favorite_tracks.added_timestamp DESC
        """
    )
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    /**
     * Проверяет, является ли трек избранным.
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который подсчитывает количество записей с указанным `trackId` в таблице `favorite_tracks`.
     * - Если количество записей больше 0, трек считается избранным, и поток эмитит `true`.
     * - Если записей с таким `trackId` нет, поток эмитит `false`.
     * - Возвращает Flow<Boolean>, который эмитит `true`, если трек избранный, и `false`, если нет.
     */
    @Query("SELECT COUNT(*) FROM favorite_tracks WHERE track_id = :trackId")
    fun isTrackFavorite(trackId: Int): Flow<Boolean>
}
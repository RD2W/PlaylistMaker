package com.practicum.playlistmaker.media.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistTrackCrossRef
import com.practicum.playlistmaker.media.data.source.local.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackCrossRefDao {

    /**
     * Добавляет связь между плейлистом и треком.
     *
     * Логика работы:
     * - Вставляет объект `PlaylistTrackCrossRef` в таблицу `playlist_track_cross_ref`.
     * - Если связь с такими же `playlistId` и `trackId` уже существует,
     *   она будет заменена (стратегия `OnConflictStrategy.REPLACE`).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    /**
     * Удаляет связь между плейлистом и треком.
     *
     * Логика работы:
     * - Удаляет запись из таблицы `playlist_track_cross_ref`,
     *   соответствующую переданному объекту `PlaylistTrackCrossRef`.
     */
    @Delete
    suspend fun removeTrackFromPlaylist(crossRef: PlaylistTrackCrossRef)

    /**
     * Удаляет все треки из указанного плейлиста.
     *
     * Логика работы:
     * - Выполняет SQL-запрос DELETE, который удаляет все записи из таблицы `playlist_track_cross_ref`,
     *   где `playlist_id` соответствует переданному идентификатору плейлиста.
     */
    @Query("DELETE FROM playlist_track_cross_ref WHERE playlist_id = :playlistId")
    suspend fun removeAllTracksFromPlaylist(playlistId: Long)

    /**
     * Возвращает поток списка треков, принадлежащих указанному плейлисту,
     * отсортированных по дате добавления (сначала новые).
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который выбирает все треки из таблицы `tracks`,
     *   чьи `trackId` присутствуют в таблице `playlist_track_cross_ref` для указанного `playlistId`.
     * - Сортирует результаты по дате добавления в порядке убывания (DESC).
     * - Результат возвращается как поток (`Flow`) списка сущностей `TrackEntity`.
     */
    @Query(
        """
        SELECT tracks.* FROM tracks 
        INNER JOIN playlist_track_cross_ref ON tracks.track_id = playlist_track_cross_ref.track_id
        WHERE playlist_track_cross_ref.playlist_id = :playlistId
        ORDER BY playlist_track_cross_ref.added_at DESC
        """,
    )
    fun getTracksInPlaylist(playlistId: Long): Flow<List<TrackEntity>>

    /**
     * Проверяет, содержится ли указанный трек в заданном плейлисте.
     *
     * Логика работы:
     * - Выполняет SQL-запрос с использованием оператора EXISTS, который проверяет наличие записи
     *   в таблице `playlist_track_cross_ref` с заданными `playlistId` и `trackId`.
     * - Возвращает `true`, если трек присутствует в плейлисте, и `false` в противном случае.
     * - Метод является suspend-функцией, что позволяет вызывать его из корутин.
     */
    @Query(
        """
        SELECT EXISTS(
        SELECT 1 FROM playlist_track_cross_ref 
        WHERE playlist_id = :playlistId AND track_id = :trackId
        )
        """,
    )
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Int): Boolean

    /**
     * Проверяет наличие трека в любом из плейлистов и возвращает результат как реактивный поток.
     *
     * ### SQL Логика работы:
     * 1. Использует оператор EXISTS для эффективной проверки наличия записи
     * 2. Ищет в таблице связей `playlist_track_cross_ref` записи с указанным track_id
     * 3. LIMIT 1 оптимизирует запрос - прекращает поиск после первой найденной записи
     *
     * ### Особенности поведения:
     * - Возвращает Flow<Boolean>, который эмитит:
     *   - `true` если трек найден хотя бы в одном плейлисте
     *   - `false` если трек отсутствует во всех плейлистах
     * - Автоматически обновляется при:
     *   - Добавлении трека в любой плейлист
     *   - Удалении трека из всех плейлистов
     *
     * ### Производительность:
     * - Запрос выполняется на уровне SQLite (максимальная эффективность)
     * - EXISTS + LIMIT 1 обеспечивают минимальную нагрузку на БД
     * - Не требует загрузки самих данных о плейлистах
     *
     * @param trackId Идентификатор трека для проверки (должен соответствовать track_id в БД)
     * @return Flow с булевым результатом проверки (true - найден, false - не найден)
     */
    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM playlist_track_cross_ref 
            WHERE track_id = :trackId
        ) LIMIT 1
        """,
    )
    fun isTrackInAnyPlaylist(trackId: Int): Flow<Boolean>

    /**
     * Возвращает поток списка идентификаторов плейлистов, содержащих указанный трек.
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который выбирает все `playlist_id` из таблицы `playlist_track_cross_ref`,
     *   где `track_id` соответствует переданному идентификатору трека.
     * - Результат возвращается как поток (`Flow`) списка идентификаторов плейлистов (`Long`).
     */
    @Query("SELECT playlist_id FROM playlist_track_cross_ref WHERE track_id = :trackId")
    fun getPlaylistsContainingTrack(trackId: Int): Flow<List<Long>>

    /**
     * Возвращает поток списка идентификаторов всех плейлистов, которые НЕ содержат указанный трек.
     *
     * Логика работы:
     * 1. Выбирает все ID плейлистов из таблицы `playlists`
     * 2. Исключает плейлисты, содержащие трек с заданным ID (через подзапрос к таблице связей)
     *
     * Особенности:
     * - Включает как плейлисты с другими треками, так и полностью пустые плейлисты
     * - Результат автоматически обновляется при изменениях в таблицах
     * - Для нового трека (еще не добавленного никуда) вернет все существующие плейлисты
     *
     * @param trackId ID трека для исключения из поиска
     * @return Flow<List<Long>> Поток со списком ID подходящих плейлистов.
     *         Если все плейлисты содержат трек, вернет пустой список.
     */
    @Query(
        """
        SELECT playlist_id FROM playlists 
        WHERE playlist_id NOT IN (
            SELECT playlist_id FROM playlist_track_cross_ref 
            WHERE track_id = :trackId
        )
        """,
    )
    fun getPlaylistsNotContainingTrack(trackId: Int): Flow<List<Long>>

    /**
     * Возвращает поток списка идентификаторов НЕпустых плейлистов, которые не содержат указанный трек.
     *
     * Логика работы:
     * 1. Выбирает ID плейлистов, не содержащих заданный трек (аналогично базовому методу)
     * 2. Дополнительно проверяет, что плейлист содержит хотя бы один трек (через EXISTS)
     *
     * Особенности:
     * - Исключает полностью пустые плейлисты
     * - Полезен для случаев, когда нужно предлагать только плейлисты с контентом
     * - Автоматически реагирует на изменения в обоих таблицах
     *
     * @param trackId ID трека для исключения из поиска
     * @return Flow<List<Long>> Поток со списком ID непустых плейлистов без указанного трека.
     *         Если все непустые плейлисты содержат трек, вернет пустой список.
     */
    @Query(
        """
        SELECT p.playlist_id FROM playlists p
        WHERE p.playlist_id NOT IN (
            SELECT pt.playlist_id FROM playlist_track_cross_ref pt
            WHERE pt.track_id = :trackId
        )
        AND EXISTS (
            SELECT 1 FROM playlist_track_cross_ref
            WHERE playlist_id = p.playlist_id
        )
        """,
    )
    fun getNonEmptyPlaylistsNotContainingTrack(trackId: Int): Flow<List<Long>>

    /**
     * Возвращает количество треков в указанном плейлисте.
     *
     * Логика работы:
     * - Выполняет SQL-запрос, который подсчитывает количество записей с указанным `playlistId`
     *   в таблице `playlist_track_cross_ref`.
     * - Возвращает целочисленное значение, представляющее количество треков.
     */
    @Query("SELECT COUNT(*) FROM playlist_track_cross_ref WHERE playlist_id = :playlistId")
    suspend fun getTrackCountInPlaylist(playlistId: Long): Int
}

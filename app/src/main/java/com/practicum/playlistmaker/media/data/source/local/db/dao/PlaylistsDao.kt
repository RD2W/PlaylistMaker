package com.practicum.playlistmaker.media.data.source.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    /**
     * Добавляет или обновляет плейлист в таблице `playlists`.
     *
     * Логика работы:
     * - Вставляет объект `PlaylistEntity` в таблицу `playlists`.
     * - Если плейлист с таким же `playlistId` уже существует,
     *   он будет заменен (стратегия `OnConflictStrategy.REPLACE`).
     * - Возвращает `rowId` вставленной или обновленной записи.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    /**
     * Удаляет плейлист по его идентификатору (`playlistId`).
     *
     * Логика работы:
     * - Выполняет SQL-запрос для удаления записи из таблицы `playlists`,
     *   где `playlistId` совпадает с указанным значением.
     */
    @Query("DELETE FROM playlists WHERE playlist_id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    /**
     * Возвращает поток списка всех плейлистов с дополнительной информацией:
     * количеством треков, общей продолжительностью, отсортированных по времени добавления
     * (самые новые первыми).
     *
     * Логика работы:
     * 1. Выполняет SQL-запрос с JOIN между таблицами:
     *    - `playlists` (основная таблица плейлистов)
     *    - `playlist_track_cross_ref` (таблица связей плейлист-трек)
     *    - `tracks` (таблица треков)
     * 2. Для каждого плейлиста рассчитывает:
     *    - Количество связанных треков (COUNT)
     *    - Суммарную длительность треков в миллисекундах (SUM)
     * 3. Группирует результаты по ID плейлиста (GROUP BY)
     * 4. Сортирует по времени добавления (`added_at`) в порядке убывания (DESC)
     * 5. Возвращает результат как поток (`Flow`) списка сущностей `PlaylistInfoEntity`
     *
     * Особенности:
     * - Самые новые плейлисты (с наибольшим значением `added_at`) будут первыми в списке
     * - Для плейлистов без треков:
     *   - `track_count` будет 0
     *   - `total_duration` будет NULL (преобразуется в 0L в маппере)
     */
    @Query(
        """
        SELECT p.*, 
               COUNT(pt.track_id) as track_count,
               SUM(t.track_time_millis) as total_duration
        FROM playlists p
        LEFT JOIN playlist_track_cross_ref pt ON p.playlist_id = pt.playlist_id
        LEFT JOIN tracks t ON pt.track_id = t.track_id
        GROUP BY p.playlist_id
        ORDER BY p.added_at DESC
        """,
    )
    fun getAllPlaylistsInfo(): Flow<List<PlaylistInfoEntity>>

    /**
     * Возвращает плейлист по его идентификатору (`playlistId`).
     *
     * Логика работы:
     * - Выполняет SQL-запрос для поиска плейлиста в таблице `playlists`.
     * - Если плейлист найден, результат возвращается как поток (`Flow`) объектов `PlaylistEntity`, иначе — `null`.
     */
    @Query("SELECT * FROM playlists WHERE playlist_id = :playlistId")
    fun getPlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    /**
     * Получает плейлисты по списку ID с дополнительной информацией о количестве треков и общей продолжительности
     * @param playlistIds Список ID плейлистов для загрузки
     * @return Flow со списком PlaylistInfoEntity, содержащих:
     *         - данные плейлиста (PlaylistEntity)
     *         - количество треков в плейлисте (trackCount)
     *         - общую продолжительность треков в миллисекундах (totalDurationMillis)
     *
     * Особенности:
     * - Использует оператор IN для фильтрации
     * - Если передан пустой список, возвращает emptyList()
     * - Порядок результатов может не соответствовать порядку в playlistIds
     *   (для сохранения порядка используйте ORDER BY CASE)
     * - Для каждого плейлиста выполняет подсчет треков и суммирование их длительности
     * - Использует LEFT JOIN, поэтому плейлисты без треков будут возвращены с trackCount = 0
     * - Продолжительность треков суммируется из поля track_time_millis таблицы tracks
     */
    @Query(
        """
        SELECT p.*, 
               COUNT(pt.track_id) AS track_count,
               COALESCE(SUM(t.track_time_millis), 0) AS total_duration
        FROM playlists p
        LEFT JOIN playlist_track_cross_ref pt ON p.playlist_id = pt.playlist_id
        LEFT JOIN tracks t ON pt.track_id = t.track_id
        WHERE p.playlist_id IN (:playlistIds)
        GROUP BY p.playlist_id
        """,
    )
    fun getPlaylistsInfoByIds(playlistIds: List<Long>): Flow<List<PlaylistInfoEntity>>

    /**
     * Обновляет информацию о плейлисте с возможностью частичного изменения полей.
     *
     * Логика работы:
     * - Выполняет SQL-запрос UPDATE для выборочного обновления полей плейлиста
     * - Использует функцию COALESCE для обновления только переданных параметров:
     *   - Если параметр не null - поле будет обновлено
     *   - Если параметр null - поле останется без изменений
     * - Автоматически обновляет поле edited_at текущим временем (System.currentTimeMillis())
     *
     * Особенности:
     * - Поддерживает частичное обновление (можно изменить одно или несколько полей)
     * - Не изменяет поля, для которых передано null
     * - Всегда обновляет edited_at (даже если другие поля не меняются)
     * - Не изменяет playlist_id и creation_date
     *
     * @param playlistId ID плейлиста для обновления (обязательный параметр)
     * @param name Новое название (необязательно, null = не изменять)
     * @param description Новое описание (необязательно, null = не изменять)
     * @param coverFilePath Новый путь к обложке (необязательно, null = не изменять)
     * @param editedAt Время редактирования (по умолчанию текущее время)
     *
     * Примеры использования:
     * 1. Изменить только название:
     *    editPlaylistInfo(123, name = "Новое название")
     *
     * 2. Изменить описание и обложку:
     *    editPlaylistInfo(123,
     *        description = "Новое описание",
     *        coverFilePath = "/new/path.jpg")
     */
    @Query(
        """
        UPDATE playlists 
        SET 
            name = COALESCE(:name, name),
            description = COALESCE(:description, description),
            cover_file_path = COALESCE(:coverFilePath, cover_file_path),
            edited_at = :editedAt
        WHERE playlist_id = :playlistId
        """,
    )
    suspend fun editPlaylistInfo(
        playlistId: Long,
        name: String? = null,
        description: String? = null,
        coverFilePath: String? = null,
        editedAt: Long = System.currentTimeMillis(),
    )
}

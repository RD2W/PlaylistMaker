package com.practicum.playlistmaker.media.domain.repository

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    /**
     * Получает поток с полной информацией о всех плейлистах.
     *
     * Логика работы:
     * 1. Запрашивает данные из таблицы плейлистов
     * 2. Для каждого плейлиста рассчитывает:
     *    - Количество треков (через JOIN с таблицей связей)
     *    - Общую длительность (сумма длительностей треков)
     *
     * Особенности:
     * - Данные автоматически обновляются при изменениях
     * - Сортировка по умолчанию: по дате добавления (новые выше)
     * - Для пустых плейлистов возвращает trackCount = 0 и totalDuration = 0
     *
     * @return Flow<List<Playlist>> поток с актуальным списком всех плейлистов
     */
    fun getPlaylistsInfo(): Flow<List<Playlist>>

    /**
     * Получает детальную информацию о конкретном плейлисте.
     *
     * Логика работы:
     * 1. Ищет плейлист по ID в локальной базе
     * 2. Загружает список треков плейлиста
     * 3. Комбинирует данные в единый объект Playlist
     *
     * @param playlistId ID запрашиваемого плейлиста
     * @return Flow<Playlist?> Поток с данными плейлиста.
     *         Эмитит null, если плейлист не найден.
     */
    fun getPlaylistById(playlistId: Long): Flow<Playlist?>

    /**
     * Получает список треков в указанном плейлисте.
     *
     * Логика работы:
     * 1. Проверяет существование плейлиста
     * 2. Запрашивает все треки через таблицу связей
     * 3. Сортирует треки по дате добавления (новые выше)
     *
     * @param playlistId ID плейлиста
     * @return Flow<List<Track>> Поток со списком треков.
     *         Эмитит emptyList(), если плейлист пуст или не существует.
     */
    fun getTracksInPlaylist(playlistId: Long): Flow<List<Track>>

    /**
     * Создает новый плейлист.
     *
     * Логика работы:
     * 1. Преобразует Playlist в PlaylistEntity
     * 2. Сохраняет в базу с текущей датой создания
     * 3. Возвращает ID созданной записи
     *
     * @param playlist Данные нового плейлиста
     * @return Long ID созданного плейлиста
     * @throws IllegalArgumentException Если переданы некорректные данные
     */
    suspend fun createPlaylist(playlist: Playlist): Long

    /**
     * Удаляет плейлист и все связанные данные.
     *
     * Логика работы:
     * 1. Удаляет записи из таблицы связей с треками
     * 2. Удаляет сам плейлист
     * 3. Очищает связанные ресурсы (обложки и т.д.)
     *
     * @param playlistId ID удаляемого плейлиста
     * @throws NoSuchElementException Если плейлист не найден
     */
    suspend fun deletePlaylist(playlistId: Long)

    /**
     * Добавляет трек в плейлист.
     *
     * Логика работы:
     * 1. Проверяет существование плейлиста
     * 2. Создает связь в таблице playlist_track_cross_ref
     * 3. Обновляет счетчик треков и длительность плейлиста
     *
     * @param playlistId ID целевого плейлиста
     * @param track Добавляемый трек
     */
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)

    /**
     * Удаляет трек из плейлиста.
     *
     * Логика работы:
     * 1. Проверяет существование связи
     * 2. Удаляет запись из таблицы связей
     * 3. Обновляет метаданные плейлиста
     *
     * @param playlistId ID плейлиста
     * @param trackId ID удаляемого трека
     * @throws NoSuchElementException Если трек не найден в плейлисте
     */
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Int)

    /**
     * Очищает плейлист, удаляя все треки из него.
     *
     * Логика работы:
     * 1. Выполняет SQL-запрос DELETE, который удаляет все записи
     *    из таблицы `playlist_track_cross_ref`,
     *    где `playlist_id` соответствует переданному идентификатору плейлиста.
     * 2. После выполнения метода плейлист становится пустым
     *    (все связи между плейлистом и треками удаляются).
     * 3. Сам плейлист (запись в таблице `playlists`) не удаляется.
     * 4. Треки (записи в таблице `tracks`) остаются в базе данных,
     *    так как они могут быть связаны с другими плейлистами.
     *
     * Особенности:
     * - Метод является suspend-функцией, что позволяет вызывать его асинхронно внутри корутин.
     * - После выполнения метода количество треков в плейлисте станет равным нулю.
     *
     * @param playlistId Идентификатор плейлиста, который нужно очистить.
     * @throws NoSuchElementException Если плейлист не найден.
     */
    suspend fun clearTracksInPlaylist(playlistId: Long)

    /**
     * Редактирует метаданные плейлиста.
     *
     * Логика работы:
     * 1. Проверяет существование плейлиста
     * 2. Обновляет только переданные поля (null-значения игнорируются)
     * 3. Автоматически обновляет edited_at
     *
     * @param playlistId ID редактируемого плейлиста
     * @param name Новое название (null = не изменять)
     * @param description Новое описание (null = не изменять)
     * @param coverFilePath Новый путь к обложке (null = не изменять)
     */
    suspend fun editPlaylist(
        playlistId: Long,
        name: String? = null,
        description: String? = null,
        coverFilePath: String? = null,
    )

    /**
     * Находит плейлисты, содержащие указанный трек.
     *
     * Логика работы:
     * 1. Ищет связи в playlist_track_cross_ref
     * 2. Загружает полные данные найденных плейлистов
     * 3. Сортирует по дате добавления трека (новые выше)
     *
     * @param trackId ID искомого трека
     * @return Flow<List<Playlist>> Поток с результатами.
     *         Эмитит emptyList(), если трек не найден ни в одном плейлисте.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPlaylistsContainingTrack(trackId: Int): Flow<List<Playlist>>

    /**
     * Находит плейлисты без указанного трека.
     *
     * Логика работы:
     * 1. Исключает плейлисты, содержащие трек
     * 2. При onlyNonEmpty=true дополнительно фильтрует пустые плейлисты
     * 3. Загружает полные данные плейлистов
     *
     * @param trackId ID исключаемого трека
     * @param onlyNonEmpty Флаг фильтрации пустых плейлистов
     * @return Flow<List<Playlist>> Поток с подходящими плейлистами.
     *         Эмитит emptyList(), если все плейлисты содержат трек.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getPlaylistsNotContainingTrack(
        trackId: Int,
        onlyNonEmpty: Boolean = false,
    ): Flow<List<Playlist>>

    /**
     * Проверяет, содержится ли трек в каком-либо плейлисте.
     *
     * Логика работы:
     * 1. Выполняет запрос к таблице связей playlist_track_cross_ref
     * 2. Возвращает Flow<Boolean>, который эмитит:
     *    - true, если трек есть хотя бы в одном плейлисте
     *    - false, если трека нет ни в одном плейлисте
     * 3. Автоматически обновляется при изменениях в БД
     *
     * @param trackId ID проверяемого трека
     * @return Flow с результатом проверки
     */
    fun isTrackInAnyPlaylist(trackId: Int): Flow<Boolean>
}

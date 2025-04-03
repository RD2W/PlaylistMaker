package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.common.domain.model.Playlist
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.data.mapper.toPlaylist
import com.practicum.playlistmaker.media.data.mapper.toPlaylistEntity
import com.practicum.playlistmaker.media.data.mapper.toTrack
import com.practicum.playlistmaker.media.data.mapper.toTrackEntity
import com.practicum.playlistmaker.media.data.source.local.db.dao.PlaylistTrackCrossRefDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.PlaylistsDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.TracksDao
import com.practicum.playlistmaker.media.data.source.local.db.entity.PlaylistTrackCrossRef
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val tracksDao: TracksDao,
    private val playlistsDao: PlaylistsDao,
    private val playlistTrackDao: PlaylistTrackCrossRefDao,
) : PlaylistsRepository {

    /**
     * Возвращает поток списка всех плейлистов.
     *
     * Логика работы:
     * 1. Вызывает метод `playlistsDao.getAllPlaylistsInfo()`, который возвращает поток списка сущностей `PlaylistInfoEntity`.
     * 2. Преобразует каждую сущность `PlaylistInfoEntity` в доменную модель `Playlist` с помощью мапера `toPlaylist()`.
     */
    override fun getPlaylistsInfo(): Flow<List<Playlist>> {
        return playlistsDao.getAllPlaylistsInfo().map { playlistInfoEntities ->
            playlistInfoEntities.map { it.toPlaylist() }
        }
    }

    /**
     * Возвращает поток плейлиста по его идентификатору (`playlistId`).
     *
     * Логика работы:
     * 1. Вызывает метод `playlistsDao.getPlaylistById(playlistId)`, который возвращает сущность `PlaylistEntity?` (может быть `null`).
     * 2. Преобразует сущность `PlaylistEntity` в доменную модель `Playlist` с помощью мапера `toPlaylist()`.
     * 3. Если плейлист не найден, возвращает `null`.
     */
    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistsDao.getPlaylistById(playlistId).combine(
            playlistTrackDao.getTracksInPlaylist(playlistId)
        ) { playlistEntity, trackEntities ->
            playlistEntity?.toPlaylist(trackEntities)
        }
    }

    /**
     * Возвращает поток списка треков, принадлежащих указанному плейлисту.
     *
     * Логика работы:
     * 1. Вызывает метод `playlistTrackDao.getTracksInPlaylist(playlistId)`, который возвращает поток списка сущностей `TrackEntity`.
     * 2. Преобразует каждую сущность `TrackEntity` в доменную модель `Track` с помощью мапера `toTrack()`.
     */
    override fun getTracksInPlaylist(playlistId: Long): Flow<List<Track>> {
        return playlistTrackDao.getTracksInPlaylist(playlistId).map { trackEntities ->
            trackEntities.map { it.toTrack() }
        }
    }

    /**
     * Создает новый плейлист.
     *
     * Логика работы:
     * 1. Преобразует доменную модель `Playlist` в сущность `PlaylistEntity` с помощью мапера `toPlaylistEntity()`.
     * 2. Вызывает метод `playlistsDao.insertPlaylist()` для сохранения плейлиста в базе данных.
     * 3. Возвращает `rowId` созданного плейлиста.
     */
    override suspend fun createPlaylist(playlist: Playlist): Long {
        return playlistsDao.insertPlaylist(playlist.toPlaylistEntity())
    }

    /**
     * Редактирует информацию о плейлисте с возможностью частичного обновления полей
     *
     * @param playlistId ID редактируемого плейлиста (обязательный)
     * @param name Новое название (необязательно - null оставит текущее значение)
     * @param description Новое описание (необязательно - null оставит текущее значение)
     * @param coverFilePath Новый путь к обложке (необязательно - null оставит текущее значение)
     * @param editedAt Время редактирования (по умолчанию текущее время)
     *
     * Примеры использования:
     * 1. Изменить только название:
     *    editPlaylist(123, name = "Новое название")
     *
     * 2. Изменить несколько полей:
     *    editPlaylist(123,
     *        name = "Новое название",
     *        description = "Обновленное описание")
     */
    override suspend fun editPlaylist(
        playlistId: Long,
        name: String?,
        description: String?,
        coverFilePath: String?,
    ) {
        playlistsDao.editPlaylistInfo(
            playlistId = playlistId,
            name = name,
            description = description,
            coverFilePath = coverFilePath,
        )
    }

    /**
     * Удаляет плейлист по его идентификатору.
     *
     * Логика работы:
     * 1. Вызывает метод `playlistDao.deletePlaylistById()` для удаления плейлиста из базы данных.
     */
    override suspend fun deletePlaylist(playlistId: Long) {
        playlistsDao.deletePlaylistById(playlistId)
    }

    /**
     * Получает полную информацию о плейлистах, содержащих указанный трек.
     *
     * Логика работы:
     * 1. Сначала запрашивает список ID плейлистов, содержащих трек (через playlistTrackDao)
     * 2. Если плейлистов не найдено - возвращает пустой список
     * 3. Для найденных плейлистов загружает полную информацию (через playlistDao)
     * 4. Преобразует сущности в доменные модели (toPlaylist())
     *
     * Особенности:
     * - Использует flatMapLatest для автоматической отмены предыдущих запросов при изменении trackId
     * - Реактивно обновляется при изменениях в любом из плейлистов
     * - Автоматически обрабатывает случай отсутствия трека в плейлистах
     *
     * @param trackId ID трека для поиска
     * @return Flow<List<Playlist>> - поток со списком плейлистов.
     *         Эмитит emptyList(), если трек не найден ни в одном плейлисте.
     *
     * Пример использования:
     * repository.getPlaylistsContainingTrack(123)
     *     .collect { playlists ->
     *         if (playlists.isEmpty()) {
     *             showMessage("Трек не добавлен ни в один плейлист")
     *         } else {
     *             updatePlaylistList(playlists)
     *         }
     *     }
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlaylistsContainingTrack(trackId: Int): Flow<List<Playlist>> {
        return playlistTrackDao.getPlaylistsContainingTrack(trackId).flatMapLatest { playlistIds ->
            when {
                playlistIds.isEmpty() -> flowOf(emptyList())
                else -> playlistsDao.getPlaylistsInfoByIds(playlistIds).map { playlistInfoEntities ->
                    playlistInfoEntities.map { it.toPlaylist() }
                }
            }
        }
    }

    /**
     * Получает полную информацию о плейлистах, НЕ содержащих указанный трек.
     *
     * Логика работы:
     * 1. Сначала запрашивает список ID плейлистов без трека (через playlistsDao)
     * 2. Если плейлистов не найдено - возвращает пустой список
     * 3. Для найденных плейлистов загружает полную информацию (через playlistsDao)
     * 4. Преобразует сущности в доменные модели (toPlaylist())
     *
     * Особенности:
     * - Поддерживает два режима: все плейлисты/только непустые (onlyNonEmpty)
     * - Использует flatMapLatest для отмены предыдущих запросов
     * - Реактивно обновляется при изменениях плейлистов
     * - Автоматически обрабатывает случай, когда все плейлисты содержат трек
     *
     * @param trackId ID трека для исключения
     * @param onlyNonEmpty Если true, возвращает только плейлисты с другими треками
     * @return Flow<List<Playlist>> - поток со списком подходящих плейлистов.
     *         Эмитит emptyList(), если все плейлисты содержат трек.
     *
     * Пример использования:
     * repository.getPlaylistsNotContainingTrack(123)
     *     .collect { playlists ->
     *         if (playlists.isEmpty()) {
     *             showMessage("Трек есть во всех плейлистах")
     *         } else {
     *             showAvailablePlaylists(playlists)
     *         }
     *     }
     *
     * fun loadAvailablePlaylists(trackId: Int) {
     *     viewModelScope.launch {
     *         repository.getPlaylistsNotContainingTrack(trackId, onlyNonEmpty = true)
     *             .collect { playlists ->
     *                 _uiState.value = when {
     *                     playlists.isEmpty() -> ShowError("Нет доступных плейлистов")
     *                     else -> ShowPlaylists(playlists)
     *                 }
     *             }
     *     }
     * }
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPlaylistsNotContainingTrack(
        trackId: Int,
        onlyNonEmpty: Boolean,
    ): Flow<List<Playlist>> {
        val playlistsFlow = if (onlyNonEmpty) {
            playlistTrackDao.getNonEmptyPlaylistsNotContainingTrack(trackId)
        } else {
            playlistTrackDao.getPlaylistsNotContainingTrack(trackId)
        }

        return playlistsFlow.flatMapLatest { playlistIds ->
            when {
                playlistIds.isEmpty() -> flowOf(emptyList())
                else -> playlistsDao.getPlaylistsInfoByIds(playlistIds).map { playlistInfoEntities ->
                    playlistInfoEntities.map { it.toPlaylist() } }
            }
        }
    }

    /**
     * Проверяет наличие трека в любом из плейлистов и возвращает результат как реактивный поток.
     *
     * ### Логика работы:
     * 1. Делегирует запрос к DAO слою через `playlistTrackDao.isTrackInAnyPlaylist(trackId)`
     * 2. Возвращает чистый Flow без дополнительных операторов для максимальной совместимости
     *
     * ### Особенности:
     * - Возвращает `Flow<Boolean>`, который будет автоматически обновляться при:
     *   - Добавлении трека в любой плейлист
     *   - Удалении трека из всех плейлистов
     * - Каждое изменение в таблице связей вызовет новое значение в Flow
     * - Не требует явной подписки/отписки - жизненный цикл управляется корутинами
     *
     * ### Пример использования:
     * repository.isTrackInAnyPlaylist(trackId)
     *     .collect { isInPlaylist ->
     *         // Обновление UI в зависимости от состояния
     *     }
     *
     * @param trackId Идентификатор трека для проверки
     * @return Flow, который эмитит:
     *         - `true` если трек существует хотя бы в одном плейлисте
     *         - `false` если трек отсутствует во всех плейлистах
     */
    override fun isTrackInAnyPlaylist(trackId: Int): Flow<Boolean> {
        return playlistTrackDao.isTrackInAnyPlaylist(trackId)
    }

    /**
     * Добавляет трек в плейлист.
     *
     * Логика работы:
     * 1. Преобразует доменную модель `Track` в сущность `TrackEntity` с помощью мапера `toTrackEntity()`.
     * 2. Вызывает метод `tracksDao.insertTrack()` для сохранения трека в таблице `tracks`.
     * 3. Создает объект `PlaylistTrackCrossRef` для связи между плейлистом и треком.
     * 4. Вызывает метод `playlistTrackDao.addTrackToPlaylist()` для добавления связи в таблицу `playlist_track_cross_ref`.
     */
    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        tracksDao.insertTrack(track.toTrackEntity())
        val crossRef = PlaylistTrackCrossRef(
            playlistId = playlistId,
            trackId = track.trackId
        )
        playlistTrackDao.addTrackToPlaylist(crossRef)
    }

    /**
     * Удаляет трек из плейлиста.
     *
     * Логика работы:
     * 1. Создает объект `PlaylistTrackCrossRef` с указанными `playlistId` и `trackId`.
     * 2. Вызывает метод `playlistTrackDao.removeTrackFromPlaylist()` для удаления связи между плейлистом и треком.
     */
    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Int) {
        val crossRef = PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId)
        playlistTrackDao.removeTrackFromPlaylist(crossRef)
    }

    /**
     * Очищает плейлист, удаляя все треки из него.
     *
     * Логика работы:
     * 1. Вызывает метод [PlaylistTrackCrossRefDao.removeAllTracksFromPlaylist], который выполняет SQL-запрос DELETE,
     *    удаляющий все записи из таблицы `playlist_track_cross_ref`, связанные с указанным `playlistId`.
     * 2. После выполнения метода плейлист становится пустым (все связи между плейлистом и треками удаляются).
     *
     * Особенности:
     * - Метод является suspend-функцией, что позволяет вызывать его асинхронно внутри корутин.
     * - Сам плейлист (запись в таблице `playlists`) не удаляется, удаляются только связи с треками.
     * - Треки (записи в таблице `tracks`) остаются в базе данных, так как они могут быть связаны с другими плейлистами.
     *
     * @param playlistId Идентификатор плейлиста, который нужно очистить.
     */
    override suspend fun clearTracksInPlaylist(playlistId: Long) {
        playlistTrackDao.removeAllTracksFromPlaylist(playlistId)
    }
}
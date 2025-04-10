package com.practicum.playlistmaker.media.domain.repository

import com.practicum.playlistmaker.common.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    /**
     * Получает поток списка всех треков, добавленных в избранное.
     *
     * Логика работы:
     * - Запрашивает полный список избранных треков из базы данных
     * - Возвращает данные в виде потока (Flow), который автоматически обновляется при изменениях
     * - Сортировка треков зависит от реализации (обычно по дате добавления)
     *
     * @return Flow<List<Track>> Поток со списком избранных треков.
     *         Возвращает emptyList(), если избранных треков нет.
     */
    fun getFavoriteTracks(): Flow<List<Track>>

    /**
     * Проверяет, добавлен ли трек в избранное.
     *
     * Логика работы:
     * - Выполняет запрос к базе данных для проверки наличия трека
     * - Результат возвращается как поток (Flow), который обновляется при изменениях статуса
     *
     * @param trackId ID трека для проверки
     * @return Flow<Boolean> Поток с результатом проверки:
     *         - true если трек в избранном
     *         - false если трека нет в избранном
     */
    fun isTrackFavorite(trackId: Int): Flow<Boolean>

    /**
     * Получает конкретный трек из избранного по ID.
     *
     * Логика работы:
     * - Ищет трек в локальной базе данных по trackId
     * - Возвращает полную информацию о треке, если он найден в избранном
     *
     * @param trackId ID трека для поиска
     * @return Track? Объект трека или null, если трек не найден в избранном
     */
    suspend fun getFavoriteTrackById(trackId: Int): Track?

    /**
     * Добавляет трек в избранное.
     *
     * Логика работы:
     * - Преобразует Track в FavoriteTrackEntity
     * - Сохраняет в базу данных с текущей меткой времени
     * - В случае конфликта (трек уже в избранном) обновляет запись
     *
     * @param track Трек для добавления
     */
    suspend fun addToFavorites(track: Track)

    /**
     * Удаляет трек из избранного.
     *
     * Логика работы:
     * - Ищет трек в избранном по trackId
     * - Удаляет соответствующую запись из базы данных
     * - Если трек не найден в избранном, операция игнорируется
     *
     * @param trackId ID трека для удаления
     */
    suspend fun removeFromFavorites(trackId: Int)
}

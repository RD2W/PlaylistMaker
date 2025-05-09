package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.DeleteTrackFromDataBaseUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromFavoritesUseCase
import kotlinx.coroutines.flow.first

/**
 * Реализация UseCase для удаления трека из избранного с интеллектуальным управлением данными.
 *
 * Описание:
 * - Удаляет трек из списка избранных треков
 * - Автоматически проверяет наличие трека в других плейлистах
 * - Полностью удаляет трек из базы данных, если он больше нигде не используется
 *
 * Зависимости:
 * - [deleteTrackUseCase] - UseCase для полного удаления трека из БД
 * - [favoriteTracksRepository] - Репозиторий для работы с избранными треками
 * - [playlistsRepository] - Репозиторий для работы с плейлистами
 */
class RemoveTrackFromFavoritesUseCaseImpl(
    private val deleteTrackUseCase: DeleteTrackFromDataBaseUseCase,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    private val playlistsRepository: PlaylistsRepository,
) : RemoveTrackFromFavoritesUseCase {

    /**
     * Основная операция UseCase - удаление трека из избранного.
     *
     * Логика работы:
     * 1. Удаляет трек из избранного через [favoriteTracksRepository.removeFromFavorites]
     * 2. Проверяет наличие трека в других плейлистах через [smartDeleteTrack]
     * 3. При необходимости полностью удаляет трек из базы данных
     *
     * Особенности:
     * - Использует suspend-функции для асинхронной работы
     * - Автоматически управляет жизненным циклом данных трека
     *
     * @param trackId Уникальный идентификатор трека для удаления
     */
    override suspend operator fun invoke(trackId: Int) {
        favoriteTracksRepository.removeFromFavorites(trackId)
        smartDeleteTrack(trackId)
    }

    /**
     * Интеллектуальная проверка и удаление неиспользуемого трека.
     *
     * Логика работы:
     * 1. Проверяет наличие трека в любых плейлистах через [playlistsRepository.isTrackInAnyPlaylist]
     * 2. Получает актуальное значение через Flow.first()
     * 3. Инициирует удаление трека через [deleteTrackUseCase], если:
     *    - Трек не найден ни в одном плейлисте (!hasInAnyPlaylists)
     *
     * @param trackId Уникальный идентификатор трека для проверки
     */
    private suspend fun smartDeleteTrack(trackId: Int) {
        val hasInAnyPlaylists = playlistsRepository.isTrackInAnyPlaylist(trackId)
            .first() // Получаем актуальное состояние из Flow

        if (!hasInAnyPlaylists) {
            deleteTrackUseCase(trackId)
        }
    }
}

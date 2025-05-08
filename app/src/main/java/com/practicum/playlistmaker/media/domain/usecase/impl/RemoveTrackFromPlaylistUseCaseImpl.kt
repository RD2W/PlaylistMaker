package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.media.domain.usecase.DeleteTrackFromDataBaseUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromPlaylistUseCase
import kotlinx.coroutines.flow.first

/**
 * Реализация UseCase для удаления трека из плейлиста с "умным" удалением из БД.
 *
 * Описание:
 * - Удаляет трек из указанного плейлиста
 * - Автоматически проверяет, используется ли трек в других плейлистах или в избранном
 * - Полностью удаляет трек из базы данных, если он больше нигде не используется
 *
 * Зависимости:
 * - [deleteTrackUseCase] - UseCase для удаления трека из БД
 * - [favoriteTracksRepository] - Репозиторий для работы с избранными треками
 * - [playlistsRepository] - Репозиторий для работы с плейлистами
 */
class RemoveTrackFromPlaylistUseCaseImpl(
    private val deleteTrackUseCase: DeleteTrackFromDataBaseUseCase,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    private val playlistsRepository: PlaylistsRepository,
) : RemoveTrackFromPlaylistUseCase {

    /**
     * Удаляет трек из плейлиста и при необходимости из базы данных.
     *
     * Логика работы:
     * 1. Удаляет трек из указанного плейлиста через [playlistsRepository]
     * 2. Проверяет наличие трека в других плейлистах и избранном
     * 3. Если трек больше нигде не используется - удаляет его из базы данных
     *
     * @param playlistId ID плейлиста, из которого нужно удалить трек
     * @param trackId ID трека для удаления
     */
    override suspend operator fun invoke(playlistId: Long, trackId: Int) {
        playlistsRepository.removeTrackFromPlaylist(playlistId, trackId)
        smartDeleteTrack(trackId)
    }

    /**
     * "Умное" удаление трека: проверяет использование трека перед удалением из БД.
     *
     * Логика работы:
     * 1. Проверяет, есть ли трек в избранном ([favoriteTracksRepository])
     * 2. Проверяет, содержится ли трек в других плейлистах ([playlistsRepository])
     * 3. Удаляет трек из базы данных только если он:
     *    - Не в избранном (!hasInFavorite)
     *    - Не содержится в других плейлистах (!hasInAnyPlaylists)
     *
     * @param trackId ID трека для проверки и удаления
     */
    private suspend fun smartDeleteTrack(trackId: Int) {
        val hasInFavorite = favoriteTracksRepository.isTrackFavorite(trackId)
            .first() // Получаем текущее значение из Flow

        val hasInAnyPlaylists = playlistsRepository.isTrackInAnyPlaylist(trackId)
            .first() // Получаем текущее значение из Flow

        if (!hasInFavorite && !hasInAnyPlaylists) {
            deleteTrackUseCase(trackId)
        }
    }
}

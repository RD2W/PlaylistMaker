package com.practicum.playlistmaker.media.data.repository

import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.media.data.mapper.toFavoriteTrackEntity
import com.practicum.playlistmaker.media.data.mapper.toTrack
import com.practicum.playlistmaker.media.data.mapper.toTrackEntity
import com.practicum.playlistmaker.media.data.mapper.toTrackList
import com.practicum.playlistmaker.media.data.source.local.db.dao.FavoriteTracksDao
import com.practicum.playlistmaker.media.data.source.local.db.dao.TracksDao
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(
    private val tracksDao: TracksDao,
    private val favoriteTracksDao: FavoriteTracksDao,
) : FavoriteTracksRepository {

    /**
     * Возвращает поток списка избранных треков.
     *
     * Логика работы:
     * 1. Вызывает метод `favoriteTracksDao.getFavoriteTracks()`,
     *    который возвращает поток списка сущностей `TrackEntity`.
     * 2. Преобразует каждую сущность `TrackEntity` в доменную модель `Track`
     *    с помощью функции `toTrackList()`.
     */
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getFavoriteTracks().map { trackEntitiesList ->
            trackEntitiesList.toTrackList()
        }
    }

    /**
     * Проверяет, является ли трек избранным, и возвращает результат в виде потока [Flow].
     *
     * Логика работы:
     * 1. Вызывает метод `favoriteTracksDao.isTrackFavorite(trackId)`,
     * который проверяет наличие `trackId` в таблице `favorite_tracks`.
     * 2. Возвращает [Flow<Boolean>], который эмитит `true`, если трек избранный, и `false`, если нет.
     */
    override fun isTrackFavorite(trackId: Int): Flow<Boolean> =
        favoriteTracksDao.isTrackFavorite(trackId)

    /**
     * Возвращает избранный трек по его идентификатору (`trackId`).
     *
     * Логика работы:
     * 1. Вызывает метод `favoriteTracksDao.getFavoriteTrackById(trackId)`,
     *    который пытается найти трек в таблице `favorite_tracks`.
     * 2. Если трек найден, преобразует сущность `TrackEntity` в доменную модель `Track`
     *    с помощью функции `toTrack()`.
     * 3. Если трек не найден, возвращает `null`.
     */
    override suspend fun getFavoriteTrackById(trackId: Int) =
        favoriteTracksDao.getFavoriteTrackById(trackId)?.toTrack()

    /**
     * Добавляет трек в избранное.
     *
     * Логика работы:
     * 1. Преобразует доменную модель [Track] в сущность базы данных [TrackEntity].
     * 2. Проверяет, существует ли трек в таблице `tracks`. Если нет — добавляет его.
     * 3. Добавляет `trackId` в таблицу `favorite_tracks`.
     *
     * Примечание:
     * Если метод [insertTrack] в DAO уже использует стратегию [OnConflictStrategy.REPLACE],
     * то проверка на существование трека через [getTrackById] может быть избыточной.
     * В этом случае метод можно упростить, убрав проверку и сразу вызывая [insertTrack].
     *
     *    override suspend fun addToFavorites(track: Track) {
     *        val trackEntity = track.toTrackEntity()
     *        if (tracksDao.getTrackById(trackEntity.trackId) == null) {
     *            tracksDao.insertTrack(trackEntity)
     *        }
     *
     *        favoriteTracksDao.addFavoriteTrack(track.toFavoriteTrackEntity())
     *    }
     */
    override suspend fun addToFavorites(track: Track) {
        tracksDao.insertTrack(track.toTrackEntity())
        favoriteTracksDao.addFavoriteTrack(track.toFavoriteTrackEntity())
    }

    /**
     * Удаляет трек из списка избранных.
     *
     * Логика работы:
     * 1. Вызывает метод `favoriteTracksDao.removeFromFavorites(trackId)`,
     * который удаляет запись с указанным `trackId` из таблицы `favorite_tracks`.
     * 2. Трек остается в таблице `tracks`, но больше не считается избранным.
     */
    override suspend fun removeFromFavorites(trackId: Int) =
        favoriteTracksDao.removeFromFavorites(trackId)
}

package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.common.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface GetPlaylistsNotContainingTrackUseCase {
    /**
     * Находит плейлисты без указанного трека.
     *
     * @param trackId ID исключаемого трека
     * @param onlyNonEmpty Флаг фильтрации пустых плейлистов
     * @return Flow<List<Playlist>> Поток с подходящими плейлистами.
     *         Эмитит emptyList(), если все плейлисты содержат трек.
     */
    operator fun invoke(
        trackId: Int,
        onlyNonEmpty: Boolean = false,
    ): Flow<List<Playlist>>
}

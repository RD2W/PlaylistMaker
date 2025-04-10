package com.practicum.playlistmaker.media.presentation.state

import com.practicum.playlistmaker.common.domain.model.Track

sealed class FavoriteScreenState {
    data object Loading : FavoriteScreenState() // Состояние загрузки
    data class Content(val tracks: List<Track>) : FavoriteScreenState() // Состояние с данными
    data object Empty : FavoriteScreenState() // Состояние, когда нет избранных треков
    data object Error : FavoriteScreenState() // Состояние ошибки
}

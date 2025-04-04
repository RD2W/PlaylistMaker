package com.practicum.playlistmaker.search.presentation.state

import com.practicum.playlistmaker.common.domain.model.Track

sealed class SearchScreenState {
    data object Idle : SearchScreenState() // Начальное состояние, когда ничего не происходит
    data object Loading : SearchScreenState() // Состояние загрузки
    data class Content(val tracks: List<Track>) : SearchScreenState() // Состояние с данными
    data object NotFound : SearchScreenState() // Состояние, когда ничего не найдено
    data object NetworkError : SearchScreenState() // Состояние ошибки сети
    data class SearchHistory(val history: List<Track>) : SearchScreenState() // Состояние с историей поиска
}

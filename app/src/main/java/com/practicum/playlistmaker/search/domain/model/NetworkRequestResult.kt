package com.practicum.playlistmaker.search.domain.model

sealed class NetworkRequestResult<T>(val data: T, val message: String? = null) {
    class Success<T>(data: T) : NetworkRequestResult<T>(data)
    class Error<T>(data: T, message: String) : NetworkRequestResult<T>(data, message)
    class NoConnection<T>(data: T, message: String) : NetworkRequestResult<T>(data, message)
}

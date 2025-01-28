package com.practicum.playlistmaker.search.domain.model

sealed class Resource<T>(val data: T, val message: String? = null, val isConnected: Boolean = true) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(data: T, message: String): Resource<T>(data, message)
    class NoConnection<T>(data: T, message: String, isConnected: Boolean): Resource<T>(data, message, isConnected)
}
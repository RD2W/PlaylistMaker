package com.practicum.playlistmaker.player.domain.model

sealed class ErrorType {
    data object NoInternet : ErrorType()
    data object PlayerException : ErrorType()
    data object Unknown : ErrorType()
}
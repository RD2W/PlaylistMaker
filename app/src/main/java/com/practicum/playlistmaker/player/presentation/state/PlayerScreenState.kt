package com.practicum.playlistmaker.player.presentation.state

import com.practicum.playlistmaker.player.domain.model.ErrorType

sealed class PlayerScreenState {
    data object Ready : PlayerScreenState()
    data class NotReady(val error: ErrorType) : PlayerScreenState()
    data object Playing : PlayerScreenState()
    data object Paused : PlayerScreenState()
    data object Stopped : PlayerScreenState()
}
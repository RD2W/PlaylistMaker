package com.practicum.playlistmaker.player.presentation.state

sealed class PlayerScreenState {
    data object Ready : PlayerScreenState()
    data object NotReady : PlayerScreenState()
    data object Playing : PlayerScreenState()
    data object Paused : PlayerScreenState()
    data object Stopped : PlayerScreenState()
}
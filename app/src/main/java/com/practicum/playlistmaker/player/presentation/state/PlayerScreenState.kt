package com.practicum.playlistmaker.player.presentation.state

sealed class PlayerScreenState {
    object Ready : PlayerScreenState()
    object NotReady : PlayerScreenState()
    object Playing : PlayerScreenState()
    object Paused : PlayerScreenState()
    object Stopped : PlayerScreenState()
}
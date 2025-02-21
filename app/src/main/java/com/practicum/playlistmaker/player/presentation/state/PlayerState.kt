package com.practicum.playlistmaker.player.presentation.state

import com.practicum.playlistmaker.common.constants.AppConstants.START_TRACK_POSITION
import com.practicum.playlistmaker.common.domain.model.Track

data class PlayerState(
    val track: Track? = null,
    val screenState: PlayerScreenState = PlayerScreenState.Stopped,
    val currentPosition: String = START_TRACK_POSITION,
)

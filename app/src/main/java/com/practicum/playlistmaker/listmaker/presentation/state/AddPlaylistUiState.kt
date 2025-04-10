package com.practicum.playlistmaker.listmaker.presentation.state

import com.practicum.playlistmaker.common.constants.AppConstants.EMPTY_MESSAGE
import java.io.File

sealed class AddPlaylistUiState {
    object Idle : AddPlaylistUiState()
    object Loading : AddPlaylistUiState()
    data class EditMode(
        val playlistId: Long,
        val currentName: String,
        val currentDescription: String,
        val currentCover: File?,
    ) : AddPlaylistUiState()
    data class Success(val playlistId: Long) : AddPlaylistUiState()
    data class Error(val message: String = EMPTY_MESSAGE) : AddPlaylistUiState()
}

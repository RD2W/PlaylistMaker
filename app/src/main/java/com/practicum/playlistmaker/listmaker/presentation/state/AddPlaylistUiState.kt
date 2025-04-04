package com.practicum.playlistmaker.listmaker.presentation.state

sealed class AddPlaylistUiState {
    object Idle : AddPlaylistUiState()
    object Loading : AddPlaylistUiState()
    data class EditMode(
        val playlistId: Long,
        val currentName: String,
        val currentDescription: String,
        val currentCoverPath: String?,
    ) : AddPlaylistUiState()
    data class Success(val playlistId: Long) : AddPlaylistUiState()
    data class Error(val message: String) : AddPlaylistUiState()
}

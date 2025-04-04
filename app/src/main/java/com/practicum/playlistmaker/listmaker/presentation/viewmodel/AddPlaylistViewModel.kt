package com.practicum.playlistmaker.listmaker.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLIST_COVER
import com.practicum.playlistmaker.listmaker.domain.usecase.DeleteTempImageUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SaveTempImageUseCase
import com.practicum.playlistmaker.listmaker.presentation.state.AddPlaylistUiState
import com.practicum.playlistmaker.media.domain.usecase.CreatePlaylistUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class AddPlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val saveTempImageUseCase: SaveTempImageUseCase,
    private val deleteTempImageUseCase: DeleteTempImageUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPlaylistUiState>(AddPlaylistUiState.Idle)
    val uiState: StateFlow<AddPlaylistUiState> get() = _uiState

    private val _coverImage = MutableStateFlow<File?>(null)
    val coverImage: StateFlow<File?> get() = _coverImage

    private var selectedImageUri: Uri? = null

    fun setSelectedImageUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _coverImage.value = saveTempImageUseCase(uri)
                selectedImageUri = Uri.fromFile(_coverImage.value)
            } catch (e: Exception) {
                Log.e(PLAYLIST_COVER, "Failed to load image preview", e)
            }
        }
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = AddPlaylistUiState.Loading
            createPlaylistUseCase(name, description, selectedImageUri)
                .onSuccess { playlistId ->
                    _uiState.value = AddPlaylistUiState.Success(playlistId)
                }
                .onFailure { e ->
                    _uiState.value = AddPlaylistUiState.Error(e.message ?: "Unknown error")
                }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            _coverImage.value?.let { deleteTempImageUseCase(it) }
        }
        super.onCleared()
    }
}

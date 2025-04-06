package com.practicum.playlistmaker.listmaker.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.common.constants.AppConstants.NEW_PLAYLIST_ID
import com.practicum.playlistmaker.common.constants.LogTags.PLAYLIST_COVER
import com.practicum.playlistmaker.listmaker.domain.usecase.DeleteTempImageUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SaveTempImageUseCase
import com.practicum.playlistmaker.listmaker.presentation.state.AddPlaylistUiState
import com.practicum.playlistmaker.media.domain.model.CoverAction
import com.practicum.playlistmaker.media.domain.usecase.CreatePlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.EditPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import kotlin.Long

/**
 * ViewModel для создания/редактирования плейлиста.
 *
 * Особенности управления состоянием обложки:
 *
 * 1. **Разделение состояний**:
 *    - Основное состояние экрана (режим редактирования/создания) хранится в `_uiState`
 *    - Текущая выбранная обложка (временная или постоянная) хранится в `_coverImage`
 *
 * 2. **Почему отдельный StateFlow для обложки**:
 *    - Частые изменения: обложка может меняться многократно без изменения других данных
 *    - Производительность: избегаем полного пересоздания UIState при каждом изменении изображения
 *    - Временное хранение: позволяет preview обложки перед окончательным сохранением
 *
 * 3. **Синхронизация состояний**:
 *    - При редактировании учитывается как текущая обложка (из `EditMode`),
 *      так и новая (из `_coverImage`)
 *    - Флаг `shouldRemoveCover` отслеживает намерение удалить существующую обложку
 *
 * 4. **Очистка ресурсов**:
 *    - Временные файлы обложек автоматически удаляются при очистке ViewModel
 */
class AddPlaylistViewModel(
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val editPlaylistUseCase: EditPlaylistUseCase,
    private val saveTempImageUseCase: SaveTempImageUseCase,
    private val deleteTempImageUseCase: DeleteTempImageUseCase,
    private val getPlaylistByIdUseCase: GetPlaylistByIdUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<AddPlaylistUiState>(AddPlaylistUiState.Idle)
    val uiState: StateFlow<AddPlaylistUiState> get() = _uiState

    /**
     * Текущее URI выбранного изображения (может быть null, если обложка не выбрана или удалена)
     *
     * Используется для:
     * - Отслеживания выбранного пользователем изображения
     * - Сохранения в постоянное хранилище при подтверждении
     */
    private var selectedImageUri: Uri? = null

    /**
     * Текущая обложка плейлиста в виде File объекта.
     *
     * Содержит:
     * - Для нового плейлиста: временный файл выбранной обложки
     * - Для редактируемого плейлиста: новую обложку (если была изменена)
     * - null если обложка отсутствует или была удалена
     */
    private val _coverImage = MutableStateFlow<File?>(null)
    val coverImage: StateFlow<File?> get() = _coverImage

    /**
     * Флаг наличия обложки у плейлиста.
     *
     * Учитывает:
     * - Для нового плейлиста: наличие временной обложки в `_coverImage`
     * - Для редактируемого плейлиста: оригинальную обложку или новую выбранную
     *
     * Используется для:
     * - Активации/деактивации функций связанных с обложкой
     * - Визуальной индикации состояния
     */
    val hasCover: StateFlow<Boolean> = combine(_uiState, _coverImage) { state, coverImage ->
        when (state) {
            is AddPlaylistUiState.EditMode -> state.currentCover != null || coverImage != null
            else -> coverImage != null
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Текущий плейлист
    private var currentPlaylistId: Long = NEW_PLAYLIST_ID
    private var currentCoverPath: String? = null
    private var shouldRemoveCover: Boolean = false

    // Установка новой обложки
    fun setSelectedImageUri(uri: Uri) {
        viewModelScope.launch {
            try {
                _coverImage.value = saveTempImageUseCase(uri)
                selectedImageUri = uri
                shouldRemoveCover = false // Сброс флага удаления при выборе новой обложки
            } catch (e: Exception) {
                Log.e(PLAYLIST_COVER, "Failed to load image preview", e)
                _uiState.value = AddPlaylistUiState.Error()
            }
        }
    }

    // Удаление обложки
    fun removeCoverImage() {
        Log.d(PLAYLIST_COVER, "Удаление обложки, текущий ID: $currentPlaylistId")
        viewModelScope.launch {
            if (currentPlaylistId != NEW_PLAYLIST_ID) {
                when (val current = _uiState.value) {
                    is AddPlaylistUiState.EditMode -> {
                        _uiState.value = current.copy(currentCover = null)
                    }
                    else -> Unit
                }
                shouldRemoveCover = true // Устанавливаем флаг удаления
            }
            _coverImage.value?.let { deleteTempImageUseCase(it) }
            _coverImage.value = null
            selectedImageUri = null
        }
    }

    // Загрузка плейлиста для редактирования
    fun loadPlaylist(playlistId: Long) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            _uiState.value = AddPlaylistUiState.Loading
            getPlaylistByIdUseCase(playlistId)
                .catch { e ->
                    Log.e("AddPlaylistVM", "Ошибка загрузки плейлиста ID: $playlistId", e)
                    _uiState.value = AddPlaylistUiState.Error()
                }
                .collect { playlist ->
                    playlist?.let {
                        currentCoverPath = it.coverFilePath
                        _uiState.value = AddPlaylistUiState.EditMode(
                            playlistId = it.playlistId,
                            currentName = it.name,
                            currentDescription = it.description,
                            currentCover = it.coverFilePath?.let { path -> File(path) },
                        )
                    } ?: run {
                        Log.w("AddPlaylistVM", "Плейлист не найден, ID: $playlistId")
                        _uiState.value = AddPlaylistUiState.Error()
                    }
                }
        }
    }

    // Сохранение плейлиста
    fun savePlaylist(name: String, description: String) {
        when {
            name.isBlank() -> {
                Log.w("AddPlaylistVM", "Попытка сохранения с пустым названием")
                _uiState.value = AddPlaylistUiState.Error()
                return
            }
            currentPlaylistId == NEW_PLAYLIST_ID -> createPlaylist(name, description)
            currentPlaylistId > 0 -> updatePlaylist(name, description)
            else -> _uiState.value = AddPlaylistUiState.Error()
        }
    }

    private fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = AddPlaylistUiState.Loading
            createPlaylistUseCase(name, description, selectedImageUri)
                .onSuccess { playlistId ->
                    _uiState.value = AddPlaylistUiState.Success(playlistId)
                    clearTempData()
                }
                .onFailure { e ->
                    Log.e("AddPlaylistVM", "Ошибка создания плейлиста", e)
                    _uiState.value = AddPlaylistUiState.Error()
                }
        }
    }

    private fun updatePlaylist(name: String, description: String) {
        viewModelScope.launch {
            _uiState.value = AddPlaylistUiState.Loading

            // Определяем действие с обложкой
            val coverAction = when {
                shouldRemoveCover -> CoverAction.Remove
                else -> selectedImageUri?.let { CoverAction.Update(it) } ?: CoverAction.Keep
            }

            editPlaylistUseCase(
                playlistId = currentPlaylistId,
                name = name,
                description = description.takeIf { it.isNotEmpty() },
                coverAction = coverAction,
            )
                .onSuccess { playlistId ->
                    _uiState.value = AddPlaylistUiState.Success(playlistId)
                    clearTempData()
                }
                .onFailure { e ->
                    Log.e("AddPlaylistVM", "Ошибка обновления плейлиста ID: $currentPlaylistId", e)
                    _uiState.value = AddPlaylistUiState.Error()
                }
        }
    }

    private fun clearTempData() {
        viewModelScope.launch {
            _coverImage.value?.let { deleteTempImageUseCase(it) }
            _coverImage.value = null
            selectedImageUri = null
            currentCoverPath = null
            shouldRemoveCover = false
        }
    }

    override fun onCleared() {
        clearTempData()
        super.onCleared()
    }
}

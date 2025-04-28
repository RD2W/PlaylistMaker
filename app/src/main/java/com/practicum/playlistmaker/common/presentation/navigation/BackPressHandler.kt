package com.practicum.playlistmaker.common.presentation.navigation

/**
 * Интерфейс для обработки нажатия кнопки "Назад" во фрагментах.
 * @return true если событие обработано, false для стандартной обработки
 */
interface BackPressHandler {
    fun handleBackPressed(): Boolean
}

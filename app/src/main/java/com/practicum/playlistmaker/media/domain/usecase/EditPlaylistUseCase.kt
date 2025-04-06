package com.practicum.playlistmaker.media.domain.usecase

import com.practicum.playlistmaker.media.domain.model.CoverAction

/**
 * UseCase для редактирования плейлиста с типобезопасным управлением обложкой.
 *
 * @param playlistId ID редактируемого плейлиста
 * @param name Новое название (null - не изменять)
 * @param description Новое описание (null - не изменять)
 * @param coverAction Действие с обложкой:
 *                    - CoverAction.Keep: оставить текущую (по умолчанию)
 *                    - CoverAction.Remove: удалить текущую
 *                    - CoverAction.Update(uri): установить новую из Uri
 * @return Result<Long> с ID плейлиста или ошибкой
 *
 * Особенности:
 * - Автоматически управляет файлами обложек (удаление старых, сохранение новых)
 * - Валидирует входные параметры
 * - Обрабатывает ошибки и возвращает в Result
 *
 * Примеры:
 * 1. invoke(123, coverAction = CoverAction.Remove) - удалить обложку
 * 2. invoke(123, name = "New", coverAction = CoverAction.Update(uri)) - изменить название и обложку
 */
interface EditPlaylistUseCase {
    suspend operator fun invoke(
        playlistId: Long,
        name: String? = null,
        description: String? = null,
        coverAction: CoverAction = CoverAction.Keep,
    ): Result<Long>
}

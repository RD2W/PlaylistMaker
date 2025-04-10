package com.practicum.playlistmaker.media.domain.model

import android.net.Uri

/**
 * Типобезопасное описание действий с обложкой плейлиста.
 *
 * Используется в EditPlaylistUseCase для явного указания:
 * - Keep: оставить текущую обложку без изменений
 * - Remove: удалить текущую обложку
 * - Update: установить новую обложку из указанного Uri
 *
 * Примеры:
 * 1. CoverAction.Keep - оставить как есть
 * 2. CoverAction.Remove - удалить обложку
 * 3. CoverAction.Update(newImageUri) - установить новую обложку
 */
sealed class CoverAction {
    /** Оставить текущую обложку без изменений */
    object Keep : CoverAction()

    /** Удалить текущую обложку (если существует) */
    object Remove : CoverAction()

    /**
     * Установить новую обложку
     * @property uri Uri нового изображения для обложки
     */
    data class Update(val uri: Uri) : CoverAction()
}

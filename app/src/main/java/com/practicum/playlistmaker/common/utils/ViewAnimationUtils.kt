package com.practicum.playlistmaker.common.utils

import android.view.View
import com.practicum.playlistmaker.common.constants.AppConstants.ANIMATION_DURATION_MILLIS
import com.practicum.playlistmaker.common.constants.AppConstants.ANIMATION_SHAKE_OFFSET

/**
 * Анимация "отказа" в виде горизонтального покачивания
 */
fun View.shake(
    offset: Float = ANIMATION_SHAKE_OFFSET,
    duration: Long = ANIMATION_DURATION_MILLIS,
) {
    animate()
        .translationXBy(offset)
        .setDuration(duration)
        .withEndAction {
            animate()
                .translationXBy(-offset)
                .start()
        }
        .start()
}

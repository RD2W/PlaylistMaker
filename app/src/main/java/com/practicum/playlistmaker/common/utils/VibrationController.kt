package com.practicum.playlistmaker.common.utils

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import com.practicum.playlistmaker.common.constants.AppConstants.VIBRATION_DURATION_MILLIS
import com.practicum.playlistmaker.common.constants.LogTags.VIBRATION

class VibrationController(private val context: Context) {

    private val vibratorManager: VibratorManager by lazy {
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    }

    private val vibrator get() = vibratorManager.defaultVibrator

    /**
     * Воспроизводит вибрацию с указанной длительностью
     */
    fun vibrate(durationMillis: Long = VIBRATION_DURATION_MILLIS) {
        try {
            if (vibrator.hasVibrator()) {
                val effect =
                    VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(effect)
            }
        } catch (e: Exception) {
            Log.e(VIBRATION, "Vibration failed", e)
        }
    }

    /**
     * Воспроизводит стандартный тактильный эффект
     */
    fun vibrateClick() {
        try {
            if (vibrator.hasVibrator()) {
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                vibrator.vibrate(effect)
            }
        } catch (e: Exception) {
            Log.e(VIBRATION, "Predefined effect failed", e)
        }
    }

    /**
     * Проверяет доступность вибрации
     */
    fun hasVibrator(): Boolean = vibrator.hasVibrator()
}

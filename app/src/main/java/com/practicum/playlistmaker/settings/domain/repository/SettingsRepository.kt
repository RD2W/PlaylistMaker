package com.practicum.playlistmaker.settings.domain.repository

import android.content.Intent

interface SettingsRepository {
    fun shareApp(): Intent
    fun writeSupport(): Intent
    fun openUserAgreement(): Intent
}
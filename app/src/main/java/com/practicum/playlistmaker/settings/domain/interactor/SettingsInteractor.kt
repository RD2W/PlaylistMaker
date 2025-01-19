package com.practicum.playlistmaker.settings.domain.interactor

import android.content.Intent

interface SettingsInteractor {
    fun shareApp(): Intent
    fun writeSupport(): Intent
    fun openUserAgreement(): Intent
}
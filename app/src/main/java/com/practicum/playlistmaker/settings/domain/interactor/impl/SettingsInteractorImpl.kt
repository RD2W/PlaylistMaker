package com.practicum.playlistmaker.settings.domain.interactor.impl

import android.content.Intent
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun shareApp(): Intent {
        return settingsRepository.shareApp()
    }

    override fun writeSupport(): Intent {
        return settingsRepository.writeSupport()
    }

    override fun openUserAgreement(): Intent {
        return settingsRepository.openUserAgreement()
    }
}
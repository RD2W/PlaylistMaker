package com.practicum.playlistmaker.settings.domain.interactor.impl

import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) : SettingsInteractor {
    override fun shareApp() {
        settingsRepository.shareApp()
    }

    override fun writeSupport() {
        settingsRepository.writeSupport()
    }

    override fun openUserAgreement() {
        settingsRepository.openUserAgreement()
    }
}
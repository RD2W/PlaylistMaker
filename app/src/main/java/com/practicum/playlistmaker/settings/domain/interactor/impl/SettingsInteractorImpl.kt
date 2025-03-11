package com.practicum.playlistmaker.settings.domain.interactor.impl

import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun shareApp(appLink: String, appShareMsg: String) {
        settingsRepository.shareApp(appLink, appShareMsg)
    }

    override fun writeSupport(email: String, emailSupportTitle: String, emailSupportMsg: String) {
        settingsRepository.writeSupport(email, emailSupportTitle, emailSupportMsg)
    }

    override fun openUserAgreement(userAgreementLink: String) {
        settingsRepository.openUserAgreement(userAgreementLink)
    }
}
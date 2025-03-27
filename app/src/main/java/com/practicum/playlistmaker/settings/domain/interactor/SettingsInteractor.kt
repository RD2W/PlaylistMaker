package com.practicum.playlistmaker.settings.domain.interactor

interface SettingsInteractor {
    fun shareApp(appLink: String, appShareMsg: String)
    fun writeSupport(email: String, emailSupportTitle: String, emailSupportMsg: String)
    fun openUserAgreement(userAgreementLink: String)
}
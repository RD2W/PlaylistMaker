package com.practicum.playlistmaker.settings.domain.repository

interface SettingsRepository {
    fun shareApp(appLink: String, appShareMsg: String)
    fun writeSupport(email: String, emailSupportTitle: String, emailSupportMsg: String)
    fun openUserAgreement(userAgreementLink: String)
}
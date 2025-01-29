package com.practicum.playlistmaker.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor

class SettingsViewModelFactory(
    private val appThemeInteractor: AppThemeInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(appThemeInteractor, settingsInteractor) as? T
                    ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
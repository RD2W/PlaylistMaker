package com.practicum.playlistmaker.settings.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.presentation.state.SettingsScreenState

class SettingsViewModel(
    private val appThemeInteractor: AppThemeInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _screenState = MutableLiveData<SettingsScreenState>()
    val screenState: LiveData<SettingsScreenState> get() = _screenState

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        val currentTheme = appThemeInteractor.getCurrentTheme()
        _screenState.value = SettingsScreenState.ThemeSwitcherState(currentTheme)
    }

    fun switchTheme(isChecked: Boolean) {
        appThemeInteractor.switchTheme(isChecked)
        _screenState.value = SettingsScreenState.ThemeSwitcherState(isChecked)
    }

    fun shareApp() {
        settingsInteractor.shareApp()
    }

    fun writeSupport() {
        settingsInteractor.writeSupport()
    }

    fun openUserAgreement() {
        settingsInteractor.openUserAgreement()
    }
}
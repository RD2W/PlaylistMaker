package com.practicum.playlistmaker.settings.presentation.state

sealed class SettingsScreenState {
    data class ThemeSwitcherState(val isThemeChecked: Boolean) : SettingsScreenState()
    data object Error : SettingsScreenState()
}

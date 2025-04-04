package com.practicum.playlistmaker.common.domain.interactor

interface AppThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun switchTheme(isDark: Boolean)
}

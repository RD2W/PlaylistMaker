package com.practicum.playlistmaker.common.domain.repository

interface AppThemeRepository {
    fun getCurrentTheme(): Boolean
    fun switchTheme(isDark: Boolean)
}

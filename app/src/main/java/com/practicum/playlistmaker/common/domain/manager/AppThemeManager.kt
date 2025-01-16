package com.practicum.playlistmaker.common.domain.manager

interface AppThemeManager {
    fun getCurrentTheme(): Boolean
    fun switchTheme(isDark: Boolean)
}
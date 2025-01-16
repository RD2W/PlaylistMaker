package com.practicum.playlistmaker.common.domain.interactor.impl

import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.common.domain.repository.AppThemeRepository

class AppThemeInteractorImpl(private val appThemeRepository: AppThemeRepository) : AppThemeInteractor {
    override fun getCurrentTheme(): Boolean {
        return appThemeRepository.getCurrentTheme()
    }

    override fun switchTheme(isDark: Boolean) {
        appThemeRepository.switchTheme(isDark)
    }
}
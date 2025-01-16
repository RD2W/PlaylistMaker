package com.practicum.playlistmaker.common.di

import android.content.Context
import android.content.SharedPreferences
import com.practicum.playlistmaker.common.data.manager.AppThemeManagerImpl
import com.practicum.playlistmaker.common.domain.manager.AppThemeManager

object AppDependencyCreator {
    fun provideThemeManager(sharedPreferences: SharedPreferences, context: Context): AppThemeManager {
        return AppThemeManagerImpl(sharedPreferences, context)
    }
}
package com.practicum.playlistmaker.common.di

import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.common.domain.interactor.impl.AppThemeInteractorImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.interactor.impl.SettingsInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val domainModule = module {
    singleOf(::AppThemeInteractorImpl) { bind<AppThemeInteractor>() }
    factoryOf(::PlayerInteractorImpl) { bind<PlayerInteractor>() }
    singleOf(::SearchHistoryInteractorImpl) { bind<SearchHistoryInteractor>() }
    singleOf(::SettingsInteractorImpl) { bind<SettingsInteractor>() }
    singleOf(::TracksInteractorImpl) { bind<TracksInteractor>() }
}
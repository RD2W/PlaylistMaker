package com.practicum.playlistmaker.common.di

import com.practicum.playlistmaker.listmaker.presentation.viewmodel.AddPlaylistViewModel
import com.practicum.playlistmaker.media.presentation.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.media.presentation.viewmodel.PlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.viewmodel.PlayerViewModel
import com.practicum.playlistmaker.playlist.presentation.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.search.presentation.viewmodel.SearchViewModel
import com.practicum.playlistmaker.settings.presentation.viewmodel.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PlayerViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::FavoritesViewModel)
    viewModelOf(::PlaylistViewModel)
    viewModelOf(::PlaylistsViewModel)
    viewModelOf(::AddPlaylistViewModel)
}

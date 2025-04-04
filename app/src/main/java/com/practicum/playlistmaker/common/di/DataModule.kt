package com.practicum.playlistmaker.common.di

import androidx.media3.exoplayer.ExoPlayer
import com.practicum.playlistmaker.common.data.repository.AppThemeRepositoryImpl
import com.practicum.playlistmaker.common.domain.repository.AppThemeRepository
import com.practicum.playlistmaker.listmaker.data.repository.ImageRepositoryImpl
import com.practicum.playlistmaker.listmaker.data.repository.PlaylistCoverRepositoryImpl
import com.practicum.playlistmaker.listmaker.domain.repository.ImageRepository
import com.practicum.playlistmaker.listmaker.domain.repository.PlaylistCoverRepository
import com.practicum.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.practicum.playlistmaker.media.data.repository.PlaylistsRepositoryImpl
import com.practicum.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.practicum.playlistmaker.media.domain.repository.PlaylistsRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.PlayerRepository
import com.practicum.playlistmaker.playlist.data.repository.SharePlaylistRepositoryImpl
import com.practicum.playlistmaker.playlist.domain.repository.SharePlaylistRepository
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    factory { ExoPlayer.Builder(androidApplication()).build() }
    singleOf(::AppThemeRepositoryImpl) { bind<AppThemeRepository>() }
    factoryOf(::PlayerRepositoryImpl) { bind<PlayerRepository>() }
    singleOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
    singleOf(::SearchHistoryRepositoryImpl) { bind<SearchHistoryRepository>() }
    singleOf(::TracksRepositoryImpl) { bind<TracksRepository>() }
    factoryOf(::FavoriteTracksRepositoryImpl) { bind<FavoriteTracksRepository>() }
    factoryOf(::PlaylistsRepositoryImpl) { bind<PlaylistsRepository>() }
    factoryOf(::PlaylistCoverRepositoryImpl) { bind<PlaylistCoverRepository>() }
    factoryOf(::SharePlaylistRepositoryImpl) { bind<SharePlaylistRepository>() }
    factoryOf(::ImageRepositoryImpl) { bind<ImageRepository>() }
}

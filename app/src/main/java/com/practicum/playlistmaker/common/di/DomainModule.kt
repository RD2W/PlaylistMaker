package com.practicum.playlistmaker.common.di

import com.practicum.playlistmaker.common.domain.interactor.AppThemeInteractor
import com.practicum.playlistmaker.common.domain.interactor.impl.AppThemeInteractorImpl
import com.practicum.playlistmaker.listmaker.domain.usecase.DeletePlaylistCoverUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.DeleteTempImageUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.GetPlaylistCoverUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SavePlaylistCoverUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.SaveTempImageUseCase
import com.practicum.playlistmaker.listmaker.domain.usecase.impl.DeletePlaylistCoverUseCaseImpl
import com.practicum.playlistmaker.listmaker.domain.usecase.impl.DeleteTempImageUseCaseImpl
import com.practicum.playlistmaker.listmaker.domain.usecase.impl.GetPlaylistCoverUseCaseImpl
import com.practicum.playlistmaker.listmaker.domain.usecase.impl.SavePlaylistCoverUseCaseImpl
import com.practicum.playlistmaker.listmaker.domain.usecase.impl.SaveTempImageUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToFavoritesUseCase
import com.practicum.playlistmaker.media.domain.usecase.AddTrackToPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.CreatePlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.DeletePlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.DeleteTrackFromDataBaseUseCase
import com.practicum.playlistmaker.media.domain.usecase.EditPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetFavoriteTrackByIdUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetFavoriteTracksUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistByIdUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistTracksUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsNotContainingTrackUseCase
import com.practicum.playlistmaker.media.domain.usecase.GetPlaylistsUseCase
import com.practicum.playlistmaker.media.domain.usecase.IsTrackFavoriteUseCase
import com.practicum.playlistmaker.media.domain.usecase.IsTrackInAnyPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromFavoritesUseCase
import com.practicum.playlistmaker.media.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.practicum.playlistmaker.media.domain.usecase.impl.AddTrackToFavoritesUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.AddTrackToPlaylistUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.CreatePlaylistUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.DeletePlaylistUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.DeleteTrackFromDataBaseUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.EditPlaylistUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetFavoriteTrackByIdUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetFavoriteTracksUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetPlaylistByIdUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetPlaylistTracksUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetPlaylistsNotContainingTrackUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.GetPlaylistsUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.IsTrackFavoriteUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.IsTrackInAnyPlaylistUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.RemoveTrackFromFavoritesUseCaseImpl
import com.practicum.playlistmaker.media.domain.usecase.impl.RemoveTrackFromPlaylistUseCaseImpl
import com.practicum.playlistmaker.player.domain.interactor.PlayerInteractor
import com.practicum.playlistmaker.player.domain.interactor.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.playlist.domain.usecase.SharePlaylistUseCase
import com.practicum.playlistmaker.playlist.domain.usecase.impl.SharePlaylistUseCaseImpl
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.interactor.impl.SettingsInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::AppThemeInteractorImpl) { bind<AppThemeInteractor>() }
    factoryOf(::PlayerInteractorImpl) { bind<PlayerInteractor>() }
    factoryOf(::SearchHistoryInteractorImpl) { bind<SearchHistoryInteractor>() }
    factoryOf(::SettingsInteractorImpl) { bind<SettingsInteractor>() }
    factoryOf(::TracksInteractorImpl) { bind<TracksInteractor>() }

    /** Tracks UseCases */
    factoryOf(::DeleteTrackFromDataBaseUseCaseImpl) { bind<DeleteTrackFromDataBaseUseCase>() }

    /** Favorite Tracks UseCases */
    factoryOf(::AddTrackToFavoritesUseCaseImpl) { bind<AddTrackToFavoritesUseCase>() }
    factoryOf(::GetFavoriteTrackByIdUseCaseImpl) { bind<GetFavoriteTrackByIdUseCase>() }
    factoryOf(::GetFavoriteTracksUseCaseImpl) { bind<GetFavoriteTracksUseCase>() }
    factoryOf(::IsTrackFavoriteUseCaseImpl) { bind<IsTrackFavoriteUseCase>() }
    factoryOf(::RemoveTrackFromFavoritesUseCaseImpl) { bind<RemoveTrackFromFavoritesUseCase>() }

    /** Playlist UseCases */
    factoryOf(::SavePlaylistCoverUseCaseImpl) { bind<SavePlaylistCoverUseCase>() }
    factoryOf(::DeletePlaylistCoverUseCaseImpl) { bind<DeletePlaylistCoverUseCase>() }
    factoryOf(::GetPlaylistCoverUseCaseImpl) { bind<GetPlaylistCoverUseCase>() }
    factoryOf(::SharePlaylistUseCaseImpl) { bind<SharePlaylistUseCase>() }
    factoryOf(::DeleteTempImageUseCaseImpl) { bind<DeleteTempImageUseCase>() }
    factoryOf(::SaveTempImageUseCaseImpl) { bind<SaveTempImageUseCase>() }

    /** Playlists UseCases */
    factoryOf(::AddTrackToPlaylistUseCaseImpl) { bind<AddTrackToPlaylistUseCase>() }
    factoryOf(::CreatePlaylistUseCaseImpl) { bind<CreatePlaylistUseCase>() }
    factoryOf(::DeletePlaylistUseCaseImpl) { bind<DeletePlaylistUseCase>() }
    factoryOf(::EditPlaylistUseCaseImpl) { bind<EditPlaylistUseCase>() }
    factoryOf(::GetPlaylistByIdUseCaseImpl) { bind<GetPlaylistByIdUseCase>() }
    factoryOf(::GetPlaylistTracksUseCaseImpl) { bind<GetPlaylistTracksUseCase>() }
    factoryOf(::GetPlaylistsUseCaseImpl) { bind<GetPlaylistsUseCase>() }
    factoryOf(::GetPlaylistsNotContainingTrackUseCaseImpl) { bind<GetPlaylistsNotContainingTrackUseCase>() }
    factoryOf(::IsTrackInAnyPlaylistUseCaseImpl) { bind<IsTrackInAnyPlaylistUseCase>() }
    factoryOf(::RemoveTrackFromPlaylistUseCaseImpl) { bind<RemoveTrackFromPlaylistUseCase>() }
}

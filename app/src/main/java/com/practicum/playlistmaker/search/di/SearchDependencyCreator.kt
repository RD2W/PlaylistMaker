package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.repository.TracksRepository

object SearchDependencyCreator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient)
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}
package com.practicum.playlistmaker.search.di

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.source.remote.RetrofitClient
import com.practicum.playlistmaker.search.domain.interactor.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.interactor.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.interactor.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.repository.TracksRepository

object SearchDependencyCreator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient)
    }

    private fun getSearchHistoryRepository(sharedPreferences: SharedPreferences, gson: Gson): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(sharedPreferences, gson)
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(sharedPreferences: SharedPreferences): SearchHistoryInteractor {
        val gson = Gson()
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(sharedPreferences, gson))
    }
}
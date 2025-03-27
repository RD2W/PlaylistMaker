package com.practicum.playlistmaker.search.domain.interactor.impl

import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.repository.TracksRepository

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String) = repository.searchTracks(expression)
}
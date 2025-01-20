package com.practicum.playlistmaker.search.domain.interactor.impl

import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            val foundTracks = repository.searchTracks(expression)
            consumer.consume(foundTracks)
        }
    }
}
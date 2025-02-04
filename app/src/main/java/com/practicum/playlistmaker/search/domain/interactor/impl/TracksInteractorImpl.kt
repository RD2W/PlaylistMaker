package com.practicum.playlistmaker.search.domain.interactor.impl

import com.practicum.playlistmaker.search.domain.interactor.TracksInteractor
import com.practicum.playlistmaker.search.domain.model.NetworkRequestResult
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val resource = repository.searchTracks(expression)) {
                is NetworkRequestResult.Success -> consumer.consume(
                    resource.data,
                    null,
                    resource.isConnected
                )

                is NetworkRequestResult.Error -> consumer.consume(
                    resource.data,
                    resource.message,
                    resource.isConnected
                )

                is NetworkRequestResult.NoConnection -> consumer.consume(
                    resource.data,
                    resource.message,
                    resource.isConnected
                )
            }
        }
    }
}
package com.practicum.playlistmaker.search.domain.interactor

import com.practicum.playlistmaker.common.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun interface TracksConsumer {
        fun consume(foundTracks: List<Track>, errorMessage: String?, isConnected: Boolean)
    }
}
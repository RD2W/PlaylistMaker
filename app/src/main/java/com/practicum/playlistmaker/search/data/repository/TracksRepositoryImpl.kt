package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.common.constants.AppConstants.NOT_AVAILABLE
import com.practicum.playlistmaker.common.constants.ResponseCode.RESPONSE_OK
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.formatDateToYear
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.search.data.model.TracksSearchRequest
import com.practicum.playlistmaker.search.data.model.TracksSearchResponse
import com.practicum.playlistmaker.search.data.source.remote.NetworkClient
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override suspend fun searchTracks(expression: String): List<Track> {
        return withContext(Dispatchers.IO) {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            if (response.resultCode == RESPONSE_OK) {
                (response as TracksSearchResponse).results.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTime?.let { time -> formatDurationToMMSS(time) } ?: NOT_AVAILABLE,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate?.let { formatDateToYear(it) } ?: NOT_AVAILABLE,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                    )
                }
            } else {
                emptyList()
            }
        }
    }
}
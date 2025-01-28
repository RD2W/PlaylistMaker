package com.practicum.playlistmaker.search.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.AppConstants.NOT_AVAILABLE
import com.practicum.playlistmaker.common.constants.ResponseCode.RESPONSE_OK
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.common.utils.NetworkUtils
import com.practicum.playlistmaker.common.utils.formatDateToYear
import com.practicum.playlistmaker.common.utils.formatDurationToMMSS
import com.practicum.playlistmaker.search.data.model.TracksSearchRequest
import com.practicum.playlistmaker.search.data.model.TracksSearchResponse
import com.practicum.playlistmaker.search.data.source.remote.NetworkClient
import com.practicum.playlistmaker.search.domain.model.Resource
import com.practicum.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(
    private val context: Context,
    private val networkClient: NetworkClient,
) : TracksRepository {
    override suspend fun searchTracks(expression: String): Resource<List<Track>> {
        if (!NetworkUtils.isNetworkAvailable(context)) return Resource.NoConnection(
            emptyList(),
            context.getString(R.string.no_internet_connection),
            false
        )

        return withContext(Dispatchers.IO) {
            val response = networkClient.doRequest(TracksSearchRequest(expression))
            if (response.resultCode == RESPONSE_OK) {
                val trackList = (response as TracksSearchResponse).results.map { it ->
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTime?.let { time -> formatDurationToMMSS(time) } ?: NOT_AVAILABLE,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate?.let { data -> formatDateToYear(data) } ?: NOT_AVAILABLE,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                    )
                }
                Resource.Success(trackList)
            } else {
                Resource.Error(
                    emptyList(),
                    context.getString(R.string.server_error)
                )
            }
        }
    }
}
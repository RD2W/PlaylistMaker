package com.practicum.playlistmaker.search.data.source.local.sprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.constants.AppConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.search.data.source.local.LocalClient

class SharedPreferencesClient(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : LocalClient {
    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > AppConstants.MAX_TRACKS_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit { remove(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY) }
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit { putString(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY, json) }
    }
}

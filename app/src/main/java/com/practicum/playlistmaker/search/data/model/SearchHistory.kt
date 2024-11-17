package com.practicum.playlistmaker.search.data.model

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.constants.AppConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants

object SearchHistory {

    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    fun init(sharedPreferences: SharedPreferences) {
        sharedPrefs = sharedPreferences
    }

    fun getHistory(): List<Track> {
        val json = sharedPrefs.getString(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        if (history.size > AppConstants.MAX_TRACKS_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history)
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY, json).apply()
    }
}
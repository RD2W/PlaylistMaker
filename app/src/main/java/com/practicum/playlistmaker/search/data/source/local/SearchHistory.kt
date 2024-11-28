package com.practicum.playlistmaker.search.data.source.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.common.constants.AppConstants
import com.practicum.playlistmaker.common.constants.PrefsConstants
import com.practicum.playlistmaker.search.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SearchHistory {

    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    private val _historyFlow = MutableStateFlow<List<Track>>(emptyList())
    val historyFlow: StateFlow<List<Track>> get() = _historyFlow

    fun init(sharedPreferences: SharedPreferences) {
        sharedPrefs = sharedPreferences
        _historyFlow.value = getHistory()
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
        _historyFlow.value = history
    }

    fun clearHistory() {
        sharedPrefs.edit().remove(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY).apply()
        _historyFlow.value = emptyList()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit().putString(PrefsConstants.KEY_TRACKS_SEARCH_HISTORY, json).apply()
    }
}
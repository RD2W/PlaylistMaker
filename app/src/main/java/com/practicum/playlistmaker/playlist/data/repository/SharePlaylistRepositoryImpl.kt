package com.practicum.playlistmaker.playlist.data.repository

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.domain.model.Track
import com.practicum.playlistmaker.playlist.domain.repository.SharePlaylistRepository

class SharePlaylistRepositoryImpl(
    private val context: Context,
) : SharePlaylistRepository {
    override fun setPlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        tracksCount: Int,
        tracksList: List<Track>,
    ) {
        val sharingText = buildString {
            appendPlaylistInfo(
                playlistName,
                playlistDescription,
                tracksCount,
            )
            appendTracks(tracksList)
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sharingText)
        }

        val chooserIntent = Intent.createChooser(shareIntent, playlistName).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        startActivitySafely(chooserIntent)
    }

    private fun StringBuilder.appendPlaylistInfo(
        playlistName: String,
        playlistDescription: String,
        tracksCount: Int,
    ) {
        val tracksCountText = context.resources?.getQuantityString(
            R.plurals.tracks_plurals,
            tracksCount,
            tracksCount,
        )
        appendLine(playlistName)
        if (playlistDescription.isNotBlank()) {
            appendLine(playlistDescription)
        }
        appendLine(tracksCountText)
    }

    private fun StringBuilder.appendTracks(tracks: List<Track>) {
        tracks.forEachIndexed { index, track ->
            append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n")
        }
    }

    private fun startActivitySafely(intent: Intent) {
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e(LogTags.EXTERNAL_NAVIGATION, "No activity found to handle intent", e)
            }
        } else {
            Log.e(
                LogTags.EXTERNAL_NAVIGATION,
                "No activity found to handle intent: ${intent.action}",
            )
        }
    }
}

package com.practicum.playlistmaker.common.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDurationToMMSS(durationInMillis: Long): String {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(durationInMillis)
}
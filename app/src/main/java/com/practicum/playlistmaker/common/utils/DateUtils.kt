package com.practicum.playlistmaker.common.utils

import com.practicum.playlistmaker.common.constants.AppConstants.MILLISECONDS_PER_MINUTE
import com.practicum.playlistmaker.common.constants.AppConstants.ROUNDING_OFFSET
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDurationToMMSS(durationInMillis: Long): String {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(durationInMillis)
}

fun formatDateToYear(date: String): String {
    val dateTime = ZonedDateTime.parse(date)
    return dateTime.year.toString()
}

fun getCurrentYearAsString(): String {
    return Instant.ofEpochMilli(System.currentTimeMillis())
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy"))
}

fun convertMillisToMinutes(milliseconds: Long): Int {
    return (milliseconds / MILLISECONDS_PER_MINUTE + ROUNDING_OFFSET).toInt()
}

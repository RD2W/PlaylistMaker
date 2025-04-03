package com.practicum.playlistmaker.common.utils

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

fun convertMillisToMinutes(milliseconds: Long) = (milliseconds / 60_000).toInt()
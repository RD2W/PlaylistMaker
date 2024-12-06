package com.practicum.playlistmaker.common.utils

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale

fun formatDurationToMMSS(durationInMillis: Long): String {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(durationInMillis)
}

fun formatDateToYear(date: String): String{
    val dateTime = ZonedDateTime.parse(date)
    return dateTime.year.toString()
}
package com.example.fitnesstracker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Returns a new Date object representing the same date but with time set to 00:00:00.
 */
fun Date.dateWithoutTime(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * Formats a Date object to a user-friendly string (e.g., "MMM dd, yyyy").
 */
fun Date.toFormattedDateString(): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Formats a Date object to a user-friendly time string (e.g., "hh:mm a").
 */
fun Date.toFormattedTimeString(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Formats a duration in milliseconds to a string like "Xh Ym" or "Ym Zs" or "Xs".
 */
fun Long.formatDuration(): String {
    if (this < 0) return "0s"

    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    return when {
        hours > 0 -> String.format("%dh %02dm", hours, minutes)
        minutes > 0 -> String.format("%dm %02ds", minutes, seconds)
        else -> String.format("%ds", seconds)
    }
}

/**
 * Formats a distance in meters to a string like "X.Y km" or "Z m".
 */
fun Double.formatDistance(preferredUnits: String = "metric"): String {
    return if (preferredUnits == "metric") {
        if (this >= 1000) {
            String.format(Locale.getDefault(), "%.2f km", this / 1000.0)
        } else {
            String.format(Locale.getDefault(), "%.0f m", this)
        }
    } else { // Imperial
        val miles = this * 0.000621371
        if (miles >= 0.1) {
            String.format(Locale.getDefault(), "%.2f mi", miles)
        } else {
            val feet = this * 3.28084
            String.format(Locale.getDefault(), "%.0f ft", feet)
        }
    }
}

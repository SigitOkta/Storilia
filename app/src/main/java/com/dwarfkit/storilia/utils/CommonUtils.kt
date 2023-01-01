package com.dwarfkit.storilia.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.text.SimpleDateFormat
import java.util.*


fun getAddress(lat: Double, lng: Double, context: Context): String {
    return when(isValidLatLang(lat, lng)){
        true -> {
            val geocoder = Geocoder(context, Locale.getDefault())
            var cityName = "-"
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
            if(addresses.isNotEmpty()) {
                cityName= addresses[0].adminArea
            }
            cityName
        }
        false -> {
            "Invalid Coordinate"
        }
    }
}

fun isValidLatLang(lat: Double?, lng: Double?): Boolean {
    return lat?.toInt() in -90 until 90 && lng?.toInt() in -180 until 180
}

private const val SECOND = 1
private const val MINUTE = 60 * SECOND
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR
private const val MONTH = 30 * DAY
private const val YEAR = 12 * MONTH

private fun currentDate(): Long {
    val calendar = Calendar.getInstance()
    return calendar.timeInMillis
}

fun String.toTimeAgo(): String {
    val timeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    timeFormat.timeZone =  TimeZone.getTimeZone("GMT")
    val time = timeFormat.parse(this)?.time ?: throw IllegalArgumentException("Invalid time string")

    val now = currentDate()
    // convert back to second
    val diff = (now - time) / 1000
    return when {
        diff < MINUTE -> "Just now"
        diff < 2 * MINUTE -> "a minute ago"
        diff < 60 * MINUTE -> "${diff / MINUTE} minutes ago"
        diff < 2 * HOUR -> "an hour ago"
        diff < 24 * HOUR -> "${diff / HOUR} hours ago"
        diff < 2 * DAY -> "yesterday"
        diff < 30 * DAY -> "${diff / DAY} days ago"
        diff < 2 * MONTH -> "a month ago"
        diff < 12 * MONTH -> "${diff / MONTH} months ago"
        diff < 2 * YEAR -> "a year ago"
        else -> "${diff / YEAR} years ago"
    }
}

package com.ayia.rider

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun getMilliFromDate(dateFormat: String): Long {
    var date = Date()

    val formatter = SimpleDateFormat("MM/dd/yyyy HH:mm aaa", Locale.getDefault())
    try {
        date = formatter.parse(dateFormat)

    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return date.time
}


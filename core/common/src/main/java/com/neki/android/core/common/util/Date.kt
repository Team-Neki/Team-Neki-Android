package com.neki.android.core.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toFormattedDate(pattern: String = "yyyy.MM.dd"): String {
    val dateTime = LocalDateTime.parse(this)
    return dateTime.format(DateTimeFormatter.ofPattern(pattern))
}

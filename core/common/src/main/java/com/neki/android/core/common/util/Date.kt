package com.neki.android.core.common.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * ISO-8601 형식의 날짜 문자열을 지정된 패턴으로 변환
 *
 * @param pattern 출력 패턴 (기본값: "yyyy.MM.dd")
 * @return 변환된 날짜 문자열
 *
 * 예시:
 * - Input: "2026-01-22T14:40:33.313120"
 * - Output: "2026.01.22"
 */
fun String.toFormattedDate(pattern: String = "yyyy.MM.dd"): String {
    return try {
        val dateTime = LocalDateTime.parse(this)
        dateTime.format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: DateTimeParseException) {
        this
    }
}

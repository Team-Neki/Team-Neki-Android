package com.neki.android.core.common.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
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

fun String.toRelativeTime(now: LocalDateTime = LocalDateTime.now()): String {
    return try {
        val createdAt = try {
            LocalDateTime.parse(this)
        } catch (e: DateTimeParseException) {
            OffsetDateTime.parse(this)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        }
        val elapsedSeconds = Duration.between(createdAt, now).seconds.coerceAtLeast(0)

        when {
            elapsedSeconds < 60 -> "방금 전"
            elapsedSeconds < 60 * 60 -> "${elapsedSeconds / 60}분 전"
            elapsedSeconds < 60 * 60 * 24 -> "${elapsedSeconds / (60 * 60)}시간 전"
            elapsedSeconds < 60 * 60 * 24 * 30 -> "${elapsedSeconds / (60 * 60 * 24)}일 전"
            elapsedSeconds < 60 * 60 * 24 * 365 -> "${elapsedSeconds / (60 * 60 * 24 * 30)}달전"
            else -> "${elapsedSeconds / (60 * 60 * 24 * 365)}년 전"
        }
    } catch (e: DateTimeParseException) {
        this
    }
}

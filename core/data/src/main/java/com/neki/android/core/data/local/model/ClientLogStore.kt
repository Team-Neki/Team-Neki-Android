package com.neki.android.core.data.local.model

import com.neki.android.core.model.analytics.ClientLogDraft
import com.neki.android.core.model.analytics.ClientLogEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal const val LOG_RETENTION_MILLIS = 7L * 24 * 60 * 60 * 1000
internal const val MAX_STORED_LOGS = 5_000
internal const val MAX_STORE_BYTES = 10 * 1024 * 1024
internal const val MAX_LOG_BYTES = 999 * 1024 // Leave room for the server's NDJSON envelope.

// Always three fraction digits, as iOS sends; Instant.toString() drops them on whole seconds.
private val OCCURRED_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC)

@Serializable
internal data class ClientLogStore(
    val events: List<ClientLogEvent> = emptyList(),
    val sessionId: Long = 0,
    val sequenceNumber: Int = 0,
) {
    /** The session comes from Amplitude; the sequence restarts at 1 whenever the session changes. */
    fun append(draft: ClientLogDraft): Pair<ClientLogStore, ClientLogEvent> {
        require(draft.eventType.isNotBlank() && draft.eventType.length <= 64)
        require(draft.deviceId.isNotBlank() && draft.appVersion.isNotBlank())
        require(draft.sessionId > 0)
        val sameSession = draft.sessionId == sessionId && sequenceNumber < Int.MAX_VALUE
        val event = ClientLogEvent(
            eventId = draft.eventId,
            eventType = draft.eventType,
            eventProperties = draft.eventProperties,
            occurredAt = OCCURRED_AT_FORMAT.format(Instant.ofEpochMilli(draft.occurredAtMillis)),
            sessionId = draft.sessionId,
            sequenceNumber = if (sameSession) sequenceNumber + 1 else 1,
            deviceId = draft.deviceId,
            userIdAtOccurrence = draft.userIdAtOccurrence,
            appVersion = draft.appVersion,
            context = draft.context,
        )
        require(Json.encodeToString(event).toByteArray().size <= MAX_LOG_BYTES) { "Oversized client log" }
        return copy(
            events = events + event,
            sessionId = event.sessionId,
            sequenceNumber = event.sequenceNumber,
        ) to event
    }

    fun prune(now: Long): ClientLogPruneResult {
        val unexpired = events.filter {
            Instant.parse(it.occurredAt).toEpochMilli() >= now - LOG_RETENTION_MILLIS
        }
        val retained = unexpired.takeLast(MAX_STORED_LOGS)
        var bytes = 1024
        val bounded = retained.asReversed().takeWhile {
            bytes += Json.encodeToString(it).toByteArray().size + 1
            bytes <= MAX_STORE_BYTES
        }.asReversed()
        return ClientLogPruneResult(
            store = copy(events = bounded),
            expired = events.size - unexpired.size,
            overCount = unexpired.size - retained.size,
            overSize = retained.size - bounded.size,
        )
    }
}

internal data class ClientLogPruneResult(
    val store: ClientLogStore,
    val expired: Int,
    val overCount: Int,
    val overSize: Int,
)

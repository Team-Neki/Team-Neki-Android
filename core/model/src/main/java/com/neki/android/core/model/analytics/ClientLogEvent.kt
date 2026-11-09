package com.neki.android.core.model.analytics

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ClientLogEvent(
    val eventId: String,
    val eventType: String,
    val eventProperties: JsonObject,
    val occurredAt: String,
    val sessionId: Long,
    val sequenceNumber: Int,
    val deviceId: String,
    val userIdAtOccurrence: String?,
    val appVersion: String,
    val context: JsonObject,
)

data class ClientLogDraft(
    val eventId: String,
    val eventType: String,
    val eventProperties: JsonObject,
    val occurredAtMillis: Long,
    val sessionId: Long,
    val deviceId: String,
    val userIdAtOccurrence: String?,
    val appVersion: String,
    val context: JsonObject,
)

data class ClientLogWithdrawal(val userId: String, val eventId: String)

package com.neki.android.core.analytics.logger

import com.amplitude.android.Amplitude
import com.amplitude.core.events.EventOptions
import javax.inject.Inject

internal interface AmplitudeAnalyticsClient {
    fun track(eventName: String, eventProperties: Map<String, Any?>)
    fun track(eventName: String, eventProperties: Map<String, Any?>, insertId: String, timestamp: Long) {
        track(eventName, eventProperties)
    }
    suspend fun awaitDeviceId(): String = error("SDK device ID unavailable")
    suspend fun awaitUserId(): String? = null
    fun currentSessionId(): Long = -1
    fun currentUserId(): String? = null
    fun startNewSession() {}
    fun setUserId(userId: String?)
}

internal class AmplitudeSdkAnalyticsClient @Inject constructor(
    private val amplitude: Amplitude,
) : AmplitudeAnalyticsClient {

    override fun track(eventName: String, eventProperties: Map<String, Any?>) {
        amplitude.track(eventName, eventProperties)
    }

    override fun track(eventName: String, eventProperties: Map<String, Any?>, insertId: String, timestamp: Long) {
        amplitude.track(eventName, eventProperties, EventOptions().apply {
            this.insertId = insertId
            this.timestamp = timestamp
        })
    }

    override suspend fun awaitDeviceId(): String {
        amplitude.isBuilt.await()
        return checkNotNull(amplitude.getDeviceId()).also { check(it.isNotBlank()) }
    }

    override fun currentSessionId(): Long = amplitude.sessionId

    override fun currentUserId(): String? = amplitude.getUserId()

    // The Android SDK has no public setSessionId; the timeline switches to the session_start event's session.
    override fun startNewSession() {
        val now = System.currentTimeMillis()
        amplitude.track(
            Amplitude.START_SESSION_EVENT,
            options = EventOptions().apply {
                sessionId = now
                timestamp = now
            },
        )
    }

    override suspend fun awaitUserId(): String? {
        amplitude.isBuilt.await()
        return amplitude.getUserId()
    }

    override fun setUserId(userId: String?) {
        amplitude.setUserId(userId)
    }
}

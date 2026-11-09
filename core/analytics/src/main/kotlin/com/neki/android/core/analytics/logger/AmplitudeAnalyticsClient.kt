package com.neki.android.core.analytics.logger

import com.amplitude.android.Amplitude
import com.amplitude.core.events.BaseEvent
import com.amplitude.core.events.EventOptions
import javax.inject.Inject

internal interface AmplitudeAnalyticsClient {
    fun track(eventName: String, eventProperties: Map<String, Any?>)
    fun track(eventName: String, eventProperties: Map<String, Any?>, insertId: String, timestamp: Long) {
        track(eventName, eventProperties)
    }
    suspend fun awaitDeviceId(): String = error("SDK device ID unavailable")
    suspend fun awaitInitialUserId(): String? = null
    fun currentSessionId(): Long = -1
    fun currentUserId(): String? = null
    fun startNewSession(previousUserId: String) {}
    fun setUserId(userId: String?)
}

internal class AmplitudeSdkAnalyticsClient @Inject constructor(
    private val amplitude: Amplitude,
) : AmplitudeAnalyticsClient {

    @Volatile
    private var initialUser: InitialUser? = null

    override fun track(eventName: String, eventProperties: Map<String, Any?>) {
        amplitude.track(eventName, eventProperties)
    }

    override fun track(eventName: String, eventProperties: Map<String, Any?>, insertId: String, timestamp: Long) {
        val identityKnown = amplitude.isBuilt.isCompleted || initialUser != null
        val userId = amplitude.getUserId()
        val resolveInitialUser = if (!identityKnown && userId == null) ::captureInitialUserId else null
        val event = AmplitudeOccurrenceEvent(userId, resolveInitialUser).apply {
            eventType = eventName
            this.eventProperties = eventProperties.toMutableMap()
            this.insertId = insertId
            this.timestamp = timestamp
        }
        amplitude.track(event)
    }

    override suspend fun awaitDeviceId(): String {
        amplitude.isBuilt.await()
        return checkNotNull(amplitude.getDeviceId()).also { check(it.isNotBlank()) }
    }

    override fun currentSessionId(): Long = amplitude.sessionId

    override fun currentUserId(): String? = amplitude.getUserId()

    // The Android SDK has no public setSessionId; the timeline switches to the session_start event's session.
    // Like iOS setSessionId, the previous session gets a session_end first.
    override fun startNewSession(previousUserId: String) {
        val now = System.currentTimeMillis()
        if (amplitude.sessionId > 0) {
            amplitude.track(Amplitude.END_SESSION_EVENT, options = EventOptions().apply {
                userId = previousUserId
            })
        }
        amplitude.track(AmplitudeOccurrenceEvent(amplitude.getUserId()).apply {
            eventType = Amplitude.START_SESSION_EVENT
            sessionId = now
            timestamp = now
        })
    }

    override suspend fun awaitInitialUserId(): String? {
        amplitude.isBuilt.await()
        return captureInitialUserId()
    }

    override fun setUserId(userId: String?) {
        // Startup events may still be queued when the app sets or clears its first user ID.
        captureInitialUserId()
        amplitude.setUserId(userId)
    }

    @Synchronized
    private fun captureInitialUserId(): String? {
        initialUser?.let { return it.id }
        return amplitude.getUserId().also { initialUser = InitialUser(it) }
    }

    private class InitialUser(val id: String?)
}

internal class AmplitudeOccurrenceEvent(
    private val userIdAtOccurrence: String?,
    private val resolveInitialUser: (() -> String?)? = null,
) : BaseEvent() {
    init {
        userId = userIdAtOccurrence
    }

    fun restoreUserIdAtOccurrence() {
        // The SDK's context plugin fills an empty user ID from the current identity.
        userId = userIdAtOccurrence ?: resolveInitialUser?.invoke()
    }
}

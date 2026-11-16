package com.neki.android.core.analytics.logger

import com.amplitude.core.Amplitude
import com.amplitude.core.events.BaseEvent
import com.amplitude.core.platform.Plugin
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hands the session ID Amplitude assigned to an event over to the server log of the same event.
 * Amplitude decides the session in its own queue after `track`, so it cannot be read at log time.
 */
@Singleton
internal class AmplitudeSessionRegistry @Inject constructor() {
    private val pending = ConcurrentHashMap<String, CompletableDeferred<Long>>()

    // After a wait times out, skip waiting until Amplitude reports a session again.
    @Volatile
    private var stalled = false

    fun expect(eventId: String) {
        pending[eventId] = CompletableDeferred()
    }

    fun cancel(eventId: String) {
        pending.remove(eventId)
    }

    fun complete(eventId: String, sessionId: Long) {
        stalled = false
        pending[eventId]?.complete(sessionId)
    }

    suspend fun await(eventId: String): Long? {
        val session = pending[eventId] ?: return null
        return try {
            if (stalled && !session.isCompleted) return null
            withTimeoutOrNull(SESSION_WAIT_MILLIS) { session.await() }.also { stalled = it == null }
        } finally {
            pending.remove(eventId)
        }
    }

    private companion object {
        const val SESSION_WAIT_MILLIS = 2_000L
    }
}

/** Runs after the Android timeline assigned the session, so `event.sessionId` is final here. */
internal class AmplitudeSessionPlugin(
    private val registry: AmplitudeSessionRegistry,
) : Plugin {
    override val type = Plugin.Type.Enrichment
    override lateinit var amplitude: Amplitude

    override fun execute(event: BaseEvent): BaseEvent {
        if (event is AmplitudeOccurrenceEvent) event.restoreUserIdAtOccurrence()
        val eventId = event.insertId
        val sessionId = event.sessionId
        if (eventId != null && sessionId != null && sessionId > 0) registry.complete(eventId, sessionId)
        return event
    }
}

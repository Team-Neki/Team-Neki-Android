package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent
import java.util.UUID
import javax.inject.Inject

/** Sends each event to Amplitude and the server with the same event ID, occurrence time and session. */
internal class NekiAnalyticsLogger(
    private val amplitudeLogger: AmplitudeAnalyticsLogger,
    private val serverLogger: ServerAnalyticsLogger,
    private val sessions: AmplitudeSessionRegistry,
    private val now: () -> Long,
) : AnalyticsLogger {

    @Inject
    constructor(
        amplitudeLogger: AmplitudeAnalyticsLogger,
        serverLogger: ServerAnalyticsLogger,
        sessions: AmplitudeSessionRegistry,
    ) : this(amplitudeLogger, serverLogger, sessions, System::currentTimeMillis)

    override fun log(event: AnalyticsEvent) {
        val eventId = UUID.randomUUID().toString()
        val occurredAt = now()
        sessions.expect(eventId)
        amplitudeLogger.log(event, eventId, occurredAt)
        serverLogger.log(event, eventId, occurredAt)
    }

    override fun setUserId(userId: String) {
        amplitudeLogger.setUserId(userId)
        serverLogger.setUserId(userId)
    }

    override fun clearUserId() {
        amplitudeLogger.clearUserId()
        serverLogger.clearUserId()
    }

    override suspend fun flush() {
        serverLogger.flush()
    }

    override fun deletePendingUserEvents() {
        serverLogger.deletePendingUserEvents()
    }
}

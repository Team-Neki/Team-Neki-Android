package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent
import javax.inject.Inject

internal class AmplitudeAnalyticsLogger @Inject constructor(
    private val amplitudeClient: AmplitudeAnalyticsClient,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        amplitudeClient.track(
            eventName = event.name,
            eventProperties = event.params,
        )
    }

    fun log(event: AnalyticsEvent, eventId: String, occurredAt: Long) {
        amplitudeClient.track(
            eventName = event.name,
            eventProperties = event.params,
            insertId = eventId,
            timestamp = occurredAt,
        )
    }

    // Sessions belong to the signed-in member: signing out or switching members opens a new session, as on iOS.
    // Signing in from a signed-out state keeps the session.
    override fun setUserId(userId: String) {
        val previous = amplitudeClient.currentUserId()
        amplitudeClient.setUserId(userId)
        if (previous != null && previous != userId) amplitudeClient.startNewSession()
    }

    override fun clearUserId() {
        val previous = amplitudeClient.currentUserId()
        amplitudeClient.setUserId(null)
        if (previous != null) amplitudeClient.startNewSession()
    }
}

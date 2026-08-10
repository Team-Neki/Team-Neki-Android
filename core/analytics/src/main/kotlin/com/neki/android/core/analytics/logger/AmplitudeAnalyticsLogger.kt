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

    override fun setUserId(userId: String) {
        amplitudeClient.setUserId(userId)
    }

    override fun clearUserId() {
        amplitudeClient.setUserId(null)
    }
}

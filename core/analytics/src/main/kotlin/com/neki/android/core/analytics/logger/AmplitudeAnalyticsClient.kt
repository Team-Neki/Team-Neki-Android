package com.neki.android.core.analytics.logger

import com.amplitude.android.Amplitude
import javax.inject.Inject

internal interface AmplitudeAnalyticsClient {
    fun track(eventName: String, eventProperties: Map<String, Any?>)
    fun setUserId(userId: String?)
}

internal class AmplitudeSdkAnalyticsClient @Inject constructor(
    private val amplitude: Amplitude,
) : AmplitudeAnalyticsClient {

    override fun track(eventName: String, eventProperties: Map<String, Any?>) {
        amplitude.track(eventName, eventProperties)
    }

    override fun setUserId(userId: String?) {
        amplitude.setUserId(userId)
    }
}

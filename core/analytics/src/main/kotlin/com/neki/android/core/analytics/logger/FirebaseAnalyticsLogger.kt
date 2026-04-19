package com.neki.android.core.analytics.logger

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.neki.android.core.analytics.AnalyticsEvent
import javax.inject.Inject

internal class FirebaseAnalyticsLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {

    override fun log(event: AnalyticsEvent) {
        val bundle = Bundle().apply {
            event.params.forEach { (key, value) -> putString(key, value) }
        }
        firebaseAnalytics.logEvent(event.name, bundle)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String) {
        firebaseAnalytics.setUserProperty(key, value)
    }
}

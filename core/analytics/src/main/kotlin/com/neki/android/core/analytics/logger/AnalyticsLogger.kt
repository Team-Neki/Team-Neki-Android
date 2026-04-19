package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
    fun resetAnalytics()
}

package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.AnalyticsEvent

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
}

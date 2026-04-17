package com.neki.android.core.analytics

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun setUserProperty(key: String, value: String)
}

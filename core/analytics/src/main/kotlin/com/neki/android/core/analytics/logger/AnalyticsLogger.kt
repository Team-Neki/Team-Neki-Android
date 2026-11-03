package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun clearUserId()
    suspend fun flush() {}

    /** Drops the current user's logs that are not yet sent. Call after account deletion, before its event. */
    fun deletePendingUserEvents() {}
}

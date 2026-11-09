package com.neki.android.core.analytics.logger

import com.neki.android.core.analytics.event.AnalyticsEvent
import com.neki.android.core.analytics.event.MypageAnalyticsEvent
import com.neki.android.core.model.analytics.ClientLogWithdrawal

interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)
    fun setUserId(userId: String)
    fun clearUserId()
    suspend fun flush() {}

    /** Clears the previous analytics identity and returns the queue cleanup marker to save with credential removal. */
    suspend fun completeWithdrawal(): ClientLogWithdrawal? {
        log(MypageAnalyticsEvent.Withdraw)
        clearUserId()
        return null
    }
}

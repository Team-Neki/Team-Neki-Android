package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.analytics.ClientLogDraft
import com.neki.android.core.model.analytics.ClientLogEvent

interface ClientLogRepository {
    val retryAtMillis: Long
    fun setUserId(userId: String?)
    suspend fun enqueue(draft: ClientLogDraft): Result<ClientLogEvent>
    suspend fun pendingCount(): Int
    suspend fun storedTokenOwner(): String?
    fun beginWithdrawal()
    suspend fun deleteUserEvents(userId: String, withdrawalEventId: String): Result<Unit>
    fun endWithdrawal()

    /** [ignoreBackoff] sends even while waiting to retry; used when the app goes to the background. */
    suspend fun flush(ignoreBackoff: Boolean = false): Result<Unit>
}

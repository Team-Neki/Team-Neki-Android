package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.analytics.ClientLogDraft
import com.neki.android.core.model.analytics.ClientLogEvent

interface ClientLogRepository {
    val retryAtMillis: Long
    fun setUserId(userId: String?)
    suspend fun enqueue(draft: ClientLogDraft): Result<ClientLogEvent>
    suspend fun pendingCount(): Int
    suspend fun flush(): Result<Unit>
}

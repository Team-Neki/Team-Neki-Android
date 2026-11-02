package com.neki.android.core.data.remote.model.request

import com.neki.android.core.model.analytics.ClientLogEvent
import kotlinx.serialization.Serializable

@Serializable
internal data class ClientLogRequest(
    val platform: String,
    val logs: List<ClientLogEvent>,
)

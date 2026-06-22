package com.neki.android.core.data.remote.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateNotificationRequest(
    @SerialName("deviceToken") val deviceToken: String,
    @SerialName("pushAgreed") val pushAgreed: Boolean,
)

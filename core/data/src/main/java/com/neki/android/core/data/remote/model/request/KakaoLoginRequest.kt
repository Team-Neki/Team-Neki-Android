package com.neki.android.core.data.remote.model.request

import com.neki.android.core.model.Login
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoLoginRequest(
    @SerialName("idToken") val idToken: String,
)

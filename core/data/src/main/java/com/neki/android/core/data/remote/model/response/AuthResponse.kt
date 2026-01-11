package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.Auth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
) {
    fun toModel() = Auth(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
    )
}

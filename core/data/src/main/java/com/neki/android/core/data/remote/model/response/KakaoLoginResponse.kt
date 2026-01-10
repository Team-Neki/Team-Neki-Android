package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.Login
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoLoginResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
) {
    fun toModel() = Login(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
    )
}

package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.UserInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    @SerialName("userId") val userId: Long = 0L,
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("profileImageUrl") val profileImageUrl: String = "",
    @SerialName("providerType") val providerType: String = "",
    @SerialName("agreeTerms") val agreeTerms: Boolean = false,
) {
    fun toModel() = UserInfo(
        id = userId,
        nickname = name,
        profileImageUrl = profileImageUrl,
        loginType = providerType,
        isRequiredTermsAgreed = agreeTerms,
    )
}

package com.neki.android.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserInfo(
    val id: Long = 0L,
    val nickname: String = "",
    val profileImageUrl: String = "",
    val loginType: String = "",
    val agreeTerms: Boolean = false,
)

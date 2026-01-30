package com.neki.android.core.model

data class User(
    val id: Long = 0L,
    val nickname: String = "",
    val profileImageUrl: String = "",
    val loginType: String = "",
)

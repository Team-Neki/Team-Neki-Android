package com.neki.android.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Notification(
    val id: Long = 0L,
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val link: String = "",
    val createdAt: String = "",
)

package com.neki.android.core.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Photo(
    val id: Long = 0L,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val date: String = "",
)

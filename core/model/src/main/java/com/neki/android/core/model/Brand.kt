package com.neki.android.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Brand(
    val isChecked: Boolean = false,
    val id: Long = 0L,
    val name: String = "",
    val code: String = "",
    val imageUrl: String = "",
)

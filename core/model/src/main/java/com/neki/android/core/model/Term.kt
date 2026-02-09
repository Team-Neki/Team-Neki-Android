package com.neki.android.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class Term(
    val id: Long = 0L,
    val title: String = "",
    val url: String = "",
    val isRequired: Boolean = false,
    val isChecked: Boolean = false,
)

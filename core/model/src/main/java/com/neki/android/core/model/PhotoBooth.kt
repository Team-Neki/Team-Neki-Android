package com.neki.android.core.model

import androidx.compose.runtime.Immutable

@Immutable
data class PhotoBooth(
    val isFocused: Boolean = false,
    val isCheckedBrand: Boolean = true,
    val id: Long = 0L,
    val brandName: String = "",
    val branchName: String = "",
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val distance: Int = 0,
    val imageUrl: String = "",
)

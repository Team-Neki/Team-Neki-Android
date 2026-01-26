package com.neki.android.core.model

data class PhotoBooth(
    val isFocused: Boolean = false,
    val id: Long = 0L,
    val brandName: String = "",
    val branchName: String = "",
    val address: String = "",
    val longitude: Double = 0.0,
    val latitude: Double = 0.0,
    val distance: Int = 0,
    val imageUrl: String = "",
)

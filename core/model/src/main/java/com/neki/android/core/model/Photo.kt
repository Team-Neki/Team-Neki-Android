package com.neki.android.core.model

import java.time.LocalDate

data class Photo(
    val id: Long = 0L,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val date: LocalDate = LocalDate.now(),
)

package com.neki.android.core.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class Photo @OptIn(ExperimentalTime::class) constructor(
    val id: Long = 0L,
    val imageUrl: String = "",
    val isFavorite: Boolean = false,
    val date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
)

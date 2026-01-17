package com.neki.android.core.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Album(
    val id: Long = 0L,
    val title: String = "",
    val photoList: ImmutableList<Photo> = persistentListOf(),
)

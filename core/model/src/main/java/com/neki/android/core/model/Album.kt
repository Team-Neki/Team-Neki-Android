package com.neki.android.core.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class Album(
    val id: Long = 0L,
    val title: String = "",
    val thumbnailUrl: String? = null,
    val photoList: ImmutableList<Photo> = persistentListOf(),
)

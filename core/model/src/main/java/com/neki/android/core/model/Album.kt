package com.neki.android.core.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Album(
    val id: Long = 0L,
    val title: String = "",
    val thumbnailUrl: String? = null,
    val photoList: ImmutableList<Photo> = persistentListOf(),
)

@Serializable
@Immutable
data class AlbumPreview(
    val id: Long = 0L,
    val title: String = "",
    val thumbnailUrl: String? = null,
    val photoCount: Int = 0,
)

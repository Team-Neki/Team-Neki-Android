package com.neki.android.feature.archive.api

sealed interface ArchiveResult {
    data class PhotoDeleted(val photoId: List<Long>) : ArchiveResult {
        constructor(photoId: Long) : this(listOf(photoId))
    }

    data class FavoriteChanged(val photoId: Long, val isFavorite: Boolean) : ArchiveResult
}

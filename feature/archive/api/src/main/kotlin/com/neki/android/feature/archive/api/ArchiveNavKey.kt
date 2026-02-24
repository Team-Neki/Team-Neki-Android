package com.neki.android.feature.archive.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface ArchiveNavKey : NavKey {

    @Serializable
    data object Archive : ArchiveNavKey

    @Serializable
    data object AllPhoto : ArchiveNavKey

    @Serializable
    data object AllAlbum : ArchiveNavKey

    @Serializable
    data class AlbumDetail(
        val isFavorite: Boolean,
        val title: String,
        val albumId: Long,
    ) : ArchiveNavKey

    @Serializable
    data class PhotoDetail(
        val photos: List<Photo>,
        val initialIndex: Int,
        val hasNext: Boolean = false,
        val folderId: Long? = null,
        val sortOrder: SortOrder = SortOrder.DESC,
        val isFavoriteOnly: Boolean = false,
    ) : ArchiveNavKey
}

fun MainNavigator.navigateToArchive() {
    navigate(ArchiveNavKey.Archive)
}

fun MainNavigator.navigateToAllPhoto() {
    navigate(ArchiveNavKey.AllPhoto)
}

fun MainNavigator.navigateToAllAlbum() {
    navigate(ArchiveNavKey.AllAlbum)
}

fun MainNavigator.navigateToAlbumDetail(id: Long, title: String = "", isFavorite: Boolean = false) {
    navigate(ArchiveNavKey.AlbumDetail(isFavorite, title, id))
}

fun MainNavigator.navigateToPhotoDetail(
    photos: List<Photo>,
    initialIndex: Int,
    hasNext: Boolean = false,
    folderId: Long? = null,
    sortOrder: SortOrder = SortOrder.DESC,
    isFavoriteOnly: Boolean = false,
) {
    navigate(
        ArchiveNavKey.PhotoDetail(
            photos = photos,
            initialIndex = initialIndex,
            hasNext = hasNext,
            folderId = folderId,
            sortOrder = sortOrder,
            isFavoriteOnly = isFavoriteOnly,
        ),
    )
}

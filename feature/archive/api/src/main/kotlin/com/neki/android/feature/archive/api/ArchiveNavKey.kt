package com.neki.android.feature.archive.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.Photo
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
    data class PhotoDetail(val photos: List<Photo>, val initialIndex: Int) : ArchiveNavKey
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

fun MainNavigator.navigateToPhotoDetail(photos: List<Photo>, initialIndex: Int) {
    navigate(ArchiveNavKey.PhotoDetail(photos, initialIndex))
}

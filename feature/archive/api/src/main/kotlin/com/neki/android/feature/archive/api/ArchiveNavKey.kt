package com.neki.android.feature.archive.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.Photo
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface ArchiveNavKey : NavKey {

    @Serializable
    data object Archive : ArchiveNavKey

    @Serializable
    data object AllPhoto : ArchiveNavKey

    @Serializable
    data object AllAlbum : ArchiveNavKey

    @Serializable
    data class AlbumDetail(val isFavorite: Boolean, val albumId: Long) : ArchiveNavKey

    @Serializable
    data class PhotoDetail(val photo: Photo) : ArchiveNavKey
}

fun Navigator.navigateToArchive() {
    navigate(ArchiveNavKey.Archive)
}

fun Navigator.navigateToAllPhoto() {
    navigate(ArchiveNavKey.AllPhoto)
}

fun Navigator.navigateToAllAlbum() {
    navigate(ArchiveNavKey.AllAlbum)
}

fun Navigator.navigateToAlbumDetail(id: Long, isFavorite: Boolean = false) {
    navigate(ArchiveNavKey.AlbumDetail(isFavorite, id))
}

fun Navigator.navigateToPhotoDetail(photo: Photo) {
    navigate(ArchiveNavKey.PhotoDetail(photo))
}

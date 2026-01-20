package com.neki.android.feature.archive.impl.main

import android.net.Uri
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ArchiveMainState(
    val isFirstEntered: Boolean = true,
    val favoriteAlbum: Album = Album(),
    val albums: ImmutableList<Album> = persistentListOf(),
    val recentPhotos: ImmutableList<Photo> = persistentListOf(),
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val showAddDialog: Boolean = false,
    val showChooseWithAlbumDialog: Boolean = false,
    val showAddAlbumBottomSheet: Boolean = false,
)

sealed interface ArchiveMainIntent {
    data object EnterArchiveMainScreen : ArchiveMainIntent
    data object ClickScreen : ArchiveMainIntent
    data object ClickGoToTopButton : ArchiveMainIntent

    // TopBar Intent
    data object ClickAddIcon : ArchiveMainIntent
    data object DismissAddDialog : ArchiveMainIntent
    data object ClickQRScanRow : ArchiveMainIntent

    data object ClickGalleryUploadRow : ArchiveMainIntent
    data class SelectGalleryImage(val uris: List<Uri>) : ArchiveMainIntent
    data object DismissChooseWithAlbumDialog : ArchiveMainIntent
    data object ClickUploadWithAlbumRow : ArchiveMainIntent
    data object ClickUploadWithoutAlbumRow : ArchiveMainIntent

    data object ClickAddNewAlbumRow : ArchiveMainIntent
    data object ClickNotificationIcon : ArchiveMainIntent

    // Album Intent
    data object ClickAllAlbumText : ArchiveMainIntent
    data object ClickFavoriteAlbum : ArchiveMainIntent
    data class ClickAlbumItem(val album: Album) : ArchiveMainIntent

    // Photo Intent
    data object ClickAllPhotoText : ArchiveMainIntent
    data class ClickPhotoItem(val photo: Photo) : ArchiveMainIntent

    // Add Album BottomSheet Intent
    data object DismissAddAlbumBottomSheet : ArchiveMainIntent
    data class ClickAddAlbumButton(val albumName: String) : ArchiveMainIntent
}

sealed interface ArchiveMainSideEffect {
    data object NavigateToQRScan : ArchiveMainSideEffect
    data class NavigateToGalleryUpload(val uriStrings: List<String>) : ArchiveMainSideEffect
    data object NavigateToAllAlbum : ArchiveMainSideEffect
    data class NavigateToFavoriteAlbum(val album: Album) : ArchiveMainSideEffect
    data class NavigateToAlbumDetail(val album: Album) : ArchiveMainSideEffect
    data object NavigateToAllPhoto : ArchiveMainSideEffect
    data class NavigateToPhotoDetail(val photo: Photo) : ArchiveMainSideEffect

    data object ScrollToTop : ArchiveMainSideEffect
    data object OpenGallery : ArchiveMainSideEffect
    data class ShowToastMessage(val message: String) : ArchiveMainSideEffect
}

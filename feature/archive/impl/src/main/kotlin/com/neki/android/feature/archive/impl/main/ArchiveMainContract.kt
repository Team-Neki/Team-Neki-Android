package com.neki.android.feature.archive.impl.main

import android.net.Uri
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import com.neki.android.core.model.UploadType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ArchiveMainState(
    val isLoading: Boolean = false,
    val isFirstEntered: Boolean = true,
    val favoriteAlbum: AlbumPreview = AlbumPreview(),
    val albums: ImmutableList<AlbumPreview> = persistentListOf(),
    val recentPhotos: ImmutableList<Photo> = persistentListOf(),
    val scannedImageUrl: String? = null,
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val isShowAddDialog: Boolean = false,
    val isShowChooseWithAlbumDialog: Boolean = false,
    val isShowAddAlbumBottomSheet: Boolean = false,
) {
    val uploadType: UploadType
        get() = if (scannedImageUrl == null) UploadType.GALLERY else UploadType.QR_SCAN
}

sealed interface ArchiveMainIntent {
    data object EnterArchiveMainScreen : ArchiveMainIntent
    data class QRCodeScanned(val imageUrl: String) : ArchiveMainIntent
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
    data class ClickAlbumItem(val albumId: Long) : ArchiveMainIntent

    // Photo Intent
    data object ClickAllPhotoText : ArchiveMainIntent
    data class ClickPhotoItem(val photo: Photo) : ArchiveMainIntent

    // Add Album BottomSheet Intent
    data object DismissAddAlbumBottomSheet : ArchiveMainIntent
    data class ClickAddAlbumButton(val albumName: String) : ArchiveMainIntent
}

sealed interface ArchiveMainSideEffect {
    data object NavigateToQRScan : ArchiveMainSideEffect
    data class NavigateToUploadAlbumWithGallery(val uriStrings: List<String>) : ArchiveMainSideEffect
    data class NavigateToUploadAlbumWithQRScan(val imageUrl: String) : ArchiveMainSideEffect
    data object NavigateToAllAlbum : ArchiveMainSideEffect
    data class NavigateToFavoriteAlbum(val albumId: Long) : ArchiveMainSideEffect
    data class NavigateToAlbumDetail(val albumId: Long) : ArchiveMainSideEffect
    data object NavigateToAllPhoto : ArchiveMainSideEffect
    data class NavigateToPhotoDetail(val photo: Photo) : ArchiveMainSideEffect

    data object ScrollToTop : ArchiveMainSideEffect
    data object OpenGallery : ArchiveMainSideEffect
    data class ShowToastMessage(val message: String) : ArchiveMainSideEffect
}

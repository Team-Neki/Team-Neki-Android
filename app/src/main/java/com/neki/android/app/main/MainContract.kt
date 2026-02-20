package com.neki.android.app.main

import android.net.Uri
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MainState(
    val isLoading: Boolean = false,
    val isShowAddPhotoBottomSheet: Boolean = false,
    val isShowSelectWithAlbumDialog: Boolean = false,
    val selectedUris: ImmutableList<Uri> = persistentListOf(),
    val scannedImageUrl: String? = null,
)

sealed interface MainIntent {
    data object ClickAddPhotoFab : MainIntent
    data object DismissAddPhotoBottomSheet : MainIntent
    data object ClickQRScan : MainIntent
    data object ClickGalleryUpload : MainIntent
    data class SelectGalleryImage(val uris: List<Uri>) : MainIntent
    data class QRCodeScanned(val imageUrl: String) : MainIntent
    data object DismissSelectWithAlbumDialog : MainIntent
    data object ClickUploadWithAlbum : MainIntent
    data object ClickUploadWithoutAlbum : MainIntent
}

sealed interface MainSideEffect {
    data object NavigateToQRScan : MainSideEffect
    data object OpenGallery : MainSideEffect
    data class NavigateToUploadAlbumWithGallery(val uriStrings: List<String>) : MainSideEffect
    data class NavigateToUploadAlbumWithQRScan(val imageUrl: String) : MainSideEffect
    data class ShowToast(val message: String) : MainSideEffect
}

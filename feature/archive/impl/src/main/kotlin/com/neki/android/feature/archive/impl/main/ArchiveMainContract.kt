package com.neki.android.feature.archive.impl.main

import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ArchiveMainState(
    val isFirstEntered: Boolean = false,
    val favoriteAlbum: Album = Album(),
    val albums: ImmutableList<Album> = persistentListOf(),
    val recentPhotos: ImmutableList<Photo> = persistentListOf(),
    val showAddDialog: Boolean = false,
    val showAddAlbumBottomSheet: Boolean = false,
    val newAlbumTitleState: TextFieldState = TextFieldState(),
    val albumNameErrorState: AlbumNameErrorState? = null,
)

enum class AlbumNameErrorState(val message: String) {
    EXCEED_LENGTH("앨범명은 최대 16자까지 입력할 수 있어요."),
    INVALID_CHARACTER("앨범명은 한글, 영문, 숫자만 사용할 수 있어요."),
    DUPLICATE("이미 사용 중인 앨범명이에요."),
}

sealed interface ArchiveMainIntent {
    data object EnterArchiveMainScreen : ArchiveMainIntent
    data object ClickGoToTopButton : ArchiveMainIntent

    // TopBar Intent
    data object ClickAddIcon : ArchiveMainIntent
    data object DismissAddDialog : ArchiveMainIntent
    data object ClickQRScanRow : ArchiveMainIntent
    data object ClickGalleryUploadRow : ArchiveMainIntent
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
    data object ClickAddAlbumButton : ArchiveMainIntent
}

sealed interface ArchiveMainSideEffect {
    data object NavigateToQRScan : ArchiveMainSideEffect
    data object NavigateToGalleryUpload : ArchiveMainSideEffect
    data object NavigateToAllAlbum : ArchiveMainSideEffect
    data class NavigateToFavoriteAlbum(val album: Album) : ArchiveMainSideEffect
    data class NavigateToAlbumDetail(val album: Album) : ArchiveMainSideEffect
    data object NavigateToAllPhoto : ArchiveMainSideEffect
    data class NavigateToPhotoDetail(val photo: Photo) : ArchiveMainSideEffect

    data object ScrollToTop : ArchiveMainSideEffect
    data class ShowToastMessage(val message: String) : ArchiveMainSideEffect
}

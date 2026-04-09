package com.neki.android.feature.archive.impl.main

import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ArchiveMainState(
    val isLoading: Boolean = false,
    val isFirstEntered: Boolean = false,
    val favoriteAlbum: AlbumPreview = AlbumPreview(title = "즐겨찾는사진"),
    val albums: ImmutableList<AlbumPreview> = persistentListOf(),
    val recentPhotos: ImmutableList<Photo> = persistentListOf(),
    val isShowAddAlbumBottomSheet: Boolean = false,
    val albumNameTextState: TextFieldState = TextFieldState(),
)

sealed interface ArchiveMainIntent {
    data object EnterArchiveMainScreen : ArchiveMainIntent
    data object RefreshArchiveMain : ArchiveMainIntent
    data object ClickScreen : ArchiveMainIntent
    data object ClickGoToTopButton : ArchiveMainIntent

    // TopBar Intent
    data object DismissToolTipPopup : ArchiveMainIntent
    data object ClickQRScanIcon : ArchiveMainIntent
    data object ClickNotificationIcon : ArchiveMainIntent

    // Album Intent
    data object ClickAllAlbumText : ArchiveMainIntent
    data object ClickFavoriteAlbum : ArchiveMainIntent
    data class ClickAlbumItem(val albumId: Long, val albumTitle: String) : ArchiveMainIntent
    data object ClickAddAlbum : ArchiveMainIntent

    // Photo Intent
    data object ClickAllPhotoText : ArchiveMainIntent
    data class ClickPhotoItem(val photo: Photo, val index: Int) : ArchiveMainIntent
    data class ClickFavoriteIcon(val photo: Photo) : ArchiveMainIntent

    // Add Album BottomSheet Intent
    data object DismissAddAlbumBottomSheet : ArchiveMainIntent
    data object ClickAddAlbumButton : ArchiveMainIntent
}

sealed interface ArchiveMainSideEffect {
    data object NavigateToQRScan : ArchiveMainSideEffect
    data object NavigateToAllAlbum : ArchiveMainSideEffect
    data class NavigateToFavoriteAlbum(val albumId: Long) : ArchiveMainSideEffect
    data class NavigateToAlbumDetail(val albumId: Long, val title: String) : ArchiveMainSideEffect
    data object NavigateToAllPhoto : ArchiveMainSideEffect
    data class NavigateToPhotoDetail(val photos: List<Photo>, val index: Int) : ArchiveMainSideEffect

    data object ScrollToTop : ArchiveMainSideEffect
    data class ShowToastMessage(val message: String) : ArchiveMainSideEffect
}

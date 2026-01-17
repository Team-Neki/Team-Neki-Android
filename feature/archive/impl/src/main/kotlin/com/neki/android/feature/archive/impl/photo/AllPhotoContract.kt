package com.neki.android.feature.archive.impl.photo

import com.neki.android.core.model.Photo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AllPhotoState(
    val photos: ImmutableList<Photo> = persistentListOf(),
    val sortedDescendingPhotos: ImmutableList<Photo> = persistentListOf(),
    val selectMode: PhotoSelectMode = PhotoSelectMode.DEFAULT,
    val selectedPhotoFilter: PhotoFilter = PhotoFilter.NEWEST,
    val selectedPhotos: ImmutableList<Photo> = persistentListOf(),
    val showFavoritePhoto: Boolean = false,
    val showFilterDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
)

enum class PhotoFilter(val label: String) {
    NEWEST("최신순"),
    OLDEST("오래된순"),
}

enum class PhotoSelectMode {
    DEFAULT,
    SELECTING,
}

sealed interface AllPhotoIntent {

    data object EnterAllPhotoScreen : AllPhotoIntent

    // TopBar Intent
    data object ClickTopBarBackIcon : AllPhotoIntent
    data object ClickTopBarSelectIcon : AllPhotoIntent
    data object ClickTopBarCancelIcon : AllPhotoIntent

    // Filter Intent
    data object ClickFilterChip : AllPhotoIntent
    data object DismissFilterDialog : AllPhotoIntent
    data object ClickFavoriteFilterChip : AllPhotoIntent
    data class ClickFilterDialogRow(val filter: PhotoFilter) : AllPhotoIntent

    // Photo Intent
    data class ClickPhotoItem(val photo: Photo) : AllPhotoIntent
    data object ClickDownloadIcon : AllPhotoIntent
    data object ClickDeleteIcon : AllPhotoIntent
    data object DismissDeleteDialog : AllPhotoIntent
    data object ClickDeleteDialogConfirmButton : AllPhotoIntent
}

sealed interface AllPhotoSideEffect {
    data object NavigateBack : AllPhotoSideEffect
    data class NavigateToPhotoDetail(val photo: Photo) : AllPhotoSideEffect
    data class ShowToastMessage(val message: String) : AllPhotoSideEffect
    data class DownloadImage(val imageUrl: String) : AllPhotoSideEffect
}

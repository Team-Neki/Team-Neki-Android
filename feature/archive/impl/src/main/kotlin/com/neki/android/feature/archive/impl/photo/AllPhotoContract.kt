package com.neki.android.feature.archive.impl.photo

import com.neki.android.core.model.Photo
import com.neki.android.feature.archive.impl.dummyPhotos
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AllPhotoState(
    val photos: ImmutableList<Photo> = dummyPhotos,
    val sortedDescendingPhotos: ImmutableList<Photo> = dummyPhotos,
    val showingPhotos: ImmutableList<Photo> = dummyPhotos,
    val selectMode: SelectMode = SelectMode.DEFAULT,
    val selectedPhotoFilter: PhotoFilter = PhotoFilter.NEWEST,
    val selectedPhotos: ImmutableList<Photo> = persistentListOf(),
    val isFavoriteChipSelected: Boolean = false,
    val showFilterDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
)

enum class PhotoFilter(val label: String) {
    NEWEST("최신순"),
    OLDEST("오래된순"),
}

sealed interface AllPhotoIntent {

    data object EnterAllPhotoScreen : AllPhotoIntent

    // TopBar Intent
    data object ClickTopBarBackIcon : AllPhotoIntent
    data object ClickTopBarSelectIcon : AllPhotoIntent
    data object ClickTopBarCancelIcon : AllPhotoIntent
    data object OnBackPressed : AllPhotoIntent

    // Filter Intent
    data object ClickFilterChip : AllPhotoIntent
    data object DismissFilterPopup : AllPhotoIntent
    data object ClickFavoriteFilterChip : AllPhotoIntent
    data class ClickFilterPopupRow(val filter: PhotoFilter) : AllPhotoIntent

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

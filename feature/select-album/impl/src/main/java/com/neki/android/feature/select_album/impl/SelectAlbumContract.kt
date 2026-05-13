package com.neki.android.feature.select_album.impl

import androidx.compose.foundation.text.input.TextFieldState
import com.neki.android.core.model.AlbumPreview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class SelectAlbumState(
    val isLoading: Boolean = false,
    val isUploading: Boolean = false,
    val title: String = "",
    val photoCount: Int = 0,
    val multiSelect: Boolean = false,
    val favoriteAlbum: AlbumPreview = AlbumPreview(),
    val albums: ImmutableList<AlbumPreview> = persistentListOf(),
    val selectedAlbums: PersistentList<AlbumPreview> = persistentListOf(),
    val isShowAddAlbumBottomSheet: Boolean = false,
    val albumNameTextState: TextFieldState = TextFieldState(),
    val disabledAlbumId: Long? = null,
) {
    val isConfirmEnabled: Boolean
        get() = selectedAlbums.isNotEmpty() && !isUploading
}

sealed interface SelectAlbumIntent {
    data object EnterSelectAlbumScreen : SelectAlbumIntent

    data object ClickBackIcon : SelectAlbumIntent
    data object ClickConfirmButton : SelectAlbumIntent

    data class ClickAlbumItem(val album: AlbumPreview) : SelectAlbumIntent

    data object ClickCreateAlbumButton : SelectAlbumIntent
    data object DismissAddAlbumBottomSheet : SelectAlbumIntent
    data object ClickAddAlbumConfirmButton : SelectAlbumIntent
}

sealed interface SelectAlbumSideEffect {
    data object NavigateBack : SelectAlbumSideEffect
    data class SendUploadResult(val album: AlbumPreview) : SelectAlbumSideEffect
    data object SendPhotoMovedResult : SelectAlbumSideEffect
    data class SendPhotoCopiedResult(val albumIds: List<Long>, val albumTitle: String) : SelectAlbumSideEffect
    data class ShowToastMessage(val message: String) : SelectAlbumSideEffect
}

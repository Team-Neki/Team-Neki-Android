package com.neki.android.feature.archive.impl.main

import androidx.lifecycle.ViewModel
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArchiveMainViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<ArchiveMainState, ArchiveMainIntent, ArchiveMainSideEffect> =
        mviIntentStore(
            initialState = ArchiveMainState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(ArchiveMainIntent.EnterArchiveMainScreen) },
        )

    private fun onIntent(
        intent: ArchiveMainIntent,
        state: ArchiveMainState,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        if(intent != ArchiveMainIntent.EnterArchiveMainScreen) reduce { copy(isFirstEntered = false) }
        when (intent) {
            ArchiveMainIntent.EnterArchiveMainScreen -> fetchInitialDate(reduce)
            ArchiveMainIntent.ClickScreen -> reduce { copy(isFirstEntered = false) }
            ArchiveMainIntent.ClickGoToTopButton -> postSideEffect(ArchiveMainSideEffect.ScrollToTop)

            // TopBar Intent
            ArchiveMainIntent.ClickAddIcon -> reduce { copy(showAddDialog = true) }
            ArchiveMainIntent.DismissAddDialog -> reduce { copy(showAddDialog = false) }
            ArchiveMainIntent.ClickQRScanRow -> {
                reduce { copy(showAddDialog = false) }
                postSideEffect(ArchiveMainSideEffect.NavigateToQRScan)
            }

            ArchiveMainIntent.ClickGalleryUploadRow -> {
                reduce { copy(showAddDialog = false) }
                postSideEffect(ArchiveMainSideEffect.NavigateToGalleryUpload)
            }

            ArchiveMainIntent.ClickAddNewAlbumRow -> reduce {
                copy(
                    showAddDialog = false,
                    showAddAlbumBottomSheet = true,
                )
            }

            ArchiveMainIntent.ClickNotificationIcon -> {}

            // Album Intent
            ArchiveMainIntent.ClickAllAlbumText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllAlbum)
            ArchiveMainIntent.ClickFavoriteAlbum -> postSideEffect(ArchiveMainSideEffect.NavigateToFavoriteAlbum(state.favoriteAlbum))
            is ArchiveMainIntent.ClickAlbumItem -> postSideEffect(ArchiveMainSideEffect.NavigateToAlbumDetail(intent.album))

            // Photo Intent
            ArchiveMainIntent.ClickAllPhotoText -> postSideEffect(ArchiveMainSideEffect.NavigateToAllPhoto)
            is ArchiveMainIntent.ClickPhotoItem -> postSideEffect(ArchiveMainSideEffect.NavigateToPhotoDetail(intent.photo))

            // Add Album BottomSheet Intent
            ArchiveMainIntent.DismissAddAlbumBottomSheet -> reduce { copy(showAddAlbumBottomSheet = false) }
            is ArchiveMainIntent.ClickAddAlbumButton -> handleAddAlbum(intent.albumName, reduce, postSideEffect)
        }
    }

    private fun fetchInitialDate(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        reduce { copy() }
    }

    private fun handleAddAlbum(
        albumName: String,
        reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit,
        postSideEffect: (ArchiveMainSideEffect) -> Unit,
    ) {
        // TODO: Add album to repository
        reduce { copy(showAddAlbumBottomSheet = false) }
        postSideEffect(ArchiveMainSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
    }
}

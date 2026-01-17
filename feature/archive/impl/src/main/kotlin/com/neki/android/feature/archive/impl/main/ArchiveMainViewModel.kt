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
        when (intent) {
            ArchiveMainIntent.EnterArchiveMainScreen -> fetchInitialDate(reduce)
            ArchiveMainIntent.ClickGoToTopButton -> postSideEffect(ArchiveMainSideEffect.ScrollToTop)

            // TopBar Intent
            ArchiveMainIntent.ClickAddIcon -> reduce { copy(showAddDialog = true) }
            ArchiveMainIntent.DismissAddDialog -> reduce { copy(showAddDialog = false) }
            ArchiveMainIntent.ClickQRScanRow -> postSideEffect(ArchiveMainSideEffect.NavigateToQRScan)
            ArchiveMainIntent.ClickGalleryUploadRow -> postSideEffect(ArchiveMainSideEffect.NavigateToGalleryUpload)
            ArchiveMainIntent.ClickAddNewAlbumRow -> reduce { copy(showAddAlbumBottomSheet = true) }
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
            is ArchiveMainIntent.ClickAddAlbumButton -> {}
        }
    }

    private fun fetchInitialDate(reduce: (ArchiveMainState.() -> ArchiveMainState) -> Unit) {
        reduce { copy() }
    }
}

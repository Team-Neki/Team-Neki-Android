package com.neki.android.feature.archive.impl.photo

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class AllPhotoViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<AllPhotoState, AllPhotoIntent, AllPhotoSideEffect> =
        mviIntentStore(
            initialState = AllPhotoState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(AllPhotoIntent.EnterAllPhotoScreen) },
        )

    private fun onIntent(
        intent: AllPhotoIntent,
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        when (intent) {
            AllPhotoIntent.EnterAllPhotoScreen -> fetchInitialData(reduce)

            // TopBar Intent
            AllPhotoIntent.ClickTopBarBackIcon -> handleBackClick(state, reduce, postSideEffect)
            AllPhotoIntent.ClickTopBarSelectIcon -> reduce { copy(selectMode = SelectMode.SELECTING) }
            AllPhotoIntent.OnBackPressed -> handleBackClick(state, reduce, postSideEffect)
            AllPhotoIntent.ClickTopBarCancelIcon -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = emptyList<Photo>().toImmutableList(),
                )
            }

            // Filter Intent
            AllPhotoIntent.ClickFilterChip -> reduce { copy(isShowFilterDialog = true) }
            AllPhotoIntent.DismissFilterPopup -> reduce { copy(isShowFilterDialog = false) }
            AllPhotoIntent.ClickFavoriteFilterChip -> handleFavoriteFilter(state, reduce)
            is AllPhotoIntent.ClickFilterPopupRow -> handleFilterRow(intent.filter, reduce, postSideEffect)

            // Photo Intent
            is AllPhotoIntent.ClickPhotoItem -> handlePhotoClick(intent.photo, state, reduce, postSideEffect)
            AllPhotoIntent.ClickDownloadIcon -> downloadSelectedPhotos(state, postSideEffect)
            AllPhotoIntent.ClickDeleteIcon -> reduce { copy(isShowDeleteDialog = true) }
            AllPhotoIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            AllPhotoIntent.ClickDeleteDialogConfirmButton -> deleteSelectedPhotos(state, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (AllPhotoState.() -> AllPhotoState) -> Unit) {
        val dummyPhotos = listOf(
            Photo(id = 1, imageUrl = "https://picsum.photos/seed/all1/400/520", isFavorite = true, date = "2025.01.15"),
            Photo(id = 2, imageUrl = "https://picsum.photos/seed/all2/400/680", isFavorite = false, date = "2025.01.15"),
            Photo(id = 3, imageUrl = "https://picsum.photos/seed/all3/400/450", isFavorite = true, date = "2025.01.14"),
            Photo(id = 4, imageUrl = "https://picsum.photos/seed/all4/400/600", isFavorite = false, date = "2025.01.14"),
            Photo(id = 5, imageUrl = "https://picsum.photos/seed/all5/400/550", isFavorite = false, date = "2025.01.13"),
            Photo(id = 6, imageUrl = "https://picsum.photos/seed/all6/400/720", isFavorite = true, date = "2025.01.13"),
            Photo(id = 7, imageUrl = "https://picsum.photos/seed/all7/400/480", isFavorite = false, date = "2025.01.12"),
            Photo(id = 8, imageUrl = "https://picsum.photos/seed/all8/400/650", isFavorite = false, date = "2025.01.12"),
            Photo(id = 9, imageUrl = "https://picsum.photos/seed/all9/400/500", isFavorite = true, date = "2025.01.11"),
            Photo(id = 10, imageUrl = "https://picsum.photos/seed/all10/400/580", isFavorite = false, date = "2025.01.11"),
            Photo(id = 11, imageUrl = "https://picsum.photos/seed/all11/400/700", isFavorite = false, date = "2025.01.10"),
            Photo(id = 12, imageUrl = "https://picsum.photos/seed/all12/400/460", isFavorite = true, date = "2025.01.10"),
            Photo(id = 13, imageUrl = "https://picsum.photos/seed/all13/400/620", isFavorite = false, date = "2025.01.09"),
            Photo(id = 14, imageUrl = "https://picsum.photos/seed/all14/400/540", isFavorite = false, date = "2025.01.09"),
            Photo(id = 15, imageUrl = "https://picsum.photos/seed/all15/400/690", isFavorite = true, date = "2025.01.08"),
            Photo(id = 16, imageUrl = "https://picsum.photos/seed/all16/400/470", isFavorite = false, date = "2025.01.08"),
            Photo(id = 17, imageUrl = "https://picsum.photos/seed/all17/400/610", isFavorite = false, date = "2025.01.07"),
            Photo(id = 18, imageUrl = "https://picsum.photos/seed/all18/400/530", isFavorite = true, date = "2025.01.07"),
            Photo(id = 19, imageUrl = "https://picsum.photos/seed/all19/400/670", isFavorite = false, date = "2025.01.06"),
            Photo(id = 20, imageUrl = "https://picsum.photos/seed/all20/400/490", isFavorite = false, date = "2025.01.06"),
            Photo(id = 21, imageUrl = "https://picsum.photos/seed/all21/400/640", isFavorite = true, date = "2025.01.05"),
            Photo(id = 22, imageUrl = "https://picsum.photos/seed/all22/400/560", isFavorite = false, date = "2025.01.05"),
            Photo(id = 23, imageUrl = "https://picsum.photos/seed/all23/400/710", isFavorite = false, date = "2025.01.04"),
            Photo(id = 24, imageUrl = "https://picsum.photos/seed/all24/400/440", isFavorite = true, date = "2025.01.04"),
            Photo(id = 25, imageUrl = "https://picsum.photos/seed/all25/400/590", isFavorite = false, date = "2025.01.03"),
        ).toImmutableList()

        val sortedPhotos = dummyPhotos.sortedByDescending { it.date }.toImmutableList()

        reduce {
            copy(
                photos = dummyPhotos,
                sortedDescendingPhotos = sortedPhotos,
                showingPhotos = sortedPhotos,
            )
        }
    }

    private fun handleBackClick(
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> postSideEffect(AllPhotoSideEffect.NavigateBack)
            SelectMode.SELECTING -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = persistentListOf(),
                )
            }
        }
    }

    private fun handleFavoriteFilter(
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
    ) {
        reduce {
            copy(
                isFavoriteChipSelected = !isFavoriteChipSelected,
                showingPhotos = if (!state.isFavoriteChipSelected) {
                    showingPhotos.filter { it.isFavorite }
                } else {
                    if (state.selectedPhotoFilter == PhotoFilter.NEWEST) sortedDescendingPhotos
                    else photos.sortedBy { it.date }
                }.toImmutableList(),
            )
        }
    }

    private fun handleFilterRow(
        filter: PhotoFilter,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        reduce {
            val sortedPhotos = when (filter) {
                PhotoFilter.NEWEST -> sortedDescendingPhotos.ifEmpty { photos.sortedByDescending { it.date } }
                PhotoFilter.OLDEST -> photos.sortedBy { it.date }
            }.filter { if (isFavoriteChipSelected) it.isFavorite else true }.toImmutableList()
            copy(
                isShowFilterDialog = false,
                selectedPhotoFilter = filter,
                showingPhotos = sortedPhotos,
            )
        }
        postSideEffect(AllPhotoSideEffect.ScrollToTop)
    }

    private fun handlePhotoClick(
        photo: Photo,
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AllPhotoSideEffect.NavigateToPhotoDetail(photo))
            }

            SelectMode.SELECTING -> {
                val isSelected = state.selectedPhotos.any { it.id == photo.id }
                if (isSelected) {
                    reduce {
                        copy(selectedPhotos = selectedPhotos.filter { it.id != photo.id }.toImmutableList())
                    }
                } else {
                    reduce {
                        copy(selectedPhotos = (selectedPhotos + photo).toImmutableList())
                    }
                }
            }
        }
    }

    private fun downloadSelectedPhotos(
        state: AllPhotoState,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }
        postSideEffect(AllPhotoSideEffect.DownloadImages(state.selectedPhotos.map { it.imageUrl }))
    }

    private fun deleteSelectedPhotos(
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }
        // TODO: Delete selected photos from repository
        reduce {
            copy(
                photos = photos.filter { photo -> selectedPhotos.none { it.id == photo.id } }.toImmutableList(),
                selectedPhotos = persistentListOf(),
                selectMode = SelectMode.DEFAULT,
                isShowDeleteDialog = false,
            )
        }
        postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 삭제했어요"))
    }
}

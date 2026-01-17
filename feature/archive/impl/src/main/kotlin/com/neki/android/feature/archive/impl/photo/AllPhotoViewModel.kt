package com.neki.android.feature.archive.impl.photo

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
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
            AllPhotoIntent.ClickTopBarBackIcon -> postSideEffect(AllPhotoSideEffect.NavigateBack)
            AllPhotoIntent.ClickTopBarSelectIcon -> reduce { copy(selectMode = PhotoSelectMode.SELECTING) }
            AllPhotoIntent.ClickTopBarCancelIcon -> reduce {
                copy(
                    selectMode = PhotoSelectMode.DEFAULT,
                    selectedPhotos = emptyList<Photo>().toImmutableList(),
                )
            }

            // Filter Intent
            AllPhotoIntent.ClickFilterChip -> reduce { copy(showFilterDialog = true) }
            AllPhotoIntent.DismissFilterDialog -> reduce { copy(showFilterDialog = false) }
            AllPhotoIntent.ClickFavoriteFilterChip -> handleFavoriteFilter(state, reduce)
            is AllPhotoIntent.ClickFilterDialogRow -> handleFilterRow(intent.filter, reduce)

            // Photo Intent
            is AllPhotoIntent.ClickPhotoItem -> handlePhotoClick(intent.photo, state, reduce, postSideEffect)
            AllPhotoIntent.ClickDownloadIcon -> downloadSelectedPhotos(state, postSideEffect)
            AllPhotoIntent.ClickDeleteIcon -> reduce { copy(showDeleteDialog = true) }
            AllPhotoIntent.DismissDeleteDialog -> reduce { copy(showDeleteDialog = false) }
            AllPhotoIntent.ClickDeleteDialogConfirmButton -> deleteSelectedPhotos(state, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (AllPhotoState.() -> AllPhotoState) -> Unit) {
        // TODO: Fetch photos from repository
        reduce { copy() }
    }

    private fun handleFavoriteFilter(
        state: AllPhotoState,
        reduce: ((AllPhotoState) -> AllPhotoState) -> Unit,
    ) {
        reduce {
            copy(
                showingPhotos = if (!state.isFavoriteChipSelected) {
                    showingPhotos.filter { it.isFavorite }
                } else {
                    if (state.selectedPhotoFilter == PhotoFilter.NEWEST) sortedDescendingPhotos
                    else photos

                }.toImmutableList(),
            )
        }
    }

    private fun handleFilterRow(filter: PhotoFilter, reduce: (AllPhotoState.() -> AllPhotoState) -> Unit) {
        reduce {
            val sortedPhotos = when (filter) {
                PhotoFilter.NEWEST -> sortedDescendingPhotos.ifEmpty { photos.sortedByDescending { it.date } }
                PhotoFilter.OLDEST -> photos
            }.toImmutableList()
            copy(
                showFilterDialog = false,
                selectedPhotoFilter = filter,
                showingPhotos = sortedPhotos,
            )
        }
    }

    private fun handlePhotoClick(
        photo: Photo,
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            PhotoSelectMode.DEFAULT -> {
                postSideEffect(AllPhotoSideEffect.NavigateToPhotoDetail(photo))
            }

            PhotoSelectMode.SELECTING -> {
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
        // TODO: Download selected photos
        postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 갤러리에 다운로드했어요"))
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
                selectMode = PhotoSelectMode.DEFAULT,
            )
        }
        postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 삭제했어요"))
    }
}

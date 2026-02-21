package com.neki.android.feature.archive.impl.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.Photo
import com.neki.android.core.model.SortOrder
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllPhotoViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    private val deletedPhotoIds = MutableStateFlow<Set<Long>>(emptySet())
    private val updatedFavorites = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    private val _photoFilter = MutableStateFlow(PhotoFilter.NEWEST)
    private val _isFavoriteOnly = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val originalPagingData: Flow<PagingData<Photo>> = combine(
        _photoFilter,
        _isFavoriteOnly,
    ) { filter, isFavoriteOnly ->
        filter to isFavoriteOnly
    }.flatMapLatest { (filter, isFavoriteOnly) ->
        val sortOrder = when (filter) {
            PhotoFilter.NEWEST -> SortOrder.DESC
            PhotoFilter.OLDEST -> SortOrder.ASC
        }
        if (isFavoriteOnly) {
            photoRepository.getFavoritePhotosFlow(sortOrder)
        } else {
            photoRepository.getPhotosFlow(sortOrder = sortOrder)
        }
    }.cachedIn(viewModelScope)

    val photoPagingData: Flow<PagingData<Photo>> = combine(
        originalPagingData,
        deletedPhotoIds,
        updatedFavorites,
    ) { pagingData, deletedIds, favorites ->
        val isFavoriteOnly = _isFavoriteOnly.value
        pagingData
            .filter { photo -> photo.id !in deletedIds }
            .map { photo ->
                favorites[photo.id]?.let { isFavorite ->
                    photo.copy(isFavorite = isFavorite)
                } ?: photo
            }
            .let { data ->
                if (isFavoriteOnly) data.filter { it.isFavorite } else data
            }
    }

    val store: MviIntentStore<AllPhotoState, AllPhotoIntent, AllPhotoSideEffect> =
        mviIntentStore(
            initialState = AllPhotoState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: AllPhotoIntent,
        state: AllPhotoState,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        when (intent) {
            AllPhotoIntent.EnterAllPhotoScreen -> Unit

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
            AllPhotoIntent.ClickFavoriteFilterChip -> handleFavoriteFilter(state, reduce, postSideEffect)
            is AllPhotoIntent.ClickFilterPopupRow -> handleFilterRow(intent.filter, reduce, postSideEffect)

            // Photo Intent
            is AllPhotoIntent.ClickPhotoItem -> handlePhotoClick(intent.photo, state, reduce, postSideEffect)
            AllPhotoIntent.ClickDownloadIcon -> downloadSelectedPhotos(state, postSideEffect)
            AllPhotoIntent.ClickDeleteIcon -> reduce { copy(isShowDeleteDialog = true) }
            AllPhotoIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            AllPhotoIntent.ClickDeleteDialogConfirmButton -> deleteSelectedPhotos(state, reduce, postSideEffect)

            // Result Intent
            is AllPhotoIntent.PhotoDeleted -> {
                deletedPhotoIds.update { it + intent.photoIds.toSet() }
            }
            is AllPhotoIntent.ClickFavoriteIcon -> {
                val photo = intent.photo
                val newFavorite = !photo.isFavorite
                updatedFavorites.update { it + (photo.id to newFavorite) }
                viewModelScope.launch {
                    photoRepository.updateFavorite(photo.id, newFavorite)
                        .onFailure { e ->
                            Timber.e(e)
                            updatedFavorites.update { it + (photo.id to photo.isFavorite) }
                            postSideEffect(AllPhotoSideEffect.ShowToastMessage("즐겨찾기 변경에 실패했어요"))
                        }
                }
            }

            is AllPhotoIntent.FavoriteChanged -> {
                updatedFavorites.update { it + (intent.photoId to intent.isFavorite) }
            }
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
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        val newValue = !state.isFavoriteChipSelected
        _isFavoriteOnly.value = newValue
        reduce { copy(isFavoriteChipSelected = newValue) }
        postSideEffect(AllPhotoSideEffect.ScrollToTop)
    }

    private fun handleFilterRow(
        filter: PhotoFilter,
        reduce: (AllPhotoState.() -> AllPhotoState) -> Unit,
        postSideEffect: (AllPhotoSideEffect) -> Unit,
    ) {
        _photoFilter.value = filter
        reduce {
            copy(
                isShowFilterDialog = false,
                selectedPhotoFilter = filter,
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

        viewModelScope.launch {
            val selectedPhotoIds = state.selectedPhotos.map { it.id }
            reduce { copy(isLoading = true) }

            photoRepository.deletePhoto(photoIds = selectedPhotoIds)
                .onSuccess {
                    Timber.d("삭제 성공")
                    deletedPhotoIds.update { it + selectedPhotoIds.toSet() }
                    reduce {
                        copy(
                            selectedPhotos = persistentListOf(),
                            selectMode = SelectMode.DEFAULT,
                            isShowDeleteDialog = false,
                            isLoading = false,
                        )
                    }
                    postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진을 삭제했어요"))
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isLoading = false) }
                    postSideEffect(AllPhotoSideEffect.ShowToastMessage("사진 삭제에 실패했어요"))
                }
        }
    }
}

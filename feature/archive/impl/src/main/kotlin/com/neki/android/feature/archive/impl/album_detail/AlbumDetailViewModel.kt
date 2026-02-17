package com.neki.android.feature.archive.impl.album_detail

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.model.Photo
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = AlbumDetailViewModel.Factory::class)
class AlbumDetailViewModel @AssistedInject constructor(
    @Assisted private val id: Long,
    @Assisted private val title: String,
    @Assisted private val isFavoriteAlbum: Boolean,
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(id: Long, title: String, isFavoriteAlbum: Boolean): AlbumDetailViewModel
    }

    private val deletedPhotoIds = MutableStateFlow<Set<Long>>(emptySet())
    private val updatedFavorites = MutableStateFlow<Map<Long, Boolean>>(emptyMap())

    private val originalPagingData: Flow<PagingData<Photo>> =
        if (isFavoriteAlbum) {
            photoRepository.getFavoritePhotosFlow()
        } else {
            photoRepository.getPhotosFlow(id)
        }.cachedIn(viewModelScope)

    val photoPagingData: Flow<PagingData<Photo>> = combine(
        originalPagingData,
        deletedPhotoIds,
        updatedFavorites,
    ) { pagingData, deletedIds, favorites ->
        pagingData
            .filter { photo -> photo.id !in deletedIds }
            .map { photo ->
                favorites[photo.id]?.let { isFavorite ->
                    photo.copy(isFavorite = isFavorite)
                } ?: photo
            }
    }

    val store: MviIntentStore<AlbumDetailState, AlbumDetailIntent, AlbumDetailSideEffect> =
        mviIntentStore(
            initialState = AlbumDetailState(
                title = title,
                isFavoriteAlbum = isFavoriteAlbum,
            ),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: AlbumDetailIntent,
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (intent) {
            AlbumDetailIntent.EnterAlbumDetailScreen -> {
                // Paging이 자동으로 처리
            }

            AlbumDetailIntent.ClickBackIcon -> handleBackClick(state, reduce, postSideEffect)
            AlbumDetailIntent.OnBackPressed -> handleBackClick(state, reduce, postSideEffect)
            AlbumDetailIntent.ClickOptionIcon -> reduce { copy(isShowOptionPopup = true) }
            AlbumDetailIntent.DismissOptionPopup -> reduce { copy(isShowOptionPopup = false) }
            AlbumDetailIntent.ClickSelectOption -> {
                reduce {
                    copy(
                        selectMode = SelectMode.SELECTING,
                        isShowOptionPopup = false,
                    )
                }
            }

            AlbumDetailIntent.ClickAddPhotoOption -> {
                reduce { copy(isShowOptionPopup = false) }
                postSideEffect(AlbumDetailSideEffect.OpenGallery)
            }

            AlbumDetailIntent.ClickRenameAlbumOption -> {
                reduce {
                    copy(
                        isShowOptionPopup = false,
                        isShowRenameAlbumBottomSheet = true,
                        renameAlbumTextState = TextFieldState(initialText = title),
                    )
                }
            }

            AlbumDetailIntent.ClickCancelButton -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = persistentListOf(),
                )
            }

            is AlbumDetailIntent.ClickPhotoItem -> handlePhotoClick(intent.photo, state, reduce, postSideEffect)

            AlbumDetailIntent.ClickDownloadIcon -> handleDownload(state, postSideEffect)
            AlbumDetailIntent.ClickDeleteIcon -> handleDeleteIconClick(state, reduce, postSideEffect)

            AlbumDetailIntent.DismissDeleteDialog -> reduce { copy(isShowDeleteDialog = false) }
            AlbumDetailIntent.ClickDeleteDialogCancelButton -> reduce { copy(isShowDeleteDialog = false) }
            AlbumDetailIntent.ClickDeleteDialogConfirmButton -> handleFavoriteDelete(state, reduce, postSideEffect)

            AlbumDetailIntent.DismissDeleteBottomSheet -> reduce { copy(isShowDeleteBottomSheet = false) }
            is AlbumDetailIntent.SelectDeleteOption -> reduce { copy(selectedDeleteOption = intent.option) }
            AlbumDetailIntent.ClickDeleteBottomSheetCancelButton -> reduce { copy(isShowDeleteBottomSheet = false) }
            AlbumDetailIntent.ClickDeleteBottomSheetConfirmButton -> handleAlbumPhotoDelete(state, reduce, postSideEffect)

            // Gallery Intent
            is AlbumDetailIntent.SelectGalleryImage -> uploadMultipleImages(intent.uris, reduce, postSideEffect)

            // Result Intent
            is AlbumDetailIntent.PhotoDeleted -> {
                deletedPhotoIds.update { it + intent.photoIds.toSet() }
            }

            is AlbumDetailIntent.FavoriteChanged -> {
                updatedFavorites.update { it + (intent.photoId to intent.isFavorite) }
            }

            AlbumDetailIntent.DismissRenameBottomSheet -> reduce {
                copy(isShowRenameAlbumBottomSheet = false, renameAlbumTextState = TextFieldState())
            }
            AlbumDetailIntent.ClickRenameBottomSheetCancelButton -> reduce {
                copy(isShowRenameAlbumBottomSheet = false, renameAlbumTextState = TextFieldState())
            }
            AlbumDetailIntent.ClickRenameBottomSheetConfirmButton -> handleRenameAlbum(state, reduce, postSideEffect)
        }
    }

    private fun handleRenameAlbum(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        reduce { copy(isLoading = true) }
        viewModelScope.launch {
            val newName = state.renameAlbumTextState.text.trim().toString()
            folderRepository.updateFolder(
                folderId = id,
                name = newName,
            ).onSuccess {
                reduce {
                    copy(
                        title = newName,
                        isShowRenameAlbumBottomSheet = false,
                        isLoading = false,
                    )
                }
                postSideEffect(AlbumDetailSideEffect.ShowToastMessage("앨범 이름을 변경했어요"))
            }.onFailure { e ->
                Timber.e(e)
                reduce { copy(isLoading = false) }
                postSideEffect(AlbumDetailSideEffect.ShowToastMessage("앨범 이름 변경에 실패했어요"))
            }
        }
    }

    private fun uploadMultipleImages(
        imageUris: List<Uri>,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            // TODO: 추후 즐겨찾기 옵션 추가
            uploadMultiplePhotoUseCase(
                imageUris = imageUris,
                folderId = id.takeUnless { isFavoriteAlbum },
            ).onSuccess {
                reduce { copy(isLoading = false) }
                postSideEffect(AlbumDetailSideEffect.RefreshPhotos)
                postSideEffect(AlbumDetailSideEffect.ShowToastMessage("새로운 사진을 추가했어요"))
            }.onFailure { error ->
                Timber.e(error)
                postSideEffect(AlbumDetailSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
                reduce { copy(isLoading = false) }
            }
        }
    }

    private fun handleBackClick(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> postSideEffect(AlbumDetailSideEffect.NavigateBack)
            SelectMode.SELECTING -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedPhotos = persistentListOf(),
                )
            }
        }
    }

    private fun handlePhotoClick(
        photo: Photo,
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AlbumDetailSideEffect.NavigateToPhotoDetail(photo))
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

    private fun handleDownload(
        state: AlbumDetailState,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }
        postSideEffect(AlbumDetailSideEffect.DownloadImages(state.selectedPhotos.map { it.imageUrl }))
    }

    private fun handleDeleteIconClick(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        if (state.selectedPhotos.isEmpty()) {
            postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 선택해주세요."))
            return
        }

        if (state.isFavoriteAlbum) {
            reduce { copy(isShowDeleteDialog = true) }
        } else {
            reduce { copy(isShowDeleteBottomSheet = true) }
        }
    }

    private fun handleFavoriteDelete(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        val selectedPhotoIds = state.selectedPhotos.map { it.id }

        viewModelScope.launch {
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
                    postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 삭제했어요"))
                }
                .onFailure { error ->
                    Timber.e(error)
                    reduce {
                        copy(
                            isShowDeleteDialog = false,
                            isLoading = false,
                        )
                    }
                    postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진 삭제에 실패했어요"))
                }
        }
    }

    private fun handleAlbumPhotoDelete(
        state: AlbumDetailState,
        reduce: (AlbumDetailState.() -> AlbumDetailState) -> Unit,
        postSideEffect: (AlbumDetailSideEffect) -> Unit,
    ) {
        val selectedPhotoIds = state.selectedPhotos.map { it.id }

        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            val result = when (state.selectedDeleteOption) {
                PhotoDeleteOption.REMOVE_FROM_ALBUM -> folderRepository.removePhotosFromFolder(id, selectedPhotoIds)
                PhotoDeleteOption.REMOVE_FROM_ALL -> photoRepository.deletePhoto(photoIds = selectedPhotoIds)
            }

            result
                .onSuccess {
                    Timber.d("삭제 성공")
                    deletedPhotoIds.update { it + selectedPhotoIds.toSet() }
                    reduce {
                        copy(
                            selectedPhotos = persistentListOf(),
                            selectMode = SelectMode.DEFAULT,
                            isShowDeleteBottomSheet = false,
                            isLoading = false,
                        )
                    }
                    postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진을 삭제했어요"))
                }
                .onFailure { error ->
                    Timber.e(error)
                    reduce {
                        copy(
                            isShowDeleteBottomSheet = false,
                            isLoading = false,
                        )
                    }
                    postSideEffect(AlbumDetailSideEffect.ShowToastMessage("사진 삭제에 실패했어요"))
                }
        }
    }
}

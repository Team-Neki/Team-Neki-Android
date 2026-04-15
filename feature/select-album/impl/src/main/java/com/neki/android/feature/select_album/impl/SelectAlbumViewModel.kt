package com.neki.android.feature.select_album.impl

import androidx.compose.foundation.text.input.TextFieldState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import kotlinx.collections.immutable.ImmutableList
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.select_album.api.SelectAlbumAction
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = SelectAlbumViewModel.Factory::class)
class SelectAlbumViewModel @AssistedInject constructor(
    @Assisted private val title: String,
    @Assisted private val multiSelect: Boolean,
    @Assisted private val action: SelectAlbumAction,
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(title: String, multiSelect: Boolean, action: SelectAlbumAction): SelectAlbumViewModel
    }

    private val photoCount: Int = when (action) {
        is SelectAlbumAction.UploadFromQR -> 1
        is SelectAlbumAction.UploadFromGallery -> action.imageUriStrings.size
        is SelectAlbumAction.MovePhotos -> action.photoIds.size
        is SelectAlbumAction.CopyPhotos -> action.photoIds.size
    }

    val store: MviIntentStore<SelectAlbumState, SelectAlbumIntent, SelectAlbumSideEffect> =
        mviIntentStore(
            initialState = SelectAlbumState(
                title = title,
                multiSelect = multiSelect,
                photoCount = photoCount,
                disabledAlbumId = if (action is SelectAlbumAction.MovePhotos) action.sourceFolderId else null,
            ),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(SelectAlbumIntent.EnterSelectAlbumScreen) },
        )

    private fun onIntent(
        intent: SelectAlbumIntent,
        state: SelectAlbumState,
        reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit,
        postSideEffect: (SelectAlbumSideEffect) -> Unit,
    ) {
        when (intent) {
            SelectAlbumIntent.EnterSelectAlbumScreen -> fetchInitialData(reduce)
            SelectAlbumIntent.ClickBackIcon -> postSideEffect(SelectAlbumSideEffect.NavigateBack)
            SelectAlbumIntent.ClickConfirmButton -> {
                if (state.isUploading) return
                val albums = state.selectedAlbums
                if (albums.isEmpty()) return
                performAction(albums, reduce, postSideEffect)
            }

            is SelectAlbumIntent.ClickAlbumItem -> handleAlbumClick(intent.album, state, reduce)
            SelectAlbumIntent.ClickCreateAlbumButton -> reduce {
                copy(isShowAddAlbumBottomSheet = true, albumNameTextState = TextFieldState())
            }

            SelectAlbumIntent.DismissAddAlbumBottomSheet -> reduce { copy(isShowAddAlbumBottomSheet = false) }
            SelectAlbumIntent.ClickAddAlbumConfirmButton -> handleAddAlbum(
                albumName = state.albumNameTextState.text.toString(),
                reduce = reduce,
                postSideEffect = postSideEffect,
            )
        }
    }

    private fun performAction(
        albums: ImmutableList<AlbumPreview>,
        reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit,
        postSideEffect: (SelectAlbumSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isUploading = true) }

            val targetFolderIds = albums.map { it.id }
            val result = when (action) {
                is SelectAlbumAction.UploadFromQR -> {
                    uploadSinglePhotoUseCase(imageUrl = action.imageUrl, folderId = albums.first().id)
                }

                is SelectAlbumAction.UploadFromGallery -> {
                    uploadMultiplePhotoUseCase(
                        imageUris = action.imageUriStrings.map { it.toUri() },
                        folderId = albums.first().id,
                    )
                }

                is SelectAlbumAction.MovePhotos -> {
                    folderRepository.movePhotos(
                        sourceFolderId = action.sourceFolderId,
                        photoIds = action.photoIds,
                        targetFolderIds = targetFolderIds,
                    )
                }

                is SelectAlbumAction.CopyPhotos -> {
                    folderRepository.copyPhotos(
                        photoIds = action.photoIds,
                        targetFolderIds = targetFolderIds,
                    )
                }
            }

            result.onSuccess {
                reduce { copy(isUploading = false) }
                when (action) {
                    is SelectAlbumAction.UploadFromQR, is SelectAlbumAction.UploadFromGallery -> {
                        postSideEffect(SelectAlbumSideEffect.ShowToastMessage("이미지를 추가했어요"))
                        postSideEffect(SelectAlbumSideEffect.SendUploadResult(albums.first()))
                    }

                    is SelectAlbumAction.MovePhotos -> {
                        postSideEffect(SelectAlbumSideEffect.ShowToastMessage("사진을 앨범에 이동했어요"))
                        postSideEffect(SelectAlbumSideEffect.SendPhotoMovedResult)
                        postSideEffect(SelectAlbumSideEffect.NavigateBack)
                    }

                    is SelectAlbumAction.CopyPhotos -> {
                        if (action.withShowToast) {
                            postSideEffect(SelectAlbumSideEffect.ShowToastMessage("사진을 앨범에 추가했어요"))
                        }
                        postSideEffect(SelectAlbumSideEffect.SendPhotoCopiedResult(targetFolderIds, albums.map { it.title }))
                        postSideEffect(SelectAlbumSideEffect.NavigateBack)
                    }
                }
            }.onFailure { e ->
                Timber.e(e)
                reduce { copy(isUploading = false) }
                postSideEffect(SelectAlbumSideEffect.ShowToastMessage("작업에 실패했어요"))
            }
        }
    }

    private fun fetchInitialData(reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }
            try {
                awaitAll(
                    async { fetchFavoriteSummary(reduce) },
                    async { fetchFolders(reduce) },
                )
            } finally {
                reduce { copy(isLoading = false) }
            }
        }
    }

    private suspend fun fetchFavoriteSummary(reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit) {
        photoRepository.getFavoriteSummary()
            .onSuccess { data ->
                reduce { copy(favoriteAlbum = data) }
            }
            .onFailure { e ->
                Timber.e(e)
            }
    }

    private suspend fun fetchFolders(reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit) {
        folderRepository.getFolders()
            .onSuccess { data ->
                reduce { copy(albums = data.toImmutableList()) }
            }
            .onFailure { e ->
                Timber.e(e)
            }
    }

    private fun handleAlbumClick(
        album: AlbumPreview,
        state: SelectAlbumState,
        reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit,
    ) {
        val isSelected = state.selectedAlbums.any { it.id == album.id }
        val newSelectedAlbums = when {
            !state.multiSelect -> if (isSelected) persistentListOf() else persistentListOf(album)
            isSelected -> state.selectedAlbums.filter { it.id != album.id }.toPersistentList()
            else -> state.selectedAlbums.add(album)
        }
        reduce { copy(selectedAlbums = newSelectedAlbums) }
    }

    private fun handleAddAlbum(
        albumName: String,
        reduce: (SelectAlbumState.() -> SelectAlbumState) -> Unit,
        postSideEffect: (SelectAlbumSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            folderRepository.createFolder(name = albumName)
                .onSuccess {
                    fetchFolders(reduce)
                    reduce {
                        copy(
                            isShowAddAlbumBottomSheet = false,
                            albumNameTextState = TextFieldState(),
                        )
                    }
                    postSideEffect(SelectAlbumSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
                }
                .onFailure { e ->
                    Timber.e(e)
                    reduce { copy(isShowAddAlbumBottomSheet = false, albumNameTextState = TextFieldState()) }
                    postSideEffect(SelectAlbumSideEffect.ShowToastMessage("앨범 추가에 실패했어요"))
                }
        }
    }
}

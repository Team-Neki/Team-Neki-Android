package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.usecase.UploadMultiplePhotoUseCase
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.model.UploadType
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = UploadAlbumViewModel.Factory::class)
class UploadAlbumViewModel @AssistedInject constructor(
    @Assisted private val imageUrl: String?,
    @Assisted private val uriStrings: List<String>,
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
    private val uploadMultiplePhotoUseCase: UploadMultiplePhotoUseCase,
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(imageUrl: String?, uriStrings: List<String>): UploadAlbumViewModel
    }

    val store: MviIntentStore<UploadAlbumState, UploadAlbumIntent, UploadAlbumSideEffect> =
        mviIntentStore(
            initialState = UploadAlbumState(
                imageUrl = imageUrl,
                selectedUris = uriStrings.map { it.toUri() }.toImmutableList(),
            ),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(UploadAlbumIntent.EnterUploadAlbumScreen) },
        )

    private fun onIntent(
        intent: UploadAlbumIntent,
        state: UploadAlbumState,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
    ) {
        when (intent) {
            UploadAlbumIntent.EnterUploadAlbumScreen -> fetchInitialData(reduce)
            UploadAlbumIntent.ClickBackIcon -> postSideEffect(UploadAlbumSideEffect.NavigateBack)
            UploadAlbumIntent.ClickUploadButton -> handleUploadButtonClick(state, reduce, postSideEffect)
            is UploadAlbumIntent.ClickAlbumItem -> {
                reduce {
                    copy(
                        selectedAlbumIds = if (state.selectedAlbumIds.any { it == intent.albumId }) {
                            selectedAlbumIds.remove(intent.albumId)
                        } else {
                            selectedAlbumIds.add(intent.albumId)
                        }.toPersistentList(),
                    )
                }
            }
        }
    }

    private fun fetchInitialData(reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit) {
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

    private suspend fun fetchFavoriteSummary(reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit) {
        photoRepository.getFavoriteSummary()
            .onSuccess { data ->
                reduce { copy(favoriteAlbum = data) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private suspend fun fetchFolders(reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit) {
        folderRepository.getFolders()
            .onSuccess { data ->
                reduce { copy(albums = data.toImmutableList()) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private fun handleUploadButtonClick(
        state: UploadAlbumState,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
    ) {
        val firstAlbumId = state.selectedAlbumIds.firstOrNull() ?: return
        val onSuccessAction = {
            reduce { copy(isLoading = false) }
            postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지를 추가했어요"))
            postSideEffect(UploadAlbumSideEffect.NavigateToAlbumDetail(firstAlbumId))
        }
        val onFailureAction: (Throwable) -> Unit = { error ->
            Timber.e(error)
            reduce { copy(isLoading = false) }
            postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
        }

        if (state.uploadType == UploadType.SINGLE) {
            uploadSingleImage(
                imageUrl = state.imageUrl ?: return,
                albumId = firstAlbumId,
                reduce = reduce,
                onSuccessAction = onSuccessAction,
                onFailureAction = onFailureAction,
            )
        } else {
            uploadMultipleImages(
                imageUris = state.selectedUris,
                albumId = firstAlbumId,
                reduce = reduce,
                onSuccessAction = onSuccessAction,
                onFailureAction = onFailureAction,
            )
        }
    }

    private fun uploadSingleImage(
        imageUrl: String,
        albumId: Long,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        onSuccessAction: () -> Unit,
        onFailureAction: (Throwable) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadSinglePhotoUseCase(
                imageUrl = imageUrl,
                folderId = albumId,
            ).onSuccess { data ->
                Timber.d(data.toString())
                onSuccessAction()
            }.onFailure { error ->
                onFailureAction(error)
            }
        }
    }

    private fun uploadMultipleImages(
        imageUris: List<Uri>,
        albumId: Long,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        onSuccessAction: () -> Unit,
        onFailureAction: (Throwable) -> Unit,
    ) {
        viewModelScope.launch {
            reduce { copy(isLoading = true) }

            uploadMultiplePhotoUseCase(
                imageUris = imageUris,
                folderId = albumId,
            ).onSuccess {
                onSuccessAction()
            }.onFailure { error ->
                onFailureAction(error)
            }
        }
    }
}

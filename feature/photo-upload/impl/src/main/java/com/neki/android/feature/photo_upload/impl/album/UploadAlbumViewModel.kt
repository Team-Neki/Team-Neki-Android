package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.common.util.urlToByteArray
import com.neki.android.core.domain.usecase.UploadSinglePhotoUseCase
import com.neki.android.core.model.Album
import com.neki.android.core.model.UploadType
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = UploadAlbumViewModel.Factory::class)
class UploadAlbumViewModel @AssistedInject constructor(
    @Assisted private val imageUrl: String?,
    @Assisted private val uriStrings: List<String>,
    private val uploadSinglePhotoUseCase: UploadSinglePhotoUseCase,
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
        // TODO: Fetch albums from repository
        val dummyAlbums = persistentListOf(
            Album(
                id = 1,
                title = "Travel",
                photoList = persistentListOf(),
            ),
            Album(
                id = 2,
                title = "Family",
                photoList = persistentListOf(),
            ),
        )

        reduce {
            copy(
                albums = dummyAlbums,
                imageUrl = imageUrl,
                selectedUris = uriStrings.map { it.toUri() }.toImmutableList(),
            )
        }
    }

    private fun handleUploadButtonClick(
        state: UploadAlbumState,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
    ) {
        val firstAlbumId = state.albums.firstOrNull()?.id ?: return
        val onSuccessSideEffect = {
            postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지를 추가했어요"))
            postSideEffect(UploadAlbumSideEffect.NavigateToAlbumDetail(firstAlbumId))
        }

        if (state.uploadType == UploadType.QR_SCAN) {
            uploadSingleImage(
                imageUrl = state.imageUrl ?: return,
                albumId = firstAlbumId,
                postSideEffect = postSideEffect,
                onSuccess = onSuccessSideEffect,
            )
        } else {
            // TODO: 이미지 여러개 업로드
            uploadMultipleImages(
                imageUris = state.selectedUris,
                albumId = firstAlbumId,
                postSideEffect = postSideEffect,
                onSuccess = onSuccessSideEffect,
            )
        }
    }

    private fun uploadSingleImage(
        imageUrl: String,
        albumId: Long,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val imageBytes = imageUrl.urlToByteArray()

            uploadSinglePhotoUseCase(
                imageBytes = imageBytes,
                folderId = albumId,
            ).onSuccess { data ->
                Timber.d(data.toString())
                onSuccess()
            }.onFailure { error ->
                Timber.e(error)
                postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
            }
        }
    }

    private fun uploadMultipleImages(
        imageUris: List<Uri>,
        albumId: Long,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
        onSuccess: () -> Unit,
    ) {
        // TODO: 이미지 여러개 업로드
        postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지 업로드에 실패했어요"))
    }
}

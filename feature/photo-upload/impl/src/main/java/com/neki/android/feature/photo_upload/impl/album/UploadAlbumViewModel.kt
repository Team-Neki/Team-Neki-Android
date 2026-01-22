package com.neki.android.feature.photo_upload.impl.album

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Album
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList

@HiltViewModel(assistedFactory = UploadAlbumViewModel.Factory::class)
class UploadAlbumViewModel @AssistedInject constructor(
    @Assisted private val imageUrl: String?,
    @Assisted private val uriStrings: List<String>,
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
                id = 1001,
                title = "Travel",
                photoList = persistentListOf(),
            ),
            Album(
                id = 1002,
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
        postSideEffect(UploadAlbumSideEffect.ShowToastMessage("이미지를 추가했어요"))
        // TODO: Upload photos to repository
        postSideEffect(UploadAlbumSideEffect.NavigateToAlbumDetail(state.selectedAlbumIds.first()))
    }
}

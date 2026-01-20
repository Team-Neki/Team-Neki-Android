package com.neki.android.feature.photo_upload.impl.album

import android.net.Uri
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

@HiltViewModel(assistedFactory = UploadAlbumViewModel.Factory::class)
class UploadAlbumViewModel @AssistedInject constructor(
    @Assisted private val uri: Uri,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(uri: Uri): UploadAlbumViewModel
    }

    val store: MviIntentStore<UploadAlbumState, UploadAlbumIntent, UploadAlbumSideEffect> =
        mviIntentStore(
            initialState = UploadAlbumState(),
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
            is UploadAlbumIntent.ClickAlbumItem -> handleAlbumClick(intent.album, state, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit) {
        // TODO: Fetch albums from repository
        reduce {
            copy(selectedUris = persistentListOf(uri))
        }
    }

    private fun handleUploadButtonClick(
        state: UploadAlbumState,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
    ){
        postSideEffect(UploadAlbumSideEffect.NavigateBack)
    }

    private fun handleAlbumClick(
        album: Album,
        state: UploadAlbumState,
        reduce: (UploadAlbumState.() -> UploadAlbumState) -> Unit,
        postSideEffect: (UploadAlbumSideEffect) -> Unit,
    ) {
        postSideEffect(UploadAlbumSideEffect.NavigateToAlbumDetail(album))
    }
}

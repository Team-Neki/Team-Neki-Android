package com.neki.android.feature.archive.impl.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AllAlbumViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val folderRepository: FolderRepository,
) : ViewModel() {

    val store: MviIntentStore<AllAlbumState, AllAlbumIntent, AllAlbumSideEffect> =
        mviIntentStore(
            initialState = AllAlbumState(),
            onIntent = ::onIntent,
            initialFetchData = { store.onIntent(AllAlbumIntent.EnterAllAlbumScreen) },
        )

    private fun onIntent(
        intent: AllAlbumIntent,
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        when (intent) {
            AllAlbumIntent.EnterAllAlbumScreen -> fetchInitialData(reduce)

            // TopBar Intent
            AllAlbumIntent.ClickBackIcon -> handleBackClick(state, reduce, postSideEffect)
            AllAlbumIntent.OnBackPressed -> handleBackClick(state, reduce, postSideEffect)
            AllAlbumIntent.ClickCreateButton -> reduce { copy(isShowAddAlbumBottomSheet = true) }
            AllAlbumIntent.ClickOptionIcon -> reduce { copy(isShowOptionPopup = true) }
            AllAlbumIntent.DismissOptionPopup -> reduce { copy(isShowOptionPopup = false) }
            AllAlbumIntent.ClickDeleteOptionRow -> reduce {
                copy(
                    isShowOptionPopup = false,
                    selectMode = SelectMode.SELECTING,
                )
            }

            AllAlbumIntent.ClickDeleteButton -> handleDeleteButtonClick(state, reduce, postSideEffect)
            AllAlbumIntent.ClickCancelDeleteMode -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedAlbums = persistentListOf(),
                )
            }

            // Album Intent
            AllAlbumIntent.ClickFavoriteAlbum -> postSideEffect(
                AllAlbumSideEffect.NavigateToFavoriteAlbum(state.favoriteAlbum.id),
            )

            is AllAlbumIntent.ClickAlbumItem -> handleAlbumClick(intent.album, state, reduce, postSideEffect)

            // Add Album BottomSheet Intent
            AllAlbumIntent.DismissAddAlbumBottomSheet -> reduce { copy(isShowAddAlbumBottomSheet = false) }
            is AllAlbumIntent.ClickAddAlbumButton -> handleAddAlbum(intent.albumName, reduce, postSideEffect)

            // Delete Album BottomSheet Intent
            AllAlbumIntent.DismissDeleteAlbumBottomSheet -> reduce { copy(isShowDeleteAlbumBottomSheet = false) }
            is AllAlbumIntent.SelectDeleteOption -> reduce { copy(selectedDeleteOption = intent.option) }
            AllAlbumIntent.ClickDeleteConfirmButton -> handleDeleteConfirm(state, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (AllAlbumState.() -> AllAlbumState) -> Unit) {
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

    private suspend fun fetchFavoriteSummary(reduce: (AllAlbumState.() -> AllAlbumState) -> Unit) {
        photoRepository.getFavoriteSummary()
            .onSuccess { data ->
                reduce { copy(favoriteAlbum = data) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private suspend fun fetchFolders(reduce: (AllAlbumState.() -> AllAlbumState) -> Unit) {
        folderRepository.getFolders()
            .onSuccess { data ->
                reduce { copy(albums = data.toImmutableList()) }
            }
            .onFailure { error ->
                Timber.e(error)
            }
    }

    private fun handleBackClick(
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> postSideEffect(AllAlbumSideEffect.NavigateBack)
            SelectMode.SELECTING -> reduce {
                copy(
                    selectMode = SelectMode.DEFAULT,
                    selectedAlbums = persistentListOf(),
                )
            }
        }
    }

    private fun handleDeleteButtonClick(
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        if (state.selectedAlbums.isEmpty()) {
            postSideEffect(AllAlbumSideEffect.ShowToastMessage("앨범을 선택해주세요."))
            return
        }
        reduce { copy(isShowDeleteAlbumBottomSheet = true) }
    }

    private fun handleAlbumClick(
        album: AlbumPreview,
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AllAlbumSideEffect.NavigateToAlbumDetail(album.id, album.title))
            }

            SelectMode.SELECTING -> {
                val album = state.albums.find { it.id == album.id } ?: return
                val isSelected = state.selectedAlbums.any { it.id == album.id }
                if (isSelected) {
                    reduce {
                        copy(selectedAlbums = selectedAlbums.filter { it.id != album.id }.toImmutableList())
                    }
                } else {
                    reduce {
                        copy(selectedAlbums = (selectedAlbums + album).toImmutableList())
                    }
                }
            }
        }
    }

    private fun handleAddAlbum(
        albumName: String,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            folderRepository.createFolder(name = albumName)
                .onSuccess {
                    fetchFolders(reduce)
                    postSideEffect(AllAlbumSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
                }
                .onFailure { error ->
                    postSideEffect(AllAlbumSideEffect.ShowToastMessage("앨범 추가에 실패했어요"))
                    Timber.e(error)
                }
            reduce { copy(isShowAddAlbumBottomSheet = false) }
        }
    }

    private fun handleDeleteConfirm(
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        // TODO: 삭제 타입에 따라 핸들링
        viewModelScope.launch {
            val selectedAlbumIds = state.selectedAlbums.map { it.id }

            folderRepository.deleteFolder(selectedAlbumIds)
                .onSuccess {
                    fetchFolders(reduce)
                    postSideEffect(AllAlbumSideEffect.ShowToastMessage("앨범을 삭제했어요"))
                }
                .onFailure { error ->
                    Timber.e(error, "사진 삭제 실패")
                    postSideEffect(AllAlbumSideEffect.ShowToastMessage("앨범 삭제에 실패했어요"))
                }
            reduce {
                copy(
                    selectedAlbums = persistentListOf(),
                    selectMode = SelectMode.DEFAULT,
                    isShowDeleteAlbumBottomSheet = false,
                )
            }
        }
    }
}

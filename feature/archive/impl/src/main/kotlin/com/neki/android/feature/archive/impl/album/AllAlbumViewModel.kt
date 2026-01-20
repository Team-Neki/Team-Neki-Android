package com.neki.android.feature.archive.impl.album

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Album
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.feature.archive.impl.model.SelectMode
import com.neki.android.core.ui.mviIntentStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class AllAlbumViewModel @Inject constructor() : ViewModel() {

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
            AllAlbumIntent.ClickCreateButton -> reduce { copy(showAddAlbumBottomSheet = true) }
            AllAlbumIntent.ClickOptionIcon -> reduce { copy(showOptionPopup = true) }
            AllAlbumIntent.DismissOptionPopup -> reduce { copy(showOptionPopup = false) }
            AllAlbumIntent.ClickDeleteOptionRow -> reduce {
                copy(
                    showOptionPopup = false,
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
                AllAlbumSideEffect.NavigateToFavoriteAlbum(state.favoriteAlbum),
            )

            is AllAlbumIntent.ClickAlbumItem -> handleAlbumClick(intent.album, state, reduce, postSideEffect)

            // Add Album BottomSheet Intent
            AllAlbumIntent.DismissAddAlbumBottomSheet -> reduce { copy(showAddAlbumBottomSheet = false) }
            is AllAlbumIntent.ClickAddAlbumButton -> handleAddAlbum(intent.albumName, reduce, postSideEffect)

            // Delete Album BottomSheet Intent
            AllAlbumIntent.DismissDeleteAlbumBottomSheet -> reduce { copy(showDeleteAlbumBottomSheet = false) }
            is AllAlbumIntent.SelectDeleteOption -> reduce { copy(selectedDeleteOption = intent.option) }
            AllAlbumIntent.ClickDeleteConfirmButton -> handleDeleteConfirm(state, reduce, postSideEffect)
        }
    }

    private fun fetchInitialData(reduce: (AllAlbumState.() -> AllAlbumState) -> Unit) {
        // TODO: Fetch albums from repository
        reduce { copy() }
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
        reduce { copy(showDeleteAlbumBottomSheet = true) }
    }

    private fun handleAlbumClick(
        album: Album,
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AllAlbumSideEffect.NavigateToAlbumDetail(album))
            }

            SelectMode.SELECTING -> {
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
        // TODO: Add album to repository
        reduce {
            copy(
                showAddAlbumBottomSheet = false,
                albums = (albums + Album(id = albums.size.toLong(), title = albumName)).toImmutableList(),
            )
        }
        postSideEffect(AllAlbumSideEffect.ShowToastMessage("새로운 앨범을 추가했어요"))
    }

    private fun handleDeleteConfirm(
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        // TODO: Delete albums from repository based on selectedDeleteOption
        reduce {
            copy(
                albums = albums.filter { album -> selectedAlbums.none { it.id == album.id } }.toImmutableList(),
                selectedAlbums = persistentListOf(),
                selectMode = SelectMode.DEFAULT,
                showDeleteAlbumBottomSheet = false,
            )
        }
        postSideEffect(AllAlbumSideEffect.ShowToastMessage("앨범을 삭제했어요"))
    }
}

package com.neki.android.feature.archive.impl.album

import androidx.lifecycle.ViewModel
import com.neki.android.core.model.Album
import com.neki.android.core.model.Photo
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
                AllAlbumSideEffect.NavigateToFavoriteAlbum(state.favoriteAlbum.id),
            )

            is AllAlbumIntent.ClickAlbumItem -> handleAlbumClick(intent.albumId, state, reduce, postSideEffect)

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
        val travelPhotos = persistentListOf(
            Photo(id = 101, imageUrl = "https://picsum.photos/seed/travel1/400/500", date = "2025.01.10"),
            Photo(id = 102, imageUrl = "https://picsum.photos/seed/travel2/400/600", date = "2025.01.10"),
            Photo(id = 103, imageUrl = "https://picsum.photos/seed/travel3/400/480", date = "2025.01.09"),
            Photo(id = 104, imageUrl = "https://picsum.photos/seed/travel4/400/550", date = "2025.01.09"),
            Photo(id = 105, imageUrl = "https://picsum.photos/seed/travel5/400/620", date = "2025.01.08"),
        )

        val familyPhotos = persistentListOf(
            Photo(id = 201, imageUrl = "https://picsum.photos/seed/family1/400/520", date = "2025.01.05"),
            Photo(id = 202, imageUrl = "https://picsum.photos/seed/family2/400/680", date = "2025.01.05"),
            Photo(id = 203, imageUrl = "https://picsum.photos/seed/family3/400/450", date = "2025.01.04"),
        )

        val friendPhotos = persistentListOf(
            Photo(id = 301, imageUrl = "https://picsum.photos/seed/friend1/400/580", date = "2024.12.25"),
            Photo(id = 302, imageUrl = "https://picsum.photos/seed/friend2/400/620", date = "2024.12.25"),
            Photo(id = 303, imageUrl = "https://picsum.photos/seed/friend3/400/500", date = "2024.12.24"),
            Photo(id = 304, imageUrl = "https://picsum.photos/seed/friend4/400/700", date = "2024.12.24"),
            Photo(id = 305, imageUrl = "https://picsum.photos/seed/friend5/400/460", date = "2024.12.23"),
        )

        val workPhotos = persistentListOf(
            Photo(id = 401, imageUrl = "https://picsum.photos/seed/work1/400/550", date = "2024.12.20"),
            Photo(id = 402, imageUrl = "https://picsum.photos/seed/work2/400/480", date = "2024.12.19"),
            Photo(id = 403, imageUrl = "https://picsum.photos/seed/work3/400/620", date = "2024.12.18"),
        )

        val petPhotos = persistentListOf(
            Photo(id = 501, imageUrl = "https://picsum.photos/seed/pet1/400/600", date = "2024.12.15"),
            Photo(id = 502, imageUrl = "https://picsum.photos/seed/pet2/400/520", date = "2024.12.14"),
            Photo(id = 503, imageUrl = "https://picsum.photos/seed/pet3/400/680", date = "2024.12.13"),
            Photo(id = 504, imageUrl = "https://picsum.photos/seed/pet4/400/450", date = "2024.12.12"),
        )

        val dummyAlbums = persistentListOf(
            Album(id = 1, title = "제주도 여행 2025", thumbnailUrl = "https://picsum.photos/seed/travel1/400/500", photoList = travelPhotos),
            Album(id = 2, title = "가족 생일파티", thumbnailUrl = "https://picsum.photos/seed/family1/400/520", photoList = familyPhotos),
            Album(id = 3, title = "대학 동기 모임", thumbnailUrl = "https://picsum.photos/seed/friend1/400/580", photoList = friendPhotos),
            Album(id = 4, title = "회사 워크샵", thumbnailUrl = "https://picsum.photos/seed/work1/400/550", photoList = workPhotos),
            Album(id = 5, title = "우리집 반려동물", thumbnailUrl = "https://picsum.photos/seed/pet1/400/600", photoList = petPhotos),
        )

        val favoritePhotos = persistentListOf(
            Photo(id = 601, imageUrl = "https://picsum.photos/seed/fav1/400/520", isFavorite = true, date = "2025.01.15"),
            Photo(id = 602, imageUrl = "https://picsum.photos/seed/fav2/400/680", isFavorite = true, date = "2025.01.14"),
            Photo(id = 603, imageUrl = "https://picsum.photos/seed/fav3/400/450", isFavorite = true, date = "2025.01.13"),
            Photo(id = 604, imageUrl = "https://picsum.photos/seed/fav4/400/600", isFavorite = true, date = "2025.01.12"),
            Photo(id = 605, imageUrl = "https://picsum.photos/seed/fav5/400/550", isFavorite = true, date = "2025.01.11"),
        )

        val favoriteAlbum = Album(
            id = 0,
            title = "즐겨찾는 사진",
            thumbnailUrl = favoritePhotos.firstOrNull()?.imageUrl,
            photoList = favoritePhotos,
        )

        reduce {
            copy(
                favoriteAlbum = favoriteAlbum,
                albums = dummyAlbums,
            )
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
        reduce { copy(showDeleteAlbumBottomSheet = true) }
    }

    private fun handleAlbumClick(
        albumId: Long,
        state: AllAlbumState,
        reduce: (AllAlbumState.() -> AllAlbumState) -> Unit,
        postSideEffect: (AllAlbumSideEffect) -> Unit,
    ) {
        when (state.selectMode) {
            SelectMode.DEFAULT -> {
                postSideEffect(AllAlbumSideEffect.NavigateToAlbumDetail(albumId))
            }

            SelectMode.SELECTING -> {
                val album = state.albums.find { it.id == albumId } ?: return
                val isSelected = state.selectedAlbums.any { it.id == albumId }
                if (isSelected) {
                    reduce {
                        copy(selectedAlbums = selectedAlbums.filter { it.id != albumId }.toImmutableList())
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

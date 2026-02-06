package com.neki.android.feature.archive.impl.album

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.ui.component.AlbumRowComponent
import com.neki.android.core.ui.component.DoubleButtonOptionBottomSheet
import com.neki.android.core.ui.component.FavoriteAlbumRowComponent
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.impl.album.component.AllAlbumTopBar
import com.neki.android.feature.archive.impl.component.AddAlbumBottomSheet
import com.neki.android.feature.archive.impl.model.SelectMode
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun AllAlbumRoute(
    viewModel: AllAlbumViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToFavoriteAlbum: (Long) -> Unit,
    navigateToAlbumDetail: (Long, String) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            AllAlbumSideEffect.NavigateBack -> navigateBack()
            is AllAlbumSideEffect.NavigateToFavoriteAlbum -> navigateToFavoriteAlbum(sideEffect.albumId)
            is AllAlbumSideEffect.NavigateToAlbumDetail -> navigateToAlbumDetail(sideEffect.albumId, sideEffect.title)
            is AllAlbumSideEffect.ShowToastMessage -> {
                nekiToast.showToast(text = sideEffect.message)
            }
        }
    }

    AllAlbumScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AllAlbumScreen(
    uiState: AllAlbumState = AllAlbumState(),
    onIntent: (AllAlbumIntent) -> Unit = {},
) {
    BackHandler(enabled = true) {
        onIntent(AllAlbumIntent.OnBackPressed)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        AllAlbumTopBar(
            selectMode = uiState.selectMode,
            showOptionPopup = uiState.isShowOptionPopup,
            onClickBack = { onIntent(AllAlbumIntent.ClickBackIcon) },
            onClickCreate = { onIntent(AllAlbumIntent.ClickCreateButton) },
            onClickOption = { onIntent(AllAlbumIntent.ClickOptionIcon) },
            onDismissPopup = { onIntent(AllAlbumIntent.DismissOptionPopup) },
            onClickDeleteOption = { onIntent(AllAlbumIntent.ClickDeleteOptionRow) },
            onClickDelete = { onIntent(AllAlbumIntent.ClickDeleteButton) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                FavoriteAlbumRowComponent(
                    album = uiState.favoriteAlbum,
                    onClick = { onIntent(AllAlbumIntent.ClickFavoriteAlbum) },
                )
            }

            items(
                items = uiState.albums,
                key = { album -> album.id },
            ) { album ->
                val isSelected = uiState.selectedAlbums.any { it.id == album.id }
                AlbumRowComponent(
                    album = album,
                    isSelectable = uiState.selectMode == SelectMode.SELECTING,
                    isSelected = isSelected,
                    onClick = { onIntent(AllAlbumIntent.ClickAlbumItem(album)) },
                )
            }
        }
    }

    if (uiState.isShowAddAlbumBottomSheet) {
        val textFieldState = rememberTextFieldState()
        val existingAlbumNames = remember(uiState.albums) { uiState.albums.map { it.title } }

        val errorMessage by remember(textFieldState.text) {
            derivedStateOf {
                val name = textFieldState.text.toString()
                if (existingAlbumNames.contains(name)) {
                    "이미 사용 중인 앨범명이에요."
                } else {
                    null
                }
            }
        }

        AddAlbumBottomSheet(
            textFieldState = textFieldState,
            onDismissRequest = { onIntent(AllAlbumIntent.DismissAddAlbumBottomSheet) },
            onClickCancel = { onIntent(AllAlbumIntent.DismissAddAlbumBottomSheet) },
            onClickConfirm = {
                val albumName = textFieldState.text.toString()
                if (errorMessage == null && albumName.isNotBlank()) {
                    onIntent(AllAlbumIntent.ClickAddAlbumButton(albumName))
                }
            },
            isError = errorMessage != null,
            errorMessage = errorMessage,
        )
    }

    if (uiState.isShowDeleteAlbumBottomSheet) {
        DoubleButtonOptionBottomSheet(
            title = "앨범을 삭제하시겠어요?",
            options = AlbumDeleteOption.entries.toImmutableList(),
            primaryButtonText = "삭제하기",
            secondaryButtonText = "취소",
            selectedOption = uiState.selectedDeleteOption,
            onDismissRequest = { onIntent(AllAlbumIntent.DismissDeleteAlbumBottomSheet) },
            onClickSecondaryButton = { onIntent(AllAlbumIntent.DismissDeleteAlbumBottomSheet) },
            onClickPrimaryButton = { onIntent(AllAlbumIntent.ClickDeleteConfirmButton) },
            onOptionSelect = { onIntent(AllAlbumIntent.SelectDeleteOption(it)) },
        )
    }
}

@DevicePreview
@Composable
private fun AllAlbumScreenPreview() {
    NekiTheme {
        AllAlbumScreen(
            uiState = AllAlbumState(
                favoriteAlbum = AlbumPreview(id = 0, title = "즐겨찾는 사진", photoCount = 3),
                albums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                    AlbumPreview(id = 3, title = "대학 동기 모임", photoCount = 3),
                    AlbumPreview(id = 4, title = "회사 송년회", photoCount = 5),
                ),
            ),
        )
    }
}

@DevicePreview
@Composable
private fun AllAlbumScreenSelectingPreview() {
    NekiTheme {
        AllAlbumScreen(
            uiState = AllAlbumState(
                favoriteAlbum = AlbumPreview(id = 0, title = "즐겨찾는 사진", photoCount = 3),
                albums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                    AlbumPreview(id = 3, title = "대학 동기 모임", photoCount = 3),
                ),
                selectMode = SelectMode.SELECTING,
                selectedAlbums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 3, title = "대학 동기 모임", photoCount = 3),
                ),
            ),
        )
    }
}

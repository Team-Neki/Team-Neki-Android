package com.neki.android.feature.select_album.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.DevicePreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.NekiTextFieldBottomSheet
import com.neki.android.core.designsystem.modifier.dashStroke
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.AlbumPreview
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.ui.component.AlbumRowComponent
import com.neki.android.core.ui.component.FavoriteAlbumRowComponent
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.archive.api.PhotoCopiedResult
import com.neki.android.feature.archive.api.PhotoMovedResult
import com.neki.android.feature.select_album.impl.component.SelectAlbumTopBar
import kotlinx.collections.immutable.persistentListOf

private const val ALBUM_NAME_MAX_LENGTH = 10

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectAlbumRoute(
    viewModel: SelectAlbumViewModel,
    navigateBack: () -> Unit,
    navigateToAlbumDetail: (Long, String) -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }
    val resultEventBus = LocalResultEventBus.current

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            SelectAlbumSideEffect.NavigateBack -> navigateBack()
            is SelectAlbumSideEffect.SendUploadResult -> {
                navigateBack()
                navigateToAlbumDetail(sideEffect.album.id, sideEffect.album.title)
            }

            SelectAlbumSideEffect.SendPhotoMovedResult -> {
                resultEventBus.sendResult(result = PhotoMovedResult, allowDuplicate = false)
            }

            is SelectAlbumSideEffect.SendPhotoCopiedResult -> {
                resultEventBus.sendResult(result = PhotoCopiedResult(sideEffect.albumIds, sideEffect.albumTitle), allowDuplicate = false)
            }

            is SelectAlbumSideEffect.ShowToastMessage -> nekiToast.showToast(sideEffect.message)
        }
    }

    SelectAlbumScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectAlbumScreen(
    uiState: SelectAlbumState = SelectAlbumState(),
    onIntent: (SelectAlbumIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NekiTheme.colorScheme.white),
    ) {
        SelectAlbumTopBar(
            title = uiState.title,
            photoCount = uiState.photoCount,
            isConfirmEnabled = uiState.isConfirmEnabled,
            onBack = { onIntent(SelectAlbumIntent.ClickBackIcon) },
            onClickUpload = { onIntent(SelectAlbumIntent.ClickConfirmButton) },
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                CreateAlbumRow(onClick = { onIntent(SelectAlbumIntent.ClickCreateAlbumButton) })
            }

            item {
                FavoriteAlbumRowComponent(album = uiState.favoriteAlbum)
            }

            items(
                items = uiState.albums,
                key = { album -> album.id },
            ) { album ->
                val isSelected = uiState.selectedAlbums.any { it.id == album.id }
                val isDisabled = album.id == uiState.disabledAlbumId
                AlbumRowComponent(
                    album = album,
                    isSelectable = true,
                    isSelected = isSelected,
                    isDisabled = isDisabled,
                    onClick = { onIntent(SelectAlbumIntent.ClickAlbumItem(album)) },
                )
            }
        }
    }

    if (uiState.isLoading || uiState.isUploading) {
        LoadingDialog()
    }

    if (uiState.isShowAddAlbumBottomSheet) {
        val existingAlbumNames = remember(uiState.albums) { uiState.albums.map { it.title } }
        val errorMessage by remember(uiState.albumNameTextState.text) {
            derivedStateOf {
                val name = uiState.albumNameTextState.text.toString()
                if (existingAlbumNames.contains(name)) "이미 사용 중인 앨범명이에요." else null
            }
        }
        NekiTextFieldBottomSheet(
            title = "새 앨범 추가",
            subtitle = "네컷사진을 모을 앨범명을 입력하세요",
            textFieldState = uiState.albumNameTextState,
            placeholder = "앨범명을 입력하세요",
            maxLength = ALBUM_NAME_MAX_LENGTH,
            confirmButtonText = "추가하기",
            isError = errorMessage != null,
            errorMessage = errorMessage,
            onDismissRequest = { onIntent(SelectAlbumIntent.DismissAddAlbumBottomSheet) },
            onClickCancel = { onIntent(SelectAlbumIntent.DismissAddAlbumBottomSheet) },
            onClickConfirm = { onIntent(SelectAlbumIntent.ClickAddAlbumConfirmButton) },
        )
    }
}

@Composable
private fun CreateAlbumRow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .noRippleClickable(onClick = onClick)
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .dashStroke(color = NekiTheme.colorScheme.primary400, cornerRadius = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_plus),
                tint = NekiTheme.colorScheme.primary400,
                contentDescription = null,
            )
        }

        Text(
            text = "새 앨범 추가",
            style = NekiTheme.typography.body16SemiBold,
            color = NekiTheme.colorScheme.gray600,
        )
    }
}

@DevicePreview
@Composable
private fun SelectAlbumScreenPreview() {
    NekiTheme {
        SelectAlbumScreen(
            uiState = SelectAlbumState(
                title = "앨범에 추가",
                multiSelect = true,
                photoCount = 1,
                favoriteAlbum = AlbumPreview(id = 0, title = "즐겨찾기", photoCount = 3),
                albums = persistentListOf(
                    AlbumPreview(id = 1, title = "제주도 여행 2024", photoCount = 4),
                    AlbumPreview(id = 2, title = "가족 생일파티", photoCount = 2),
                ),
            ),
        )
    }
}

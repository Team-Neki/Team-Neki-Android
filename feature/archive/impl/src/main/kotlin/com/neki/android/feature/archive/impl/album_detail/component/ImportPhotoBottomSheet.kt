package com.neki.android.feature.archive.impl.album_detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.bottomsheet.BottomSheetDragHandle
import com.neki.android.core.designsystem.button.NekiButton
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.button.NekiTextButton
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.album_detail.AlbumDetailIntent
import com.neki.android.feature.archive.impl.album_detail.AlbumFilterOption
import com.neki.android.feature.archive.impl.album_detail.ImportPhotoState
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImportPhotoBottomSheet(
    uiState: ImportPhotoState,
    onIntent: (AlbumDetailIntent) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val sheetHeight = remember {
        configuration.screenHeightDp.dp - statusBarHeight - navigationBarHeight
    }

    ModalBottomSheet(
        onDismissRequest = { onIntent(AlbumDetailIntent.DismissImportPhotoBottomSheet) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDragHandle() },
        containerColor = NekiTheme.colorScheme.white,
        contentWindowInsets = { WindowInsets() },
    ) {
        Column(
            modifier = Modifier
                .height(sheetHeight)
                .navigationBarsPadding()
                .fillMaxWidth(),
        ) {
            ImportPhotoTopRow(
                selectedAlbumOption = uiState.selectedAlbumOption,
                allAlbumOptions = uiState.allAlbumOptions,
                isShowAlbumDropdown = uiState.isShowAlbumDropdown,
                currentAlbumId = uiState.currentAlbumId,
                onClickFilter = { onIntent(AlbumDetailIntent.ToggleImportAlbumDropdown) },
                onSelectAlbum = { onIntent(AlbumDetailIntent.SelectImportAlbum(it)) },
                onDismissDropdown = { onIntent(AlbumDetailIntent.DismissImportAlbumDropdown) },
                onDismiss = { onIntent(AlbumDetailIntent.DismissImportPhotoBottomSheet) },
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                if (uiState.photos.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    color = NekiTheme.colorScheme.gray200,
                                    shape = RoundedCornerShape(8.dp),
                                ),
                        )
                        Text(
                            text = "아직 등록된 사진이 없어요",
                            style = NekiTheme.typography.body16Medium,
                            color = NekiTheme.colorScheme.gray500,
                        )
                    }
                } else {
                    val gridState = rememberLazyGridState()
                    val isAtBottom by remember { derivedStateOf { !gridState.canScrollForward } }

                    LazyVerticalGrid(
                        state = gridState,
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        items(uiState.photos, key = { it.id }) { photo ->
                            ImportPhotoGridItem(
                                photo = photo,
                                isSelected = photo.id in uiState.selectedPhotoIds,
                                onToggleSelect = { onIntent(AlbumDetailIntent.ToggleImportPhoto(photo.id)) },
                            )
                        }
                    }

                    if (!isAtBottom) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(197.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, NekiTheme.colorScheme.white),
                                    ),
                                ),
                        )
                    }
                }
            }

            NekiButton(
                modifier = Modifier
                    .width(335.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp),
                enabled = uiState.selectedPhotoIds.isNotEmpty(),
                onClick = { onIntent(AlbumDetailIntent.ConfirmImport) },
                shape = RoundedCornerShape(12.dp),
                containerColor = NekiTheme.colorScheme.primary400,
                contentColor = NekiTheme.colorScheme.white,
                disabledContainerColor = NekiTheme.colorScheme.primary100,
                disabledContentColor = NekiTheme.colorScheme.white,
                contentPadding = PaddingValues(vertical = 14.dp),
            ) {
                Text(
                    text = "${uiState.selectedPhotoIds.size}장 업로드",
                    style = NekiTheme.typography.body16SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ImportPhotoTopRow(
    selectedAlbumOption: AlbumFilterOption?,
    allAlbumOptions: ImmutableList<AlbumFilterOption>,
    isShowAlbumDropdown: Boolean,
    currentAlbumId: Long?,
    onClickFilter: () -> Unit,
    onSelectAlbum: (Long?) -> Unit,
    onDismissDropdown: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            NekiTextButton(
                onClick = onClickFilter,
                contentPadding = PaddingValues(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = selectedAlbumOption?.title ?: "전체사진",
                        style = NekiTheme.typography.title20SemiBold,
                        color = NekiTheme.colorScheme.gray900,
                    )
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_down),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray700,
                    )
                }
            }

            if (isShowAlbumDropdown) {
                AlbumFilterDropdown(
                    options = allAlbumOptions,
                    selectedOption = selectedAlbumOption,
                    currentAlbumId = currentAlbumId,
                    onSelect = { option -> onSelectAlbum(option.id) },
                    onDismissRequest = onDismissDropdown,
                )
            }
        }

        NekiIconButton(
            onClick = onDismiss,
            shape = RectangleShape,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                contentDescription = null,
                tint = NekiTheme.colorScheme.gray700,
            )
        }
    }
}

@Composable
private fun AlbumFilterDropdown(
    options: ImmutableList<AlbumFilterOption>,
    selectedOption: AlbumFilterOption?,
    currentAlbumId: Long?,
    onSelect: (AlbumFilterOption) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 20.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 51.dp.toPx().toInt() }

    Popup(
        offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
        alignment = Alignment.TopStart,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = modifier
                .width(159.dp)
                .dropdownShadow(shape = RoundedCornerShape(12.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            options.forEach { option ->
                val isCurrentAlbum = option.id != null && option.id == currentAlbumId
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (!isCurrentAlbum) Modifier.clickableSingle { onSelect(option) } else Modifier,
                        )
                        .padding(start = 12.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = option.title,
                        style = NekiTheme.typography.body16Medium,
                        overflow = TextOverflow.Ellipsis,
                        color = when {
                            isCurrentAlbum -> NekiTheme.colorScheme.gray300
                            option == selectedOption -> NekiTheme.colorScheme.primary500
                            else -> NekiTheme.colorScheme.gray900
                        },
                    )
                    Text(
                        text = "${option.photoCount}",
                        style = NekiTheme.typography.caption12Medium,
                        color = NekiTheme.colorScheme.gray300,
                    )
                }
            }
        }
    }
}

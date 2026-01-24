package com.neki.android.feature.archive.impl.album.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.modifier.dropdownShadow
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonOptionTopBar
import com.neki.android.core.designsystem.topbar.BackTitleTextButtonTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.archive.impl.model.SelectMode

@Composable
internal fun AllAlbumTopBar(
    selectMode: SelectMode,
    showOptionPopup: Boolean,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOptionClick: () -> Unit = {},
    onDismissPopup: () -> Unit = {},
    onDeleteOptionClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Box {
        when (selectMode) {
            SelectMode.DEFAULT -> {
                DefaultTopBar(
                    modifier = modifier,
                    onBackClick = onBackClick,
                    onCreateClick = onCreateClick,
                    onOptionClick = onOptionClick,
                )
            }

            SelectMode.SELECTING -> {
                SelectingTopBar(
                    modifier = modifier,
                    onBackClick = onBackClick,
                    onDeleteClick = onDeleteClick,
                )
            }
        }

        if (showOptionPopup) {
            OptionPopup(
                onDismissRequest = onDismissPopup,
                onDeleteOptionClick = onDeleteOptionClick,
            )
        }
    }
}

@Composable
private fun DefaultTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onOptionClick: () -> Unit = {},
) {
    BackTitleTextButtonOptionTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "생성",
        optionIconRes = R.drawable.icon_option,
        onBack = onBackClick,
        onTextButtonClick = onCreateClick,
        onIconClick = onOptionClick,
    )
}

@Composable
private fun SelectingTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    BackTitleTextButtonTopBar(
        modifier = modifier,
        title = "모든 앨범",
        buttonLabel = "삭제",
        onBack = onBackClick,
        onTextButtonClick = onDeleteClick,
    )
}

@Composable
private fun OptionPopup(
    onDismissRequest: () -> Unit,
    onDeleteOptionClick: () -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { (-20).dp.toPx().toInt() }
    val popupOffsetY = with(density) { 47.dp.toPx().toInt() }

    Popup(
        offset = IntOffset(x = popupOffsetX, y = popupOffsetY),
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier
                .dropdownShadow(shape = RoundedCornerShape(12.dp))
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(vertical = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .width(158.dp)
                    .clickableSingle {
                        onDeleteOptionClick()
                        onDismissRequest()
                    }
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                text = "삭제하기",
                style = NekiTheme.typography.body16Medium,
                color = NekiTheme.colorScheme.gray900,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarDefaultPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = false,
        )
    }
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarWithPopupPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.DEFAULT,
            showOptionPopup = true,
        )
    }
}

@ComponentPreview
@Composable
private fun AllAlbumTopBarSelectingPreview() {
    NekiTheme {
        AllAlbumTopBar(
            selectMode = SelectMode.SELECTING,
            showOptionPopup = false,
        )
    }
}

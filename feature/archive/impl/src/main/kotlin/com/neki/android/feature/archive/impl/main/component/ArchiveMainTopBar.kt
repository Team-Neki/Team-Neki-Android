package com.neki.android.feature.archive.impl.main.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.logo.PrimaryNekiTypoLogo
import com.neki.android.core.designsystem.popup.ArrowDirection
import com.neki.android.core.designsystem.popup.ToolTipPopup
import com.neki.android.core.designsystem.topbar.NekiLeftTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun ArchiveMainTopBar(
    modifier: Modifier = Modifier,
    onClickQRCodeIcon: () -> Unit = {},
    onClickNotificationIcon: () -> Unit = {},
    onDismissToolTipPopup: () -> Unit = {},
    showTooltip: Boolean = true,
) {
    NekiLeftTitleTopBar(
        modifier = modifier.padding(start = 20.dp, end = 12.dp),
        title = {
            PrimaryNekiTypoLogo()
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box {
                    NekiIconButton(
                        onClick = onClickQRCodeIcon,
                        contentPadding = PaddingValues(8.dp),
                    ) {
                        Icon(
                            modifier = Modifier.size(28.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_qr_scan),
                            contentDescription = null,
                            tint = NekiTheme.colorScheme.gray500,
                        )
                    }

                    if (showTooltip) {
                        ArchiveToolTip(
                            onDismissRequest = onDismissToolTipPopup,
                        )
                    }
                }
                NekiIconButton(
                    onClick = onClickNotificationIcon,
                    contentPadding = PaddingValues(8.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.icon_bell),
                        contentDescription = null,
                        tint = NekiTheme.colorScheme.gray500,
                    )
                }
            }
        },
    )
}

@Composable
private fun ArchiveToolTip(
    onDismissRequest: () -> Unit = {},
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 1.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 47.dp.toPx().toInt() }
    val offset = IntOffset(popupOffsetX, popupOffsetY)

    ToolTipPopup(
        tooltipText = "버튼을 눌러 네컷을 추가할 수 있어요",
        offset = offset,
        alignment = Alignment.TopEnd,
        arrowDirection = ArrowDirection.Up,
        arrowAlignment = Alignment.CenterEnd,
        onDismissRequest = onDismissRequest,
    )
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarPreview() {
    NekiTheme {
        ArchiveMainTopBar(
            showTooltip = false,
        )
    }
}

@ComponentPreview
@Composable
private fun ArchiveMainTopBarWithTooltipPreview() {
    NekiTheme {
        Box(modifier = Modifier.height(200.dp)) {
            ArchiveMainTopBar(
                showTooltip = true,
            )
        }
    }
}

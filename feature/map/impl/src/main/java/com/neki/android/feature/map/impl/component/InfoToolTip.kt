package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.layout.Box
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
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.popup.ToolTipPopup
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun InfoToolTip(
    isShowTooltip: Boolean,
    onClickInfoIcon: () -> Unit,
    onDismissTooltip: () -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 9.dp.toPx().toInt() }
    val popupOffsetY = with(density) { 34.dp.toPx().toInt() }

    Box {
        Icon(
            modifier = Modifier
                .noRippleClickableSingle(onClick = onClickInfoIcon)
                .padding(start = 2.dp, top = 2.dp, bottom = 2.dp)
                .size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_info_stroked),
            contentDescription = null,
            tint = NekiTheme.colorScheme.gray300,
        )
        if (isShowTooltip) {
            ToolTipPopup(
                tooltipText = "가까운 네컷 사진 브랜드는\n1km 기준으로 표시돼요.",
                color = NekiTheme.colorScheme.gray800,
                offset = IntOffset(popupOffsetX, popupOffsetY),
                alignment = Alignment.TopEnd,
                onDismissRequest = onDismissTooltip,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun InfoToolTipPreview() {
    NekiTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            InfoToolTip(
                isShowTooltip = true,
                onClickInfoIcon = {},
                onDismissTooltip = {},
            )
        }
    }
}

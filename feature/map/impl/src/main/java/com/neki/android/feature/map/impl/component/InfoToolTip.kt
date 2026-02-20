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
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.popup.ArrowDirection
import com.neki.android.core.designsystem.popup.ToolTipPopup
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.DragLevel

@Composable
internal fun InfoToolTip(
    dragLevel: DragLevel,
    isShowInfoTooltip: Boolean,
    onClickInfoIcon: () -> Unit,
    onDismissTooltip: () -> Unit,
) {
    val density = LocalDensity.current
    val popupOffsetX = with(density) { 9.dp.toPx().toInt() }
    val popupOffsetY = with(density) {
        when (dragLevel) {
            DragLevel.SECOND -> (-34).dp.toPx().toInt()
            DragLevel.THIRD -> 34.dp.toPx().toInt()
            else -> 0
        }
    }
    val tooltipAlignment = when (dragLevel) {
        DragLevel.THIRD -> Alignment.TopEnd
        else -> Alignment.BottomEnd
    }
    val arrowDirection = when (dragLevel) {
        DragLevel.THIRD -> ArrowDirection.Up
        else -> ArrowDirection.Down
    }

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
        if (isShowInfoTooltip) {
            ToolTipPopup(
                tooltipText = "가까운 네컷 사진 브랜드는\n1km 기준으로 표시돼요.",
                offset = IntOffset(popupOffsetX, popupOffsetY),
                alignment = tooltipAlignment,
                arrowDirection = arrowDirection,
                arrowAlignment = Alignment.CenterEnd,
                onDismissRequest = onDismissTooltip,
                properties = PopupProperties(focusable = true),
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
                dragLevel = DragLevel.SECOND,
                isShowInfoTooltip = true,
                onClickInfoIcon = {},
                onDismissTooltip = {},
            )
        }
    }
}

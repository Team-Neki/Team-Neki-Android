package com.neki.android.core.designsystem.popup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun ToolTipPopup(
    tooltipText: String,
    color: Color,
    offset: IntOffset,
    alignment: Alignment,
    onDismissRequest: () -> Unit,
) {
    Popup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
    ) {
        ToolTipContent(
            tooltipText = tooltipText,
            color = color,
        )
    }
}

@Composable
private fun ToolTipContent(
    tooltipText: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Max),
    ) {
        // 꼬리 (오른쪽 정렬, 오른쪽에서 16dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Canvas(
                modifier = Modifier.size(width = 10.dp, height = 8.dp),
            ) {
                val cornerRadius = 1.dp.toPx()
                val path = Path().apply {
                    // 왼쪽 하단에서 시작
                    moveTo(0f, size.height)
                    // 왼쪽 하단 -> 꼭대기 (둥근 모서리)
                    lineTo(
                        size.width / 2 - cornerRadius,
                        cornerRadius,
                    )
                    quadraticTo(
                        size.width / 2,
                        0f,
                        size.width / 2 + cornerRadius,
                        cornerRadius,
                    )
                    // 꼭대기 -> 오른쪽 하단
                    lineTo(size.width, size.height)
                    close()
                }
                drawPath(path, color)
            }
        }

        // 몸통
        Box(
            modifier = Modifier
                .background(
                    color = color,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = tooltipText,
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.white,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ToolTipPopupPreview() {
    NekiTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ToolTipContent(
                tooltipText = "툴팁 메시지입니다",
                color = NekiTheme.colorScheme.gray800,
            )
        }
    }
}

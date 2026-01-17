package com.neki.android.feature.archive.impl.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ui.theme.NekiTheme

private const val OFFSET_X = -35
private const val OFFSET_Y = 47

@Composable
internal fun ToolTip(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val caretColor = NekiTheme.colorScheme.gray800

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(
            initialScale = 0.8f,
            transformOrigin = TransformOrigin(1f, 0f),
        ),
        exit = fadeOut() + scaleOut(
            targetScale = 0.8f,
            transformOrigin = TransformOrigin(1f, 0f),
        ),
    ) {
        Column(
            modifier = modifier.offset(x = OFFSET_X.dp, y = OFFSET_Y.dp),
            horizontalAlignment = Alignment.End,
        ) {
            // 꼬리 (오른쪽 정렬, 오른쪽에서 16dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Canvas(modifier = Modifier.size(width = 10.dp, height = 8.dp)) {
                    val path = Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path, caretColor)
                }
            }

            // 몸통
            Box(
                modifier = Modifier
                    .background(
                        color = NekiTheme.colorScheme.gray800,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "버튼을 눌러 네컷을 추가할 수 있어요",
                    style = NekiTheme.typography.body14Medium,
                    color = NekiTheme.colorScheme.white,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToolTipPreview() {
    NekiTheme {
        Box(modifier = Modifier.padding(20.dp)) {
            ToolTip(visible = true)
        }
    }
}

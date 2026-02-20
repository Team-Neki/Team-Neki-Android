package com.neki.android.core.designsystem.popup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

private const val DEFAULT_ARROW_POSITION = 16

enum class ArrowDirection {
    Up, Down,
}

enum class ToolTipColor {
    Gray800, Gray25
}

@Composable
fun ToolTipPopup(
    tooltipText: String,
    offset: IntOffset,
    arrowDirection: ArrowDirection,
    alignment: Alignment,
    arrowAlignment: Alignment,
    arrowPosition: Dp = DEFAULT_ARROW_POSITION.dp,
    toolTipColor: ToolTipColor = ToolTipColor.Gray800,
    hasCloseButton: Boolean = false,
    properties: PopupProperties = PopupProperties(),
    onDismissRequest: () -> Unit = {},
    onClickCloseButton: () -> Unit = {},
) {
    Popup(
        alignment = alignment,
        offset = offset,
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        ToolTipContent(
            tooltipText = tooltipText,
            toolTipColor = toolTipColor,
            arrowDirection = arrowDirection,
            arrowPosition = arrowPosition,
            arrowAlignment = arrowAlignment,
            hasCloseButton = hasCloseButton,
            onClickCloseButton = onClickCloseButton,
        )
    }
}

@Composable
private fun TriangleArrow(
    direction: ArrowDirection,
    color: Color,
    modifier: Modifier = Modifier,
    width: Dp = 10.dp,
    height: Dp = 8.dp,
) {
    Canvas(
        modifier = modifier.size(width = width, height = height),
    ) {
        val cornerRadius = 1.dp.toPx()
        val path = when (direction) {
            ArrowDirection.Up -> Path().apply {
                moveTo(0f, size.height)
                lineTo(size.width / 2 - cornerRadius, cornerRadius)
                quadraticTo(size.width / 2, 0f, size.width / 2 + cornerRadius, cornerRadius)
                lineTo(size.width, size.height)
                close()
            }

            ArrowDirection.Down -> Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2 - cornerRadius, size.height - cornerRadius)
                quadraticTo(size.width / 2, size.height, size.width / 2 + cornerRadius, size.height - cornerRadius)
                lineTo(size.width, 0f)
                close()
            }
        }
        drawPath(path, color)
    }
}

@Composable
private fun ToolTipContent(
    tooltipText: String,
    arrowDirection: ArrowDirection,
    arrowAlignment: Alignment,
    modifier: Modifier = Modifier,
    toolTipColor: ToolTipColor = ToolTipColor.Gray800,
    arrowPosition: Dp = DEFAULT_ARROW_POSITION.dp,
    hasCloseButton: Boolean = false,
    onClickCloseButton: () -> Unit = {},
) {
    val backgroundColor = when (toolTipColor) {
        ToolTipColor.Gray800 -> NekiTheme.colorScheme.gray800
        ToolTipColor.Gray25 -> NekiTheme.colorScheme.gray25
    }
    val textColor = when (toolTipColor) {
        ToolTipColor.Gray800 -> NekiTheme.colorScheme.white
        ToolTipColor.Gray25 -> NekiTheme.colorScheme.gray900
    }
    val iconTint = when (toolTipColor) {
        ToolTipColor.Gray800 -> NekiTheme.colorScheme.gray25
        ToolTipColor.Gray25 -> NekiTheme.colorScheme.gray500
    }

    Column(
        modifier = modifier.width(IntrinsicSize.Max),
    ) {
        if (arrowDirection == ArrowDirection.Up) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = arrowPosition),
                contentAlignment = arrowAlignment,
            ) { TriangleArrow(direction = arrowDirection, color = backgroundColor) }
        }
        Row(
            modifier = Modifier
                .background(color = backgroundColor, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tooltipText,
                style = NekiTheme.typography.body14Medium,
                color = textColor,
            )
            if (hasCloseButton) {
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .noRippleClickableSingle(onClick = onClickCloseButton),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                    tint = iconTint,
                    contentDescription = null,
                )
            }
        }
        if (arrowDirection == ArrowDirection.Down) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = arrowPosition),
                contentAlignment = arrowAlignment,
            ) { TriangleArrow(direction = arrowDirection, color = backgroundColor) }
        }
    }
}

@ComponentPreview
@Composable
private fun ToolTipArrowUpPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
                hasCloseButton = true,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun ToolTipArrowDownPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
                hasCloseButton = true,
            )
        }
    }
}

@Preview
@Composable
private fun ToolTipArrowUpGrayPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
        }
    }
}

@Preview
@Composable
private fun ToolTipArrowDownGrayPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTipContent(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun TriangleArrowPreview() {
    NekiTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TriangleArrow(direction = ArrowDirection.Up, color = NekiTheme.colorScheme.gray800)
            TriangleArrow(direction = ArrowDirection.Down, color = NekiTheme.colorScheme.gray800)
        }
    }
}

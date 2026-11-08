package com.neki.android.core.designsystem.popup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.sp
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

enum class ToolTipSize {
    Medium, Small
}

private data class ToolTipSizeSpec(
    val cornerRadius: Dp,
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val spacing: Dp,
    val iconSize: Dp,
    val arrowWidth: Dp,
    val arrowHeight: Dp,
)

@Composable
fun ToolTipPopup(
    tooltipText: String,
    offset: IntOffset,
    arrowDirection: ArrowDirection,
    alignment: Alignment,
    arrowAlignment: Alignment,
    arrowPosition: Dp = DEFAULT_ARROW_POSITION.dp,
    toolTipColor: ToolTipColor = ToolTipColor.Gray800,
    toolTipSize: ToolTipSize = ToolTipSize.Medium,
    leadingIcon: ImageVector? = null,
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
        ToolTip(
            tooltipText = tooltipText,
            toolTipColor = toolTipColor,
            toolTipSize = toolTipSize,
            leadingIcon = leadingIcon,
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

// Popup 없이 레이아웃 안에 그리는 툴팁. leadingIcon은 tint 없이 아이콘 고유 색으로 그린다.
@Composable
fun ToolTip(
    tooltipText: String,
    arrowDirection: ArrowDirection,
    arrowAlignment: Alignment,
    modifier: Modifier = Modifier,
    toolTipColor: ToolTipColor = ToolTipColor.Gray800,
    toolTipSize: ToolTipSize = ToolTipSize.Medium,
    leadingIcon: ImageVector? = null,
    arrowPosition: Dp = DEFAULT_ARROW_POSITION.dp,
    hasCloseButton: Boolean = false,
    onClickCloseButton: () -> Unit = {},
) {
    val sizeSpec = when (toolTipSize) {
        ToolTipSize.Medium -> ToolTipSizeSpec(
            cornerRadius = 8.dp,
            horizontalPadding = 12.dp,
            verticalPadding = 8.dp,
            spacing = 2.dp,
            iconSize = 16.dp,
            arrowWidth = 10.dp,
            arrowHeight = 8.dp,
        )

        ToolTipSize.Small -> ToolTipSizeSpec(
            cornerRadius = 6.dp,
            horizontalPadding = 8.dp,
            verticalPadding = 6.dp,
            spacing = 4.dp,
            iconSize = 12.dp,
            arrowWidth = 7.5.dp,
            arrowHeight = 6.dp,
        )
    }
    val textStyle = when (toolTipSize) {
        ToolTipSize.Medium -> NekiTheme.typography.body14Medium
        ToolTipSize.Small -> NekiTheme.typography.caption11Medium.copy(
            fontSize = 10.5.sp,
            lineHeight = 15.sp,
        )
    }
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
            ) {
                TriangleArrow(
                    direction = arrowDirection,
                    color = backgroundColor,
                    width = sizeSpec.arrowWidth,
                    height = sizeSpec.arrowHeight,
                )
            }
        }
        Row(
            modifier = Modifier
                .background(color = backgroundColor, shape = RoundedCornerShape(sizeSpec.cornerRadius))
                .padding(horizontal = sizeSpec.horizontalPadding, vertical = sizeSpec.verticalPadding),
            horizontalArrangement = Arrangement.spacedBy(sizeSpec.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Image(
                    modifier = Modifier.size(sizeSpec.iconSize),
                    imageVector = leadingIcon,
                    contentDescription = null,
                )
            }
            Text(
                text = tooltipText,
                style = textStyle,
                color = textColor,
            )
            if (hasCloseButton) {
                Icon(
                    modifier = Modifier
                        .size(sizeSpec.iconSize)
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
            ) {
                TriangleArrow(
                    direction = arrowDirection,
                    color = backgroundColor,
                    width = sizeSpec.arrowWidth,
                    height = sizeSpec.arrowHeight,
                )
            }
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
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                hasCloseButton = true,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                hasCloseButton = true,
            )
            ToolTip(
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
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                hasCloseButton = true,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                hasCloseButton = true,
            )
            ToolTip(
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
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTip(
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
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
                toolTipColor = ToolTipColor.Gray25,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterStart,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.Center,
                toolTipColor = ToolTipColor.Gray25,
                hasCloseButton = true,
            )
            ToolTip(
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

@ComponentPreview
@Composable
private fun ToolTipSmallPreview() {
    NekiTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ToolTip(
                tooltipText = "최근 로그인",
                arrowDirection = ArrowDirection.Down,
                arrowAlignment = Alignment.CenterEnd,
                arrowPosition = 13.5.dp,
                toolTipSize = ToolTipSize.Small,
                leadingIcon = ImageVector.vectorResource(R.drawable.icon_clock),
            )
            ToolTip(
                tooltipText = "텍스트",
                arrowDirection = ArrowDirection.Up,
                arrowAlignment = Alignment.Center,
                toolTipSize = ToolTipSize.Small,
            )
        }
    }
}

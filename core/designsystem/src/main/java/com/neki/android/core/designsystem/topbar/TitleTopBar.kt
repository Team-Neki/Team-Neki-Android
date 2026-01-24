package com.neki.android.core.designsystem.topbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.button.NekiTextButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun CloseTitleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onClose,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
    )
}

@Composable
fun CloseTitleTextButtonTopBar(
    title: String,
    buttonText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledTextColor: Color = NekiTheme.colorScheme.primary500,
    disabledTextColor: Color = NekiTheme.colorScheme.gray200,
    onClose: () -> Unit = {},
    onClickTextButton: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onClose,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
        actions = { modifier ->
            NekiTextButton(
                modifier = modifier.fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                onClick = onTextButtonClick,
                enabled = enabled,
            ) {
                Text(
                    text = buttonText,
                    style = NekiTheme.typography.body16SemiBold,
                    color = if (enabled) enabledTextColor else disabledTextColor,
                )
            }
        },
    )
}

@Composable
fun BackTitleTextButtonTopBar(
    title: String,
    buttonLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledTextColor: Color = NekiTheme.colorScheme.primary500,
    disabledTextColor: Color = NekiTheme.colorScheme.gray200,
    onBack: () -> Unit = {},
    onClickTextButton: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
        actions = { modifier ->
            NekiTextButton(
                modifier = modifier.fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                onClick = onTextButtonClick,
                enabled = enabled,
            ) {
                Text(
                    text = buttonLabel,
                    style = NekiTheme.typography.body16SemiBold,
                    color = if (enabled) enabledTextColor else disabledTextColor,
                )
            }
        },
    )
}

@Composable
fun BackTitleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
    )
}

@Composable
fun BackTitleOptionTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onClickIcon: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
        actions = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(end = 8.dp)
                    .size(52.dp),
                onClick = onIconClick,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_kebab),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
fun BackTitleTextButtonOptionTopBar(
    title: String,
    buttonLabel: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledTextColor: Color = NekiTheme.colorScheme.primary500,
    disabledTextColor: Color = NekiTheme.colorScheme.gray200,
    optionIconRes: Int = R.drawable.icon_option,
    onBack: () -> Unit = {},
    onClickTextButton: () -> Unit = {},
    onClickIcon: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            NekiIconButton(
                modifier = modifier
                    .padding(start = 8.dp)
                    .size(52.dp),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
            }
        },
        title = title,
        actions = { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NekiTextButton(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(vertical = 3.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = onTextButtonClick,
                    enabled = enabled,
                ) {
                    Text(
                        text = buttonLabel,
                        style = NekiTheme.typography.body16SemiBold,
                        color = if (enabled) enabledTextColor else disabledTextColor,
                    )
                }
                NekiIconButton(
                    modifier = modifier
                        .padding(end = 8.dp)
                        .size(52.dp),
                    onClick = onIconClick,
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        imageVector = ImageVector.vectorResource(optionIconRes),
                        tint = NekiTheme.colorScheme.gray800,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}

@ComponentPreview
@Composable
private fun NekiTitleTopBarPreview() {
    NekiTheme {
        NekiTitleTopBar(
            title = "텍스트",
        )
    }
}

@ComponentPreview
@Composable
private fun CloseTitleTopBarPreview() {
    NekiTheme {
        CloseTitleTopBar(
            title = "텍스트",
        )
    }
}

@ComponentPreview
@Composable
private fun CloseTitleTextButtonTopBarPreview() {
    NekiTheme {
        CloseTitleTextButtonTopBar(
            title = "텍스트",
            buttonText = "텍스트버튼",
        )
    }
}

@ComponentPreview
@Composable
private fun BackTitleTextButtonTopBarPreview() {
    NekiTheme {
        BackTitleTextButtonTopBar(
            title = "텍스트",
            buttonLabel = "텍스트버튼",
        )
    }
}

@ComponentPreview
@Composable
private fun BackTitleTopBarPreview() {
    NekiTheme {
        BackTitleTopBar(
            title = "텍스트",
        )
    }
}

@ComponentPreview
@Composable
private fun BackTitleOptionTopBarPreview() {
    NekiTheme {
        BackTitleOptionTopBar(
            title = "텍스트",
        )
    }
}

@ComponentPreview
@Composable
private fun BackTitleTwoOptionTopBarPreview() {
    NekiTheme {
        BackTitleTextButtonOptionTopBar(
            title = "텍스트",
            buttonLabel = "텍스트버튼",
        )
    }
}

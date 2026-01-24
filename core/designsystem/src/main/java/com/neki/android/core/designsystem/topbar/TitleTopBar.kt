package com.neki.android.core.designsystem.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.TopBarTextButton
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
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
            Icon(
                modifier = modifier
                    .size(24.dp)
                    .noRippleClickableSingle { onClose() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
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
            Icon(
                modifier = modifier
                    .size(24.dp)
                    .noRippleClickableSingle { onClose() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
        },
        title = title,
        actions = { modifier ->
            TopBarTextButton(
                buttonText = buttonText,
                modifier = modifier,
                onClick = onClickTextButton,
                enabled = enabled,
                enabledTextColor = enabledTextColor,
                disabledTextColor = disabledTextColor,
            )
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
            Icon(
                modifier = modifier.noRippleClickableSingle { onBack() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
        },
        title = title,
        actions = { modifier ->
            TopBarTextButton(
                buttonText = buttonLabel,
                modifier = modifier,
                onClick = onClickTextButton,
                enabled = enabled,
                enabledTextColor = enabledTextColor,
                disabledTextColor = disabledTextColor,
            )
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
            Icon(
                modifier = modifier.noRippleClickableSingle { onBack() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
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
            Icon(
                modifier = modifier.noRippleClickableSingle { onBack() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
        },
        title = title,
        actions = { modifier ->
            Icon(
                modifier = modifier.noRippleClickableSingle { onClickIcon() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_kebab),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
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
    optionIconRes: Int = R.drawable.icon_kebab,
    onBack: () -> Unit = {},
    onClickTextButton: () -> Unit = {},
    onClickIcon: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            Icon(
                modifier = modifier.noRippleClickableSingle { onBack() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
        },
        title = title,
        actions = { modifier ->
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TopBarTextButton(
                    buttonText = buttonLabel,
                    onClick = onClickTextButton,
                    enabled = enabled,
                    enabledTextColor = enabledTextColor,
                    disabledTextColor = disabledTextColor,
                )
                Icon(
                    modifier = Modifier.noRippleClickableSingle { onClickIcon() },
                    imageVector = ImageVector.vectorResource(optionIconRes),
                    tint = NekiTheme.colorScheme.gray800,
                    contentDescription = null,
                )
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

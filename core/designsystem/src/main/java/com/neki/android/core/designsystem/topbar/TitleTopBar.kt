package com.neki.android.core.designsystem.topbar

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
                modifier = modifier.noRippleClickableSingle { onClose() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_close_24),
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
    onClose: () -> Unit = {},
    onTextButtonClick: () -> Unit = {},
) {
    NekiTitleTopBar(
        modifier = modifier,
        leadingIcon = { modifier ->
            Icon(
                modifier = modifier.noRippleClickableSingle { onClose() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_close_24),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
        },
        title = title,
        actions = { modifier ->
            TopBarTextButton(
                buttonText = buttonText,
                modifier = modifier,
                onClick = onTextButtonClick,
            )
        },
    )
}

@Composable
fun BackTitleTextButtonTopBar(
    title: String,
    buttonLabel: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onTextButtonClick: () -> Unit = {},
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
                onClick = onTextButtonClick,
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
    onIconClick: () -> Unit = {},
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
                modifier = modifier.noRippleClickableSingle { onIconClick() },
                imageVector = ImageVector.vectorResource(R.drawable.icon_kebab),
                tint = NekiTheme.colorScheme.gray800,
                contentDescription = null,
            )
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

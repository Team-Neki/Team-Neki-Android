package com.neki.android.core.designsystem.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun CTAButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    disabledContainerColor: Color,
    disabledContentColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    NekiButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 14.dp),
    ) {
        Text(
            text = text,
            style = NekiTheme.typography.body16SemiBold,
        )
    }
}

@Composable
fun CTAButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    CTAButton(
        modifier = modifier,
        text = text,
        onClick = onClick,
        containerColor = NekiTheme.colorScheme.primary400,
        contentColor = NekiTheme.colorScheme.white,
        disabledContainerColor = NekiTheme.colorScheme.primary400.copy(alpha = 0.4f),
        disabledContentColor = NekiTheme.colorScheme.white,
        enabled = enabled,
    )
}

@Composable
fun CTAButtonGray(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    CTAButton(
        modifier = modifier,
        text = text,
        onClick = onClick,
        containerColor = NekiTheme.colorScheme.gray50,
        contentColor = NekiTheme.colorScheme.gray300,
        disabledContainerColor = NekiTheme.colorScheme.gray50,
        disabledContentColor = NekiTheme.colorScheme.gray300,
        enabled = enabled,
    )
}

@ComponentPreview
@Composable
private fun CTAButtonPrimaryPreview() {
    NekiTheme {
        CTAButtonPrimary(
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp),
            text = "텍스트",
            onClick = {},
        )
    }
}

@ComponentPreview
@Composable
private fun CTAButtonPrimaryDisabledPreview() {
    NekiTheme {
        CTAButtonPrimary(
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp),
            text = "텍스트",
            onClick = {},
            enabled = false,
        )
    }
}

@ComponentPreview
@Composable
private fun CTAButtonGrayPreview() {
    NekiTheme {
        CTAButtonGray(
            modifier = Modifier
                .width(200.dp)
                .padding(8.dp),
            text = "텍스트",
            onClick = {},
        )
    }
}

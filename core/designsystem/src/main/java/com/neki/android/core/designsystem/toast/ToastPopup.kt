package com.neki.android.core.designsystem.toast

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.neki.android.core.designsystem.button.ToastPopupActionButton
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun ToastPopup(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    actionButton: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = NekiTheme.colorScheme.gray800,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(iconRes),
                tint = Color.Unspecified,
                contentDescription = null,
            )
            Text(
                text = text,
                style = NekiTheme.typography.body16Medium,
                color = NekiTheme.colorScheme.gray25,
            )
        }
        actionButton?.invoke()
    }
}

@Composable
fun ToastActionPopup(
    @DrawableRes iconRes: Int,
    text: String,
    modifier: Modifier = Modifier,
    buttonText: String,
    onClickActionButton: () -> Unit,
) {
    ToastPopup(
        iconRes = iconRes,
        text = text,
        actionButton = {
            ToastPopupActionButton(
                buttonText = buttonText,
                onClick = onClickActionButton,
            )
        },
    )
}

@ComponentPreview
@Composable
private fun ToastPopupPreview() {
    NekiTheme {
        ToastPopup(
            iconRes = R.drawable.icon_checkbox_on_24,
            text = "텍스트",
        )
    }
}

@ComponentPreview
@Composable
private fun ToastPopupWithActionPreview() {
    NekiTheme {
        ToastActionPopup(
            iconRes = R.drawable.icon_checkbox_on_24,
            text = "텍스트",
            buttonText = "텍스트",
            onClickActionButton = {},
        )
    }
}

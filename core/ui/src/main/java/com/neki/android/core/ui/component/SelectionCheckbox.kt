package com.neki.android.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun SelectionCheckbox(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    selectedColor: Color = NekiTheme.colorScheme.primary400,
    unselectedColor: Color = NekiTheme.colorScheme.white,
    borderColor: Color = NekiTheme.colorScheme.gray100,
) {
    val iconSize = size * 2 / 3

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (isSelected) {
                    Modifier.background(
                        color = selectedColor,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                        .background(
                            color = unselectedColor,
                            shape = CircleShape,
                        )
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = CircleShape,
                        )
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = ImageVector.vectorResource(R.drawable.icon_check_white),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SelectionCheckboxUnselectedPreview() {
    NekiTheme {
        SelectionCheckbox(isSelected = false)
    }
}

@ComponentPreview
@Composable
private fun SelectionCheckboxSelectedPreview() {
    NekiTheme {
        SelectionCheckbox(isSelected = true)
    }
}

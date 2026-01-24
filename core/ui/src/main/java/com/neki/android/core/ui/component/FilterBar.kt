package com.neki.android.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun FilterBar(
    isDownIconChipSelected: Boolean,
    isDefaultChipSelected: Boolean,
    downIconChipDisplayText: String,
    defaultChipDisplayText: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onDownIconChipClick: () -> Unit = {},
    onDefaultChipClick: () -> Unit = {},
) {
    AnimatedVisibility(
        modifier = modifier
            .background(NekiTheme.colorScheme.white)
            .fillMaxWidth(),
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DownIconFilterChip(
                isSelected = isDownIconChipSelected,
                displayText = downIconChipDisplayText,
                onClick = onDownIconChipClick,
            )
            DefaultFilterChip(
                isSelected = isDefaultChipSelected,
                displayText = defaultChipDisplayText,
                onClick = onDefaultChipClick,
            )
        }
    }
}

@Composable
private fun DownIconFilterChip(
    isSelected: Boolean,
    displayText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(
                shape = CircleShape,
                color = if (isSelected) NekiTheme.colorScheme.gray800
                else NekiTheme.colorScheme.gray50,
            )
            .padding(vertical = 7.dp, horizontal = 12.dp)
            .noRippleClickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = displayText,
            style = NekiTheme.typography.body14Medium,
            color = if (isSelected) NekiTheme.colorScheme.white
            else NekiTheme.colorScheme.gray700,
        )
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_down),
            contentDescription = null,
            tint = NekiTheme.colorScheme.gray400,
        )
    }
}

@Composable
private fun DefaultFilterChip(
    isSelected: Boolean,
    displayText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Text(
        modifier = modifier
            .background(
                shape = CircleShape,
                color = if (isSelected) NekiTheme.colorScheme.gray800
                else NekiTheme.colorScheme.gray50,
            )
            .padding(vertical = 7.dp, horizontal = 12.dp)
            .noRippleClickable(onClick = onClick),
        text = displayText,
        style = NekiTheme.typography.body14Medium,
        color = if (isSelected) NekiTheme.colorScheme.white
        else NekiTheme.colorScheme.gray700,
    )
}

@ComponentPreview
@Composable
private fun FilterBarDefaultPreview() {
    NekiTheme {
        FilterBar(
            isDownIconChipSelected = false,
            isDefaultChipSelected = false,
            downIconChipDisplayText = "인원수",
            defaultChipDisplayText = "스크랩",
        )
    }
}

@ComponentPreview
@Composable
private fun FilterBarSelectedPreview() {
    NekiTheme {
        FilterBar(
            isDownIconChipSelected = true,
            isDefaultChipSelected = true,
            downIconChipDisplayText = "2인",
            defaultChipDisplayText = "스크랩",
        )
    }
}

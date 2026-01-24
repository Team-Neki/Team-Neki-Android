package com.neki.android.feature.pose.impl.component

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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.pose.impl.NumberOfPeople

@Composable
internal fun FilterBar(
    numberOfPeople: NumberOfPeople,
    isScrapSelected: Boolean,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClickNumberOfPeople: () -> Unit = {},
    onClickScrap: () -> Unit = {},
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
            NumberOfPeopleFilter(
                numberOfPeople = numberOfPeople,
                onClick = onClickNumberOfPeople,
            )
            ScrapFilter(
                isSelected = isScrapSelected,
                onClick = onClickScrap,
            )
        }
    }
}

@Composable
private fun NumberOfPeopleFilter(
    modifier: Modifier = Modifier,
    numberOfPeople: NumberOfPeople = NumberOfPeople.UNSELECTED,
    onClick: () -> Unit = {},
) {
    val isSelected by remember(numberOfPeople) { derivedStateOf { numberOfPeople != NumberOfPeople.UNSELECTED } }
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
            text = numberOfPeople.displayText,
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
private fun ScrapFilter(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
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
        text = "스크랩",
        style = NekiTheme.typography.body14Medium,
        color = if (isSelected) NekiTheme.colorScheme.white
        else NekiTheme.colorScheme.gray700,
    )
}

@ComponentPreview
@Composable
private fun FilterBarPreview() {
    NekiTheme {
        FilterBar(
            numberOfPeople = NumberOfPeople.UNSELECTED,
            isScrapSelected = false,
        )
    }
}

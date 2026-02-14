package com.neki.android.feature.pose.impl.main.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.ui.component.FilterBar

@Composable
internal fun PoseFilterBar(
    peopleCount: PeopleCount?,
    isScrapSelected: Boolean,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClickPeopleCount: () -> Unit = {},
    onClickScrap: () -> Unit = {},
) {
    FilterBar(
        isDownIconChipSelected = peopleCount != null,
        isDefaultChipSelected = isScrapSelected,
        downIconChipDisplayText = peopleCount?.displayText ?: "인원수",
        defaultChipDisplayText = "스크랩",
        modifier = modifier,
        visible = visible,
        onClickDownIconChip = onClickPeopleCount,
        onClickDefaultChip = onClickScrap,
    )
}

@ComponentPreview
@Composable
private fun PoseFilterBarPreview() {
    NekiTheme {
        PoseFilterBar(
            peopleCount = null,
            isScrapSelected = false,
        )
    }
}

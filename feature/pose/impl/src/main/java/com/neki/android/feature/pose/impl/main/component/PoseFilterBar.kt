package com.neki.android.feature.pose.impl.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PeopleCount
import com.neki.android.core.ui.component.DefaultFilterChip
import com.neki.android.core.ui.component.DownIconFilterChip
import com.neki.android.feature.pose.impl.main.PoseRelationTag
import com.neki.android.feature.pose.impl.main.isRelationTagVisible

@Composable
internal fun PoseFilterBar(
    peopleCount: PeopleCount?,
    relationTag: PoseRelationTag?,
    isBookmarkSelected: Boolean,
    totalPoseCount: Int?,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    onClickPeopleCount: () -> Unit = {},
    onClickBookmark: () -> Unit = {},
    onClickRelationTag: (PoseRelationTag) -> Unit = {},
) {
    AnimatedVisibility(
        modifier = modifier
            .background(NekiTheme.colorScheme.white)
            .fillMaxWidth(),
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PoseTagChips(
                peopleCount = peopleCount,
                relationTag = relationTag,
                isBookmarkSelected = isBookmarkSelected,
                onClickPeopleCount = onClickPeopleCount,
                onClickBookmark = onClickBookmark,
                onClickRelationTag = onClickRelationTag,
            )
            PoseCountSortRow(totalPoseCount = totalPoseCount)
        }
    }
}

@Composable
private fun PoseTagChips(
    peopleCount: PeopleCount?,
    relationTag: PoseRelationTag?,
    isBookmarkSelected: Boolean,
    modifier: Modifier = Modifier,
    onClickPeopleCount: () -> Unit = {},
    onClickBookmark: () -> Unit = {},
    onClickRelationTag: (PoseRelationTag) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp, horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DefaultFilterChip(
            isSelected = isBookmarkSelected,
            displayText = "북마크",
            onClick = onClickBookmark,
        )
        VerticalDivider(
            modifier = Modifier.height(24.dp),
            thickness = 0.5.dp,
            color = NekiTheme.colorScheme.gray75,
        )
        DownIconFilterChip(
            isSelected = peopleCount != null,
            displayText = peopleCount?.displayText ?: "인원수",
            contentPadding = PaddingValues(start = 12.dp, end = 8.dp, top = 7.dp, bottom = 7.dp),
            onClick = onClickPeopleCount,
        )
        if (peopleCount.isRelationTagVisible()) {
            PoseRelationTag.entries.forEach { tag ->
                DefaultFilterChip(
                    isSelected = tag == relationTag,
                    displayText = tag.displayText,
                    onClick = { onClickRelationTag(tag) },
                )
            }
        }
    }
}

@Composable
private fun PoseCountSortRow(
    totalPoseCount: Int?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (totalPoseCount != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontWeight = NekiTheme.typography.body14SemiBold.fontWeight,
                            color = NekiTheme.colorScheme.gray600,
                        ),
                    ) {
                        append("$totalPoseCount")
                    }
                    append("개의 포즈")
                },
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray400,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "최신순",
            style = NekiTheme.typography.body14SemiBold,
            color = NekiTheme.colorScheme.gray600,
            textAlign = TextAlign.End,
        )
    }
}

@ComponentPreview
@Composable
private fun PoseFilterBarPreview() {
    NekiTheme {
        PoseFilterBar(
            peopleCount = null,
            relationTag = null,
            isBookmarkSelected = false,
            totalPoseCount = 98,
        )
    }
}

@ComponentPreview
@Composable
private fun PoseFilterBarRelationSelectedPreview() {
    NekiTheme {
        PoseFilterBar(
            peopleCount = PeopleCount.TWO,
            relationTag = PoseRelationTag.FRIEND,
            isBookmarkSelected = false,
            totalPoseCount = 20,
        )
    }
}

@ComponentPreview
@Composable
private fun PoseFilterBarRelationHiddenPreview() {
    NekiTheme {
        PoseFilterBar(
            peopleCount = PeopleCount.THREE,
            relationTag = null,
            isBookmarkSelected = false,
            totalPoseCount = 20,
        )
    }
}

@ComponentPreview
@Composable
private fun PoseFilterBarWithoutCountPreview() {
    NekiTheme {
        PoseFilterBar(
            peopleCount = null,
            relationTag = null,
            isBookmarkSelected = true,
            totalPoseCount = null,
        )
    }
}

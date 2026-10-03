package com.neki.android.feature.map.impl.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.search.SearchResult
import com.neki.android.feature.map.impl.search.SearchResultType
import com.neki.android.feature.map.impl.util.formatDistance

@Composable
internal fun SearchPlaceItem(
    result: SearchResult,
    distanceMeters: Int?,
    query: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val isPhotoBooth = result.type == SearchResultType.PHOTO_BOOTH
    val icon = when (result.type) {
        SearchResultType.REGION -> R.drawable.icon_map_filled
        SearchResultType.STATION -> R.drawable.icon_subway
        SearchResultType.PHOTO_BOOTH -> R.drawable.icon_camera_filled
    }
    val name = remember(result.keyword, query) {
        buildAnnotatedString {
            append(result.keyword)
            query
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .forEach { term ->
                    var start = result.keyword.indexOf(term, ignoreCase = true)
                    while (start >= 0) {
                        addStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold),
                            start = start,
                            end = start + term.length,
                        )
                        start = result.keyword.indexOf(
                            string = term,
                            startIndex = start + term.length,
                            ignoreCase = true,
                        )
                    }
                }
        }
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isPhotoBooth) NekiTheme.colorScheme.primary50 else NekiTheme.colorScheme.gray50,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
                tint = if (isPhotoBooth) NekiTheme.colorScheme.primary300 else NekiTheme.colorScheme.gray200,
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = name,
            style = NekiTheme.typography.body16Medium,
            color = NekiTheme.colorScheme.gray900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (distanceMeters != null) {
            Text(
                text = distanceMeters.formatDistance(),
                style = NekiTheme.typography.body14Regular,
                color = NekiTheme.colorScheme.gray500,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun SearchPlaceItemPreview() {
    NekiTheme {
        SearchPlaceItem(
            result = SearchResult(
                keyword = "강남역 2호선",
                type = SearchResultType.STATION,
            ),
            distanceMeters = 16000,
            query = "강남",
        )
    }
}

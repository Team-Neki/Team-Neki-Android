package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.NekiIconButton
import com.neki.android.core.designsystem.modifier.buttonShadow
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun MapSearchBar(
    modifier: Modifier = Modifier,
    query: String? = null,
    onClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onClear: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .buttonShadow(
                shape = CircleShape,
                blurRadius = if (query == null) 8.dp else 4.dp,
            )
            .clip(CircleShape)
            .clickableSingle(onClick = onClick)
            .background(
                color = NekiTheme.colorScheme.white,
                shape = CircleShape,
            )
            .padding(horizontal = if (query == null) 16.dp else 4.dp, vertical = if (query == null) 12.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(if (query == null) 12.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (query == null) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_neki),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        } else {
            NekiIconButton(
                modifier = Modifier.size(48.dp),
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_left),
                    contentDescription = "검색으로 돌아가기",
                    tint = Color.Unspecified,
                )
            }
        }
        Text(
            modifier = Modifier.weight(1f),
            text = query ?: "포토 부스 검색하기",
            color = if (query == null) NekiTheme.colorScheme.gray800 else NekiTheme.colorScheme.gray900,
            style = if (query == null) NekiTheme.typography.body16Medium else NekiTheme.typography.body16SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            modifier = Modifier
                .padding(start = if (query == null) 0.dp else 12.dp)
                .size(24.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_search),
            contentDescription = "검색",
            tint = Color.Unspecified,
        )
        if (query != null) {
            NekiIconButton(
                modifier = Modifier.size(48.dp),
                onClick = onClear,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.icon_close),
                    contentDescription = "지역 검색 닫기",
                    tint = NekiTheme.colorScheme.gray800,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun MapSearchBarPreview() {
    NekiTheme {
        MapSearchBar()
    }
}

@ComponentPreview
@Composable
private fun MapSearchBarRegionPreview() {
    NekiTheme {
        MapSearchBar(query = "서울특별시 강남구")
    }
}

package com.neki.android.feature.map.impl.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.MapTab

@Composable
internal fun PhotoBoothListToggle(
    selectedTab: MapTab,
    onTabSelected: (MapTab) -> Unit,
) {
    val thumbFraction by animateFloatAsState(
        targetValue = if (selectedTab == MapTab.NEARBY) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "tabThumbFraction",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(NekiTheme.colorScheme.gray50)
            .drawBehind {
                val margin = 4.dp.toPx()
                val thumbW = size.width / 2 - margin * 2
                val thumbH = size.height - margin * 2
                val thumbX = margin + thumbFraction * (size.width / 2)
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(x = thumbX, y = margin),
                    size = Size(width = thumbW, height = thumbH),
                    cornerRadius = CornerRadius(5.dp.toPx()),
                )
            },
    ) {
        MapTab.entries.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .noRippleClickable { onTabSelected(tab) }
                    .padding(all = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = ImageVector.vectorResource(
                            when (tab) {
                                MapTab.NEARBY -> if (isSelected) R.drawable.icon_tabbar_pin_on else R.drawable.icon_tabbar_pin_off
                                MapTab.FAVORITE -> if (isSelected) R.drawable.icon_tabbar_favorite_on else R.drawable.icon_tabbar_favorite_off
                            },
                        ),
                        contentDescription = null,
                        tint = Color.Unspecified,
                    )
                    Text(
                        text = if (tab == MapTab.NEARBY) "가까운 포토부스" else "저장한 포토부스",
                        color = if (isSelected) NekiTheme.colorScheme.gray800 else NekiTheme.colorScheme.gray500,
                        style = if (isSelected) NekiTheme.typography.body14SemiBold else NekiTheme.typography.body14Medium,
                    )
                }
            }
        }
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothListToggleNearbyPreview() {
    NekiTheme {
        PhotoBoothListToggle(
            selectedTab = MapTab.NEARBY,
            onTabSelected = {},
        )
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothListToggleFavoritePreview() {
    NekiTheme {
        PhotoBoothListToggle(
            selectedTab = MapTab.FAVORITE,
            onTabSelected = {},
        )
    }
}

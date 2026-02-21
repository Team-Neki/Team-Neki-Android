package com.neki.android.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.neki.android.app.R
import com.neki.android.app.navigation.TopLevelNavItem
import com.neki.android.core.designsystem.modifier.clickableSingle
import com.neki.android.core.designsystem.modifier.tabbarShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.designsystem.R as DesignR

@Composable
fun BottomNavigationBar(
    currentKey: NavKey,
    currentTab: NavKey,
    tabs: List<TopLevelNavItem> = TopLevelNavItem.entries,
    onTabSelected: (TopLevelNavItem) -> Unit,
    onClickFab: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .tabbarShadow(),
            color = NekiTheme.colorScheme.white,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 13.5.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                tabs.take(2).forEach { tab ->
                    BottomNavigationBarItem(
                        modifier = Modifier.weight(1f),
                        selected = tab.navKey == currentTab,
                        tab = tab,
                        onClick = { if (tab.navKey != currentKey) onTabSelected(tab) },
                    )
                }

                // 중앙 FAB 라벨 영역
                FabLabel(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                )

                tabs.drop(2).forEach { tab ->
                    BottomNavigationBarItem(
                        modifier = Modifier.weight(1f),
                        selected = tab.navKey == currentTab,
                        tab = tab,
                        onClick = { if (tab.navKey != currentKey) onTabSelected(tab) },
                    )
                }
            }
        }

        BottomNavigationFab(
            modifier = Modifier
                .offset(y = (-22).dp),
            onClick = onClickFab,
        )
    }
}

@Composable
fun BottomNavigationBarItem(
    selected: Boolean,
    tab: TopLevelNavItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val icon = if (selected) tab.selectedIconRes else tab.unselectedIconRes
    val iconColor = if (selected) NekiTheme.colorScheme.gray800 else NekiTheme.colorScheme.gray200
    val textColor = if (selected) NekiTheme.colorScheme.gray800 else NekiTheme.colorScheme.gray500
    val textStyle = if (selected) NekiTheme.typography.caption12SemiBold else NekiTheme.typography.caption12Medium

    Surface(
        modifier = modifier,
        onClick = onClick,
        color = NekiTheme.colorScheme.white,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = stringResource(tab.iconStringRes),
                tint = iconColor,
            )
            Text(
                text = stringResource(tab.iconStringRes),
                color = textColor,
                style = textStyle,
            )
        }
    }
}

@Composable
private fun FabLabel(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.nav_fab_add_photo),
        color = NekiTheme.colorScheme.gray500,
        style = NekiTheme.typography.caption12Medium,
    )
}

@Composable
private fun BottomNavigationFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val gradientStartColor = if (isPressed) NekiTheme.colorScheme.primary400 else NekiTheme.colorScheme.primary300
    val gradientEndColor = if (isPressed) NekiTheme.colorScheme.primary600 else NekiTheme.colorScheme.primary500

    Box(
        modifier = modifier
            .size(44.dp)
            .background(
                shape = CircleShape,
                color = NekiTheme.colorScheme.white,
            )
            .padding(3.dp)
            .background(
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        0.1816f to gradientStartColor,
                        0.8318f to gradientEndColor,
                    ),
                    start = Offset(Float.POSITIVE_INFINITY, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY),
                ),
                shape = CircleShape,
            )
            .clip(CircleShape)
            .clickableSingle(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = ImageVector.vectorResource(DesignR.drawable.icon_plus),
            contentDescription = stringResource(R.string.nav_fab_add_photo),
            tint = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    var currentTab by remember { mutableStateOf(TopLevelNavItem.ARCHIVE) }
    NekiTheme {
        Box(
            modifier = Modifier.padding(top = 40.dp),
        ) {
            BottomNavigationBar(
                tabs = TopLevelNavItem.entries,
                currentTab = currentTab.navKey,
                currentKey = currentTab.navKey,
                onTabSelected = { currentTab = it },
                onClickFab = {},
            )
        }
    }
}

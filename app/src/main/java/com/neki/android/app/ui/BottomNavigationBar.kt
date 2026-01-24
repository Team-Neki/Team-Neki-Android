package com.neki.android.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.neki.android.app.navigation.TopLevelNavItem
import com.neki.android.core.designsystem.extension.tabbarShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
fun BottomNavigationBar(
    visible: Boolean,
    currentKey: NavKey,
    currentTab: NavKey,
    tabs: List<TopLevelNavItem> = TopLevelNavItem.entries,
    onTabSelected: (TopLevelNavItem) -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
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
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                tabs.forEach { tab ->
                    BottomNavigationBarItem(
                        modifier = Modifier.weight(1f),
                        selected = tab.navKey == currentTab,
                        tab = tab,
                        onClick = { if (tab.navKey != currentKey) onTabSelected(tab) },
                    )
                }
            }
        }
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

    Surface(
        modifier = modifier,
        onClick = onClick,
        color = NekiTheme.colorScheme.white,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = stringResource(tab.iconStringRes),
                tint = iconColor,
            )
            Text(
                text = stringResource(tab.iconStringRes),
                color = textColor,
                style = NekiTheme.typography.caption11SemiBold,
            )
        }
    }
}

@Preview
@Composable
private fun BottomNavigationBarPreview() {
    var currentTab by remember { mutableStateOf(TopLevelNavItem.ARCHIVE) }
    NekiTheme {
        BottomNavigationBar(
            visible = true,
            tabs = TopLevelNavItem.entries,
            currentTab = currentTab.navKey,
            currentKey = currentTab.navKey,
        ) { currentTab = it }
    }
}

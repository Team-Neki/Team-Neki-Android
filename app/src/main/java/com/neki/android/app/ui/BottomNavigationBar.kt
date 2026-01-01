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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.neki.android.app.navigation.TopLevelNavItem
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
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(2.5.dp),
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
    val icon = if (selected) tab.selectedIcon else tab.unselectedIcon
    val color = if (selected) Color(0xFF3C3E48) else Color(0xFFB7B9C3)

    Surface(
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = stringResource(tab.iconTextId),
                tint = color,
            )
            Text(
                text = stringResource(tab.iconTextId),
                color = color,
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

package com.neki.android.app

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.neki.android.app.ui.BottomNavigationBar
import com.neki.android.feature.map.api.MapNavKey

@Composable
fun MainScreen(
    currentKey: NavKey,
    currentTopLevelKey: NavKey,
    topLevelKeys: Set<NavKey>,
    entries: SnapshotStateList<NavEntry<NavKey>>,
    onTabSelected: (NavKey) -> Unit,
    onBack: () -> Unit,
) {
    val shouldShowBottomBar by remember(currentKey) {
        mutableStateOf(currentKey in topLevelKeys)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        bottomBar = {
            BottomNavigationBar(
                visible = shouldShowBottomBar,
                currentTab = currentTopLevelKey,
                currentKey = currentKey,
                onTabSelected = { onTabSelected(it.navKey) },
            )
        },
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(
                if (currentKey == MapNavKey.Map) PaddingValues(bottom = innerPadding.calculateBottomPadding()) else innerPadding,
            ),
            entries = entries,
            onBack = onBack,
        )
    }
}

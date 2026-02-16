package com.neki.android.core.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.neki.android.core.navigation.rememberHiltSharedViewModelStoreNavEntryDecorator
import javax.inject.Inject

class MainNavigationState @Inject constructor(
    val startKey: NavKey,
    val topLevelKeys: Set<NavKey>,
) {
    val topLevelStack: SnapshotStateList<NavKey> = mutableStateListOf(startKey)
    val subStacks = topLevelKeys.associateWith { key -> mutableStateListOf(key) }
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    val currentSubStack: SnapshotStateList<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist")

    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }
}

@Composable
fun MainNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val sharedViewModelStoreNavEntryDecorator = rememberHiltSharedViewModelStoreNavEntryDecorator<NavKey>()
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>(),
            sharedViewModelStoreNavEntryDecorator,
        )
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            sharedViewModelStoreNavEntryDecorator.clearAll()
        }
    }

    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}

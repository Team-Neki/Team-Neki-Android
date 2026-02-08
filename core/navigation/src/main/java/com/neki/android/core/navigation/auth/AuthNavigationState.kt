package com.neki.android.core.navigation.auth

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

class AuthNavigationState @Inject constructor(
    val startKey: NavKey,
) {
    val stack: SnapshotStateList<NavKey> = mutableStateListOf(startKey)
    val currentKey: NavKey by derivedStateOf { stack.last() }
}

@Composable
fun AuthNavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    val sharedViewModelStoreNavEntryDecorator = rememberHiltSharedViewModelStoreNavEntryDecorator<NavKey>()
    val decorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
        rememberViewModelStoreNavEntryDecorator<NavKey>(),
        sharedViewModelStoreNavEntryDecorator,
    )

    DisposableEffect(Unit) {
        onDispose {
            sharedViewModelStoreNavEntryDecorator.clearAll()
        }
    }

    return rememberDecoratedNavEntries(
        backStack = stack,
        entryDecorators = decorators,
        entryProvider = entryProvider,
    ).toMutableStateList()
}

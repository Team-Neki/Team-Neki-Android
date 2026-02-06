package com.neki.android.core.navigation.root

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import javax.inject.Inject

class RootNavigationState @Inject constructor(
    val startKey: RootNavKey,
) {
    internal val stack: SnapshotStateList<RootNavKey> = mutableStateListOf(startKey)
    val currentRootKey: RootNavKey by derivedStateOf { stack.last() }
}

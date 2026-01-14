package com.neki.android.core.navigation.root

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class RootNavigationState @Inject constructor() {
    internal val stack: SnapshotStateList<RootNavKey> = mutableStateListOf(RootNavKey.Login)
    val currentRootKey: RootNavKey by derivedStateOf { stack.last() }
}

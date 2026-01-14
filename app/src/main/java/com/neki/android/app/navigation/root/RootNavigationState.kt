package com.neki.android.app.navigation.root

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jakarta.inject.Inject

@ActivityRetainedScoped
class RootNavigationState @Inject constructor() {
    private val stack: SnapshotStateList<RootNavKey> = mutableStateListOf(RootNavKey.Login)
    val currentRootKey: RootNavKey by derivedStateOf { stack.last() }

    fun navigateRoot(rootNavKey: RootNavKey) {
        stack.clear()
        stack.add(rootNavKey)
    }
}

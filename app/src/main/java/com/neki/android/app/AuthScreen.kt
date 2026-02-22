package com.neki.android.app

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import com.neki.android.feature.auth.api.AuthNavKey

private const val SLIDE_ANIMATION_DURATION = 800

@Composable
fun AuthScreen(
    currentKey: NavKey,
    entries: SnapshotStateList<NavEntry<NavKey>>,
    onBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .then(
                when (currentKey) {
                    AuthNavKey.Splash -> Modifier
                    else -> Modifier.navigationBarsPadding()
                },
            ),
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.padding(
                when (currentKey) {
                    AuthNavKey.Splash, AuthNavKey.Login -> PaddingValues.Zero
                    else -> innerPadding
                },
            ),
            entries = entries,
            onBack = onBack,
            transitionSpec = {
                when (currentKey) {
                    AuthNavKey.Login, AuthNavKey.Term -> ContentTransform(
                        targetContentEnter = slideInHorizontally(tween(SLIDE_ANIMATION_DURATION)) { it },
                        initialContentExit = slideOutHorizontally(tween(SLIDE_ANIMATION_DURATION)) { -it },
                    )

                    else -> ContentTransform(EnterTransition.None, ExitTransition.None)
                }
            },
            popTransitionSpec = {
                when (currentKey) {
                    AuthNavKey.Login -> ContentTransform(
                        targetContentEnter = slideInHorizontally(tween(SLIDE_ANIMATION_DURATION)) { -it },
                        initialContentExit = slideOutHorizontally(tween(SLIDE_ANIMATION_DURATION)) { it },
                    )

                    else -> ContentTransform(EnterTransition.None, ExitTransition.None)
                }
            },
        )
    }
}

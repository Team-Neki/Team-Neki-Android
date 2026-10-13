package com.neki.android.app.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.api.MapSceneMetadata

internal class MapSearchSceneStrategy(
    private val bottomBar: @Composable () -> Unit,
) : SceneStrategy<NavKey> {
    override fun SceneStrategyScope<NavKey>.calculateScene(entries: List<NavEntry<NavKey>>): Scene<NavKey>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val searchEntry = lastEntry.takeIf {
            it.metadata[MapSceneMetadata.ROLE] == MapSceneMetadata.Role.SEARCH
        }
        val mapEntry = if (searchEntry != null) entries.getOrNull(entries.lastIndex - 1) else lastEntry
        if (mapEntry?.metadata?.get(MapSceneMetadata.ROLE) != MapSceneMetadata.Role.MAP) return null

        return MapSearchScene(
            key = mapEntry.contentKey,
            mapEntry = mapEntry,
            searchEntry = searchEntry,
            previousEntries = entries.dropLast(1),
            bottomBar = bottomBar,
        )
    }
}

private data class MapSearchScene(
    override val key: Any,
    val mapEntry: NavEntry<NavKey>,
    val searchEntry: NavEntry<NavKey>?,
    override val previousEntries: List<NavEntry<NavKey>>,
    val bottomBar: @Composable () -> Unit,
) : Scene<NavKey> {
    override val entries: List<NavEntry<NavKey>> = listOfNotNull(mapEntry, searchEntry)

    override val content: @Composable () -> Unit = {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier.fillMaxSize().then(
                    if (searchEntry != null) Modifier.clearAndSetSemantics {} else Modifier,
                ),
            ) {
                Box(Modifier.weight(1f)) {
                    mapEntry.Content()
                }
                bottomBar()
            }
            searchEntry?.let { entry ->
                Surface(modifier = Modifier.fillMaxSize(), color = NekiTheme.colorScheme.white) {
                    entry.Content()
                }
            }
        }
    }
}

package com.neki.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.neki.android.app.ui.BottomNavigationBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.NavigatorImpl
import com.neki.android.core.navigation.toEntries
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: NavigatorImpl

    @Inject
    lateinit var entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val shouldShowBottomBar by remember(navigator.state.currentKey) {
                mutableStateOf(navigator.state.currentKey in navigator.state.topLevelKeys)
            }

            NekiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(
                            visible = shouldShowBottomBar,
                            currentTab = navigator.state.currentTopLevelKey,
                            currentKey = navigator.state.currentKey,
                            onTabSelected = { navigator.navigate(it.navKey) },
                        )
                    },
                ) { innerPadding ->
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        entries = navigator.state.  toEntries(
                            entryProvider = entryProvider {
                                entryProviderScopes.forEach { builder -> this.builder() }
                            },
                        ),
                        onBack = { navigator.goBack() },
                    )
                }
            }
        }
    }
}

package com.neki.android.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import com.neki.android.app.navigation.root.RootNavKey
import com.neki.android.app.navigation.root.RootNavigationState
import com.neki.android.core.dataapi.auth.AuthEvent
import com.neki.android.core.dataapi.auth.AuthEventManager
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

    @Inject
    lateinit var authEventManager: AuthEventManager

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
                        entries = navigator.state.toEntries(
                            entryProvider = entryProvider {
                                entryProviderScopes.forEach { builder -> this.builder() }
                            },
                        ),
                        onBack = { navigator.goBack() },
                    )
                }
            }
        }

        observeAuthEvents()
    }

    private fun observeAuthEvents() {
        lifecycleScope.launch {
            authEventManager.authEvent.collect { event ->
                when (event) {
                    AuthEvent.RefreshTokenExpired -> {
                        Toast.makeText(
                            this@MainActivity,
                            "RefreshToken이 만료되었습니다.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
    }
}

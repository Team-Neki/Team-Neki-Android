package com.neki.android.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import com.neki.android.core.dataapi.auth.AuthEvent
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.NavigatorImpl
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEventBus
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.core.navigation.root.RootNavigationState
import com.neki.android.core.navigation.toEntries
import com.neki.android.feature.auth.impl.LoginRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootNavigationState: RootNavigationState

    @Inject
    lateinit var navigator: NavigatorImpl

    @Inject
    lateinit var entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var authEventManager: AuthEventManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            NekiTheme {
                val resultBus = remember { ResultEventBus() }
                NekiTheme {
                    CompositionLocalProvider(LocalResultEventBus provides resultBus) {
                        when (rootNavigationState.currentRootKey) {
                            RootNavKey.Login -> {
                                LoginRoute(
                                    navigateMain = { navigator.navigateRoot(RootNavKey.Main) },
                                )
                            }

                            RootNavKey.Main -> {
                                MainScreen(
                                    currentKey = navigator.state.currentKey,
                                    currentTopLevelKey = navigator.state.currentTopLevelKey,
                                    topLevelKeys = navigator.state.topLevelKeys,
                                    entries = navigator.state.toEntries(
                                        entryProvider = entryProvider {
                                            entryProviderScopes.forEach { builder -> this.builder() }
                                        },
                                    ),
                                    onTabSelected = { navigator.navigate(it) },
                                    onBack = { navigator.goBack() },
                                    navigateLogin = { navigator.navigateRoot(RootNavKey.Login) },
                                )
                            }
                        }
                    }
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

                        navigator.navigateRoot(RootNavKey.Login)
                    }
                }
            }
        }
    }
}

package com.neki.android.app

import android.os.Bundle
import android.widget.Toast
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
        setContent {
            NekiTheme {
                when (rootNavigationState.currentRootKey) {
                    RootNavKey.Login -> {
                        LoginRoute(
                            navigateMain = { rootNavigationState.navigateRoot(RootNavKey.Main) }
                        )
                    }

                    RootNavKey.Main -> {
                        MainScreen(
                            currentKey = navigator.state.currentKey,
                            currentTopLevelKey = navigator.state.currentTopLevelKey,
                            topLevelKeys = navigator.state.topLevelKeys,
                            entries = navigator.state.toEntries(
                                entryProvider = entryProvider {
                                    entryProviderScopes.forEach { installer ->
                                        this.installer()
                                    }
                                },
                            ),
                            onTabSelected = { navigator.navigate(it) },
                            onBack = { navigator.goBack() },
                            navigateLogin = { rootNavigationState.navigateRoot(RootNavKey.Login) }
                        )
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
                    }
                }
            }
        }
    }
}

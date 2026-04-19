package com.neki.android.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entryProvider
import com.neki.android.app.main.MainRoute
import com.neki.android.core.dataapi.auth.AuthEvent
import com.neki.android.core.dataapi.auth.AuthEventManager
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.navigation.auth.AuthNavigator
import com.neki.android.core.navigation.auth.toEntries
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.main.toEntries
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEventBus
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.core.navigation.root.RootNavigationState
import com.neki.android.core.navigation.root.RootNavigator
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.auth.impl.navigation.authEntryProvider
import com.neki.android.feature.photo_upload.api.navigateToQRScan
import com.neki.android.feature.select_album.api.navigateToSelectAlbum
import android.net.Uri
import androidx.core.content.IntentCompat
import com.neki.android.core.analytics.logger.AnalyticsLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

private fun Intent.extractShareUriStrings(): ImmutableList<String> {
    val uris: List<Uri> = when (action) {
        Intent.ACTION_SEND -> {
            IntentCompat.getParcelableExtra(this, Intent.EXTRA_STREAM, Uri::class.java)
                ?.let { listOf(it) } ?: emptyList()
        }
        Intent.ACTION_SEND_MULTIPLE -> {
            IntentCompat.getParcelableArrayListExtra(this, Intent.EXTRA_STREAM, Uri::class.java)
                ?: emptyList()
        }
        else -> emptyList()
    }
    return uris.map { it.toString() }.toImmutableList()
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootNavigationState: RootNavigationState

    @Inject
    lateinit var rootNavigator: RootNavigator

    @Inject
    lateinit var authNavigator: AuthNavigator

    @Inject
    lateinit var mainNavigator: MainNavigator

    @Inject
    lateinit var entryProviderScopes: Set<@JvmSuppressWildcards EntryProviderInstaller>

    @Inject
    lateinit var authEventManager: AuthEventManager

    @Inject
    lateinit var analyticsLogger: AnalyticsLogger

    private var pendingShareUriStrings by mutableStateOf<ImmutableList<String>>(persistentListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pendingShareUriStrings = intent.extractShareUriStrings()

        analyticsLogger.setUserProperty("platform", "android")
        analyticsLogger.setUserProperty("app_version", BuildConfig.VERSION_NAME)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )

        setContent {
            NekiTheme {
                val resultBus = remember { ResultEventBus() }
                NekiTheme {
                    CompositionLocalProvider(LocalResultEventBus provides resultBus) {
                        when (rootNavigationState.currentRootKey) {
                            is RootNavKey.Auth -> {
                                AuthScreen(
                                    currentKey = authNavigator.state.currentKey,
                                    entries = authNavigator.state.toEntries(
                                        entryProvider = entryProvider {
                                            authEntryProvider(rootNavigator, authNavigator).invoke(this)
                                        },
                                    ),
                                    onBack = { authNavigator.goBack() },
                                )
                            }

                            RootNavKey.Main -> {
                                MainRoute(
                                    currentKey = mainNavigator.state.currentKey,
                                    currentTopLevelKey = mainNavigator.state.currentTopLevelKey,
                                    topLevelKeys = mainNavigator.state.topLevelKeys,
                                    entries = mainNavigator.state.toEntries(
                                        entryProvider = entryProvider {
                                            entryProviderScopes.forEach { builder -> this.builder() }
                                        },
                                    ),
                                    onTabSelected = { mainNavigator.navigate(it) },
                                    onBack = { mainNavigator.goBack() },
                                    navigateToQRScan = mainNavigator::navigateToQRScan,
                                    navigateToSelectAlbum = { action -> mainNavigator.navigateToSelectAlbum(action) },
                                    pendingShareUriStrings = pendingShareUriStrings,
                                    onShareUrisConsumed = { pendingShareUriStrings = persistentListOf() },
                                )
                            }
                        }
                    }
                }
            }
        }
        observeAuthEvents()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uriStrings = intent.extractShareUriStrings()
        if (uriStrings.isNotEmpty()) {
            pendingShareUriStrings = uriStrings
        }
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

                        rootNavigator.navigateToAuth(AuthNavKey.Login)
                    }
                }
            }
        }
    }
}

package com.neki.android.app.navigation.keys

import com.neki.android.app.navigation.TopLevelNavItem
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.auth.api.AuthNavKey

internal val START_ROOT_NAV_KEY = RootNavKey.Auth
internal val START_AUTH_NAV_KEY = AuthNavKey.Splash
internal val START_MAIN_NAV_KEY = ArchiveNavKey.Archive
internal val TOP_LEVEL_MAIN_NAV_KEYS = TopLevelNavItem.entries.map { it.navKey }

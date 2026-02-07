package com.neki.android.app.navigation.keys

import com.neki.android.app.navigation.TopLevelNavItem
import com.neki.android.core.navigation.root.RootNavKey
import com.neki.android.feature.archive.api.ArchiveNavKey

internal val START_ROOT_NAV_KEY = RootNavKey.Login
internal val START_NAV_KEY = ArchiveNavKey.Archive
internal val TOP_LEVEL_NAV_KEYS = TopLevelNavItem.entries.map { it.navKey }

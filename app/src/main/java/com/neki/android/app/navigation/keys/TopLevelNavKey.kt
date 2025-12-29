package com.neki.android.app.navigation.keys

import com.neki.android.feature.archive.com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.pose.api.PoseNavKey

internal val TOP_LEVEL_NAV_KEYS = listOf(
    PoseNavKey.Pose,
    ArchiveNavKey.Archive,
    MapNavKey.Map,
)

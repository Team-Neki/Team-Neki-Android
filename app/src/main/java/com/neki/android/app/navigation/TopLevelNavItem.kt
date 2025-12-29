package com.neki.android.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.neki.android.app.R
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.pose.api.PoseNavKey

enum class TopLevelNavItem(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val iconTextId: Int,
    val navKey: NavKey,
) {
    POSE_RECOMMEND(
        selectedIcon = R.drawable.ic_nav_pose_selected,
        unselectedIcon = R.drawable.ic_nav_pose_unselected,
        iconTextId = R.string.top_level_nav_pose,
        navKey = PoseNavKey.Pose,
    ),
    ARCHIVE(
        selectedIcon = R.drawable.ic_nav_archive_selected,
        unselectedIcon = R.drawable.ic_nav_archive_unselected,
        iconTextId = R.string.top_level_nav_archive,
        navKey = ArchiveNavKey.Archive
    ),
    MAP(
        selectedIcon = R.drawable.ic_nav_map_selected,
        unselectedIcon = R.drawable.ic_nav_map_unselected,
        iconTextId = R.string.top_level_nav_map,
        navKey = MapNavKey.Map
    );

    companion object {
        val startTopLevelItem = ARCHIVE
    }
}
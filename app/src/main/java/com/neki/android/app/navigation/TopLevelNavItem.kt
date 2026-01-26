package com.neki.android.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.neki.android.app.R
import com.neki.android.feature.archive.api.ArchiveNavKey
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.mypage.api.MyPageNavKey
import com.neki.android.feature.pose.api.PoseNavKey

enum class TopLevelNavItem(
    @DrawableRes val selectedIconRes: Int,
    @DrawableRes val unselectedIconRes: Int,
    @StringRes val iconStringRes: Int,
    val navKey: NavKey,
) {
    ARCHIVE(
        selectedIconRes = R.drawable.ic_nav_archive_selected,
        unselectedIconRes = R.drawable.ic_nav_archive_unselected,
        iconStringRes = R.string.top_level_nav_archive,
        navKey = ArchiveNavKey.Archive,
    ),
    POSE_RECOMMEND(
        selectedIconRes = R.drawable.ic_nav_pose_selected,
        unselectedIconRes = R.drawable.ic_nav_pose_unselected,
        iconStringRes = R.string.top_level_nav_pose,
        navKey = PoseNavKey.PoseMain,
    ),
    MAP(
        selectedIconRes = R.drawable.ic_nav_map_selected,
        unselectedIconRes = R.drawable.ic_nav_map_unselected,
        iconStringRes = R.string.top_level_nav_map,
        navKey = MapNavKey.Map,
    ),
    MYPAGE(
        selectedIconRes = R.drawable.ic_nav_mypage_selected,
        unselectedIconRes = R.drawable.ic_nav_mypage_unselected,
        iconStringRes = R.string.top_level_nav_mypage,
        navKey = MyPageNavKey.MyPage,
    ),
    ;

    companion object {
        val startTopLevelItem = ARCHIVE
    }
}

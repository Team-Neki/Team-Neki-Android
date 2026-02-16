package com.neki.android.feature.mypage.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.HiltSharedViewModelStoreNavEntryDecorator
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.root.RootNavigator
import com.neki.android.feature.auth.api.AuthNavKey
import com.neki.android.feature.mypage.api.MyPageNavKey
import com.neki.android.feature.mypage.api.navigateToEditProfile
import com.neki.android.feature.mypage.api.navigateToPermission
import com.neki.android.feature.mypage.api.navigateToProfile
import com.neki.android.feature.mypage.impl.main.MyPageRoute
import com.neki.android.feature.mypage.impl.permission.PermissionRoute
import com.neki.android.feature.mypage.impl.profile.EditProfileRoute
import com.neki.android.feature.mypage.impl.profile.ProfileSettingRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object MyPageEntryProviderModule {

    @IntoSet
    @Provides
    fun provideMyPageEntryBuilder(
        rootNavigator: RootNavigator,
        mainNavigator: MainNavigator,
    ): EntryProviderInstaller = {
        myPageEntry(rootNavigator, mainNavigator)
    }
}

private fun EntryProviderScope<NavKey>.myPageEntry(
    rootNavigator: RootNavigator,
    mainNavigator: MainNavigator,
) {
    entry<MyPageNavKey.MyPage>(
        clazzContentKey = { key -> key.toString() },
    ) {
        MyPageRoute(
            navigateToPermission = mainNavigator::navigateToPermission,
            navigateToProfile = mainNavigator::navigateToProfile,
        )
    }

    entry<MyPageNavKey.Permission>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        PermissionRoute(
            navigateBack = mainNavigator::goBack,
        )
    }

    entry<MyPageNavKey.Profile>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        ProfileSettingRoute(
            navigateBack = mainNavigator::goBack,
            navigateToEditProfile = mainNavigator::navigateToEditProfile,
            navigateToLogin = { rootNavigator.navigateToAuth(AuthNavKey.Login) },
        )
    }

    entry<MyPageNavKey.EditProfile>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        EditProfileRoute(
            navigateBack = mainNavigator::goBack,
        )
    }
}

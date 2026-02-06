package com.neki.android.feature.mypage.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.HiltSharedViewModelStoreNavEntryDecorator
import com.neki.android.core.navigation.Navigator
import com.neki.android.core.navigation.root.RootNavKey
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
    fun provideMyPageEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        myPageEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.myPageEntry(navigator: Navigator) {
    entry<MyPageNavKey.MyPage>(
        clazzContentKey = { key -> key.toString() },
    ) {
        MyPageRoute(
            navigateToPermission = navigator::navigateToPermission,
            navigateToProfile = navigator::navigateToProfile,
        )
    }

    entry<MyPageNavKey.Permission>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        PermissionRoute(
            navigateBack = navigator::goBack,
        )
    }

    entry<MyPageNavKey.Profile>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        ProfileSettingRoute(
            navigateBack = navigator::goBack,
            navigateToEditProfile = navigator::navigateToEditProfile,
            navigateToLogin = { navigator.navigateRoot(RootNavKey.Login) },
        )
    }

    entry<MyPageNavKey.EditProfile>(
        metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
            MyPageNavKey.MyPage.toString(),
        ),
    ) {
        EditProfileRoute(
            navigateBack = navigator::goBack,
        )
    }
}

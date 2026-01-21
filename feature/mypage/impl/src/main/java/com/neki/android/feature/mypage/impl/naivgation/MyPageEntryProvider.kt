package com.neki.android.feature.mypage.impl.naivgation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.mypage.api.MyPageNavKey
import com.neki.android.feature.mypage.impl.MyPageRoute
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
    entry<MyPageNavKey.MyPage> {
        MyPageRoute()
    }
}

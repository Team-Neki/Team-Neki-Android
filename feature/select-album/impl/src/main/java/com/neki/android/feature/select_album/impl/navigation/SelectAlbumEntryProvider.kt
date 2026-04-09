package com.neki.android.feature.select_album.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.feature.select_album.api.SelectAlbumNavKey
import com.neki.android.feature.select_album.impl.SelectAlbumRoute
import com.neki.android.feature.select_album.impl.SelectAlbumViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object SelectAlbumEntryProviderModule {

    @IntoSet
    @Provides
    fun provideSelectAlbumEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        selectAlbumEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.selectAlbumEntry(navigator: MainNavigator) {
    entry<SelectAlbumNavKey.SelectAlbum> { key ->
        SelectAlbumRoute(
            viewModel = hiltViewModel<SelectAlbumViewModel, SelectAlbumViewModel.Factory>(
                creationCallback = { factory ->
                    factory.create(key.title, key.multiSelect, key.photoCount)
                },
            ),
            navigateBack = navigator::goBack,
        )
    }
}

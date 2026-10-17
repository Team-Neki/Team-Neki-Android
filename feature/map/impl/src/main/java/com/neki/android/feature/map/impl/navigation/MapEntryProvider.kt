package com.neki.android.feature.map.impl.navigation

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.EntryProviderInstaller
import com.neki.android.core.navigation.main.MainNavigator
import com.neki.android.core.navigation.result.LocalResultEventBus
import com.neki.android.core.navigation.result.ResultEffect
import com.neki.android.feature.map.api.MapResult
import com.neki.android.feature.map.api.MapNavKey
import com.neki.android.feature.map.api.MapSceneMetadata
import com.neki.android.feature.map.api.navigateToPhotoBoothOrderChange
import com.neki.android.feature.map.api.navigateToSearch
import com.neki.android.feature.map.impl.MapIntent
import com.neki.android.feature.map.impl.MapRoute
import com.neki.android.feature.map.impl.MapViewModel
import com.neki.android.feature.map.impl.photobooth.PhotoBoothOrderChangeRoute
import com.neki.android.feature.map.impl.photobooth.PhotoBoothOrderChangeViewModel
import com.neki.android.feature.map.impl.search.SearchResultPanelIntent
import com.neki.android.feature.map.impl.search.SearchRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import kotlinx.collections.immutable.toImmutableList

@Module
@InstallIn(ActivityRetainedComponent::class)
object MapEntryProviderModule {

    @IntoSet
    @Provides
    fun provideMapEntryBuilder(navigator: MainNavigator): EntryProviderInstaller = {
        mapEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.mapEntry(navigator: MainNavigator) {
    entry<MapNavKey.Map>(
        clazzContentKey = { "neki.map.main" },
        metadata = mapOf(MapSceneMetadata.ROLE to MapSceneMetadata.Role.MAP),
    ) {
        val resultBus = LocalResultEventBus.current
        val viewModel = hiltViewModel<MapViewModel>()

        ResultEffect<MapResult>(resultBus) { result ->
            when (result) {
                is MapResult.BrandOrderChanged -> viewModel.store.onIntent(MapIntent.UpdateBrandOrder(result.orderedBrands))
                is MapResult.SearchSelected -> viewModel.store.onIntent(
                    MapIntent.SearchPanel(SearchResultPanelIntent.ShowResults(result.keyword)),
                )
            }
        }

        MapRoute(
            viewModel = viewModel,
            isVisible = navigator.state.currentKey == MapNavKey.Map,
            navigateToSearch = navigator::navigateToSearch,
            navigateToPhotoBoothOrderChange = { brands ->
                navigator.navigateToPhotoBoothOrderChange(brands)
            },
        )
    }

    entry<MapNavKey.Search>(
        clazzContentKey = { "neki.map.search" },
        metadata = mapOf(MapSceneMetadata.ROLE to MapSceneMetadata.Role.SEARCH),
    ) {
        val resultBus = LocalResultEventBus.current
        SearchRoute(
            navigateBack = navigator::goBack,
            onResultSelected = { result ->
                resultBus.sendResult<MapResult>(result = MapResult.SearchSelected(result.keyword), allowDuplicate = false)
                navigator.goBack()
            },
        )
    }

    entry<MapNavKey.PhotoBoothOrderChange> { key ->
        val viewModel = hiltViewModel<PhotoBoothOrderChangeViewModel, PhotoBoothOrderChangeViewModel.Factory>(
            creationCallback = { factory -> factory.create(key.brands.toImmutableList()) },
        )
        PhotoBoothOrderChangeRoute(
            viewModel = viewModel,
            navigateBack = navigator::goBack,
        )
    }
}

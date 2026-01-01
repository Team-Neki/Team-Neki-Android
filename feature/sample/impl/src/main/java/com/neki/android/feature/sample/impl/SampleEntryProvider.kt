package com.neki.android.feature.sample.impl

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.EntryProviderInstaller
import com.neki.android.core.navigation.Navigator
import com.neki.android.feature.sample.api.SampleNavKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object SampleEntryProviderModule {

    @IntoSet
    @Provides
    fun provideSampleEntryBuilder(navigator: Navigator): EntryProviderInstaller = {
        sampleEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.sampleEntry(navigator: Navigator) {
    entry<SampleNavKey.Sample> { key ->
        navigator
        val viewModel = hiltViewModel<SampleViewModel, SampleViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            },
        )
        SampleScreen(viewModel = viewModel)
    }
}

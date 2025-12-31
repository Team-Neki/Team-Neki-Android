package com.neki.android.feature.sample.impl

import androidx.lifecycle.ViewModel
import com.neki.android.feature.sample.api.SampleNavKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = SampleViewModel.Factory::class)
class SampleViewModel @AssistedInject constructor(
    @Assisted val navKey: SampleNavKey.Sample,
) : ViewModel() {

    val id = navKey.id

    @AssistedFactory
    interface Factory {
        fun create(navKey: SampleNavKey.Sample): SampleViewModel
    }
}

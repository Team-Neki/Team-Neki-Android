package com.neki.android.feature.sample.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.Navigator
import kotlinx.serialization.Serializable

sealed interface SampleNavKey : NavKey {

    @Serializable
    data class Sample(val id: Long) : SampleNavKey
}

fun Navigator.navigateToSample(id: Long) {
    navigate(SampleNavKey.Sample(id))
}

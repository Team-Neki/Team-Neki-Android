package com.neki.android.feature.map.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface MapNavKey : NavKey {

    @Serializable
    data object Map : MapNavKey

    @Serializable
    data object PhotoBoothOrderChange : MapNavKey
}

fun MainNavigator.navigateToMap() {
    navigate(MapNavKey.Map)
}

fun MainNavigator.navigateToPhotoBoothOrderChange() {
    navigate(MapNavKey.PhotoBoothOrderChange)
}

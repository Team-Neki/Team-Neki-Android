package com.neki.android.feature.map.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.model.Brand
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface MapNavKey : NavKey {

    @Serializable
    data object Map : MapNavKey

    @Serializable
    data class Search(val initialQuery: String = "") : MapNavKey

    @Serializable
    data class PhotoBoothOrderChange(val brands: List<Brand>) : MapNavKey
}

fun MainNavigator.navigateToMap() {
    navigate(MapNavKey.Map)
}

fun MainNavigator.navigateToSearch(initialQuery: String = "") {
    navigate(MapNavKey.Search(initialQuery = initialQuery))
}

fun MainNavigator.navigateToPhotoBoothOrderChange(brands: List<Brand>) {
    navigate(MapNavKey.PhotoBoothOrderChange(brands))
}

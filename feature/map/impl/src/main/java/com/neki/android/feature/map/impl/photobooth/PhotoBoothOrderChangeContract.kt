package com.neki.android.feature.map.impl.photobooth

import com.neki.android.core.model.Brand
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class PhotoBoothOrderChangeState(
    val brands: ImmutableList<Brand> = persistentListOf(),
    val initialBrands: ImmutableList<Brand> = persistentListOf(),
    val isOrderChanged: Boolean = false,
)

sealed interface PhotoBoothOrderChangeIntent {
    data object LoadBrands : PhotoBoothOrderChangeIntent
    data class ReorderBrand(val from: Int, val to: Int) : PhotoBoothOrderChangeIntent
    data object ClickComplete : PhotoBoothOrderChangeIntent
}

sealed interface PhotoBoothOrderChangeSideEffect {
    data object NavigateBack : PhotoBoothOrderChangeSideEffect
}

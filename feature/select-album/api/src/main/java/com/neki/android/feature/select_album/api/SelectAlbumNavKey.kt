package com.neki.android.feature.select_album.api

import androidx.navigation3.runtime.NavKey
import com.neki.android.core.navigation.main.MainNavigator
import kotlinx.serialization.Serializable

sealed interface SelectAlbumNavKey : NavKey {

    @Serializable
    data class SelectAlbum(
        val title: String,
        val multiSelect: Boolean,
        val action: SelectAlbumAction,
    ) : SelectAlbumNavKey
}

fun MainNavigator.navigateToSelectAlbum(
    action: SelectAlbumAction,
    title: String = "모든 앨범",
    multiSelect: Boolean = true,
) {
    navigate(SelectAlbumNavKey.SelectAlbum(title, multiSelect, action))
}

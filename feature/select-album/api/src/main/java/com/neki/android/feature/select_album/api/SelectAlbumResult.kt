package com.neki.android.feature.select_album.api

import com.neki.android.core.model.AlbumPreview

data class SelectAlbumResult(
    val selectedAlbums: List<AlbumPreview>,
)

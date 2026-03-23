package com.neki.android.feature.archive.api

sealed interface ArchiveResult

data object PhotoDetailResult : ArchiveResult

data object AlbumDetailResult : ArchiveResult

data object AllPhotoResult : ArchiveResult

data object AllAlbumResult : ArchiveResult

data object PhotoUploadedResult : ArchiveResult

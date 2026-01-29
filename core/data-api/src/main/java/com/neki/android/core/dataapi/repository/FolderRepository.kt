package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.AlbumPreview

interface FolderRepository {
    suspend fun getFolders(): Result<List<AlbumPreview>>
    suspend fun createFolder(name: String): Result<Long>
}

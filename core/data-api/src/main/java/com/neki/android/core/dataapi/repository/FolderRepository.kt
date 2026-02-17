package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.AlbumPreview

interface FolderRepository {
    suspend fun getFolders(): Result<List<AlbumPreview>>
    suspend fun createFolder(name: String): Result<Unit>
    suspend fun deleteFolder(id: List<Long>, deletePhotos: Boolean): Result<Unit>
    suspend fun removePhotosFromFolder(folderId: Long, photoIds: List<Long>): Result<Unit>
    suspend fun updateFolder(folderId: Long, name: String): Result<Unit>
}

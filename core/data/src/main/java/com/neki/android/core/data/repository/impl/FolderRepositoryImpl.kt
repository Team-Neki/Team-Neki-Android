package com.neki.android.core.data.repository.impl

import com.neki.android.core.data.remote.api.FolderService
import com.neki.android.core.data.remote.model.request.CreateFolderRequest
import com.neki.android.core.data.remote.model.request.DeleteFolderRequest
import com.neki.android.core.data.remote.model.request.DeletePhotoRequest
import com.neki.android.core.data.remote.model.request.UpdateFolderRequest
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.FolderRepository
import com.neki.android.core.model.AlbumPreview
import javax.inject.Inject

class FolderRepositoryImpl @Inject constructor(
    private val folderService: FolderService,
) : FolderRepository {
    override suspend fun getFolders(): Result<List<AlbumPreview>> = runSuspendCatching {
        folderService.getFolders().data.toModels()
    }

    override suspend fun createFolder(name: String): Result<Unit> = runSuspendCatching {
        folderService.createFolder(
            requestBody = CreateFolderRequest(name = name),
        ).data
    }

    override suspend fun deleteFolder(id: List<Long>, deletePhotos: Boolean): Result<Unit> = runSuspendCatching {
        folderService.deleteFolder(
            requestBody = DeleteFolderRequest(folderIds = id),
            deletePhotos = deletePhotos,
        ).data
    }

    override suspend fun removePhotosFromFolder(folderId: Long, photoIds: List<Long>): Result<Unit> = runSuspendCatching {
        folderService.removePhotosFromFolder(
            folderId = folderId,
            requestBody = DeletePhotoRequest(photoIds = photoIds),
        ).data
    }

    override suspend fun updateFolder(folderId: Long, name: String): Result<Unit> = runSuspendCatching {
        folderService.updateFolder(
            folderId = folderId,
            requestBody = UpdateFolderRequest(name = name),
        ).data
    }
}

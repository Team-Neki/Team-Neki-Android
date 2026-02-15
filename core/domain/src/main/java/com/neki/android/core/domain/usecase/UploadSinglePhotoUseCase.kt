package com.neki.android.core.domain.usecase

import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.extension.ContentTypeUtil
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadSinglePhotoUseCase @Inject constructor(
    private val mediaUploadRepository: MediaUploadRepository,
    private val photoRepository: PhotoRepository,
) {
    suspend operator fun invoke(
        imageUrl: String,
        contentType: ContentType = ContentType.JPEG,
        folderId: Long? = null,
    ): Result<Unit> = runSuspendCatching {
        val fileName = ContentTypeUtil.generateFileName(contentType)

        val mediaId = mediaUploadRepository.uploadImageFromUrl(
            imageUrl = imageUrl,
            fileName = fileName,
            contentType = contentType,
            mediaType = MediaType.PHOTO_BOOTH,
        ).getOrThrow()

        photoRepository.registerPhoto(
            mediaIds = listOf(mediaId),
            folderId = folderId,
        ).getOrThrow()
    }
}

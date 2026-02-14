package com.neki.android.core.domain.usecase

import android.net.Uri
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.extension.ContentTypeUtil.generateFileName
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.MediaType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadMultiplePhotoUseCase @Inject constructor(
    private val mediaUploadRepository: MediaUploadRepository,
    private val photoRepository: PhotoRepository,
) {
    suspend operator fun invoke(
        imageUris: List<Uri>,
        contentType: ContentType = ContentType.JPEG,
        folderId: Long? = null,
    ): Result<Unit> = runSuspendCatching {
        require(imageUris.isNotEmpty()) { "imageUris must not be empty" }

        val mediaIds = coroutineScope {
            imageUris.map { uri ->
                async {
                    val fileName = generateFileName(contentType)
                    mediaUploadRepository.uploadImageFromUri(
                        uri = uri,
                        contentType = contentType,
                        fileName = fileName,
                        mediaType = MediaType.PHOTO_BOOTH,
                    ).getOrThrow()
                }
            }.awaitAll()
        }

        photoRepository.registerPhoto(
            mediaIds = mediaIds,
            folderId = folderId,
        ).getOrThrow()
    }
}

package com.neki.android.core.domain.usecase

import android.net.Uri
import com.neki.android.core.data.util.runSuspendCatching
import com.neki.android.core.dataapi.repository.MediaUploadRepository
import com.neki.android.core.dataapi.repository.PhotoRepository
import com.neki.android.core.domain.extension.ContentTypeUtil.generateFileName
import com.neki.android.core.model.ContentType
import com.neki.android.core.model.Media
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
    ): Result<List<Media>> = runSuspendCatching {
        require(imageUris.isNotEmpty()) { "imageUris must not be empty" }

        val fileName = generateFileName(contentType)

        // 1. 업로드 티켓 발급 (이미지 개수만큼)
        val tickets = mediaUploadRepository.getMultipleUploadTicket(
            uploadCount = imageUris.size,
            fileName = fileName,
            contentType = contentType.label,
            mediaType = MediaType.PHOTO_BOOTH.name,
        ).getOrThrow()

        // 2. 각 이미지를 Presigned URL로 업로드
        coroutineScope {
            imageUris.mapIndexed { index, uri ->
                async {
                    val ticket = tickets[index]
                    mediaUploadRepository.uploadImageFromUri(
                        uploadUrl = ticket.uploadUrl,
                        uri = uri,
                        contentType = contentType,
                    ).getOrThrow()
                }
            }.awaitAll()
        }

        // 3. 사진 등록 (모든 mediaId를 한번에)
        val mediaIds = tickets.map { it.mediaId }
        photoRepository.registerPhoto(
            mediaIds = mediaIds,
            folderId = folderId,
        ).getOrThrow()

        return@runSuspendCatching tickets.map { ticket ->
            Media(
                mediaId = ticket.mediaId,
                folderId = folderId,
                fileName = fileName,
                contentType = contentType,
            )
        }
    }
}

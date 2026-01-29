package com.neki.android.core.data.remote.api

import com.neki.android.core.data.remote.model.request.CreateFolderRequest
import com.neki.android.core.data.remote.model.request.DeleteFolderRequest
import com.neki.android.core.data.remote.model.response.BasicNullableResponse
import com.neki.android.core.data.remote.model.response.BasicResponse
import com.neki.android.core.data.remote.model.response.CreateFolderResponse
import com.neki.android.core.data.remote.model.response.FolderResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import javax.inject.Inject

class FolderService @Inject constructor(
    private val client: HttpClient,
) {
    // 폴더 목록 조회
    suspend fun getFolders(): BasicResponse<FolderResponse> {
        return client.get("/api/folders").body()
    }

    // 폴더 생성
    suspend fun createFolder(requestBody: CreateFolderRequest): BasicNullableResponse<CreateFolderResponse> {
        return client.post("/api/folders") { setBody(requestBody) }.body()
    }

    // 폴더 삭제
    suspend fun deleteFolder(requestBody: DeleteFolderRequest): BasicNullableResponse<Unit> {
        return client.post("/api/folders") { setBody(requestBody) }.body()
    }
}

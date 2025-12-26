package com.neki.android.data.remote.api

import com.neki.android.data.remote.model.PostResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiService(
    private val client: HttpClient
) {
    // 게시글 목록 조회
    suspend fun getPosts(): List<PostResponse> {
        return client.get("/posts").body()
    }

    // 게시글 조회
    suspend fun getPost(
        id: Int
    ): PostResponse {
        return client.get("/posts/$id").body()
    }


}

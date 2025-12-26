package com.neki.android.core.dataapi.repository

import com.neki.android.core.model.Post

interface SampleRepository {
    suspend fun getPosts(): List<Post>
    suspend fun getPost(
        id: Int
    ): Post


}
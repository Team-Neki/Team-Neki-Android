package com.neki.android.core.data.remote.model.response

import com.neki.android.core.model.Brand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrandResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("code") val code: String,
    @SerialName("imageUrl") val imageUrl: String,
) {
    internal fun toModel(): Brand = Brand(
        id = id,
        name = name,
        code = code,
        imageUrl = imageUrl,
    )
}

internal fun List<BrandResponse>.toModels(): List<Brand> = map { it.toModel() }

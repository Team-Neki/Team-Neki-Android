package com.neki.android.feature.map.impl.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.neki.android.core.model.Brand
import kotlinx.collections.immutable.ImmutableList

@Composable
fun rememberCachedBrandImage(brands: ImmutableList<Brand>): Map<String, ImageBitmap> {
    val context = LocalContext.current
    val cache = remember { mutableStateMapOf<String, ImageBitmap>() }

    LaunchedEffect(brands) {
        brands.forEach { brand ->
            if (brand.imageUrl.isNotEmpty() && !cache.containsKey(brand.imageUrl)) {
                val request = ImageRequest.Builder(context)
                    .data(brand.imageUrl)
                    .allowHardware(false)
                    .build()
                val result = context.imageLoader.execute(request)
                if (result is SuccessResult) {
                    cache[brand.imageUrl] = result.image.toBitmap().asImageBitmap()
                }
            }
        }
    }

    return cache
}

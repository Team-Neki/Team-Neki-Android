package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.pinShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.PhotoBooth
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_BACKGROUND_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_IMAGE_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_IMAGE_SIZE
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_PADDING
import com.neki.android.feature.map.impl.const.MapConst.MARKER_BACKGROUND_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.MARKER_IMAGE_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.MARKER_IMAGE_SIZE
import com.neki.android.feature.map.impl.const.MapConst.MARKER_PADDING
import com.neki.android.feature.map.impl.const.MapConst.MARKER_TRIANGLE_HEIGHT
import com.neki.android.feature.map.impl.const.MapConst.MARKER_TRIANGLE_WIDTH

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun PhotoBoothMarker(
    photoBooth: PhotoBooth,
    cachedBitmap: ImageBitmap? = null,
    onClick: () -> Unit = {},
) {
    MarkerComposable(
        keys = arrayOf("${photoBooth.id}", "${photoBooth.isFocused}", "$cachedBitmap"),
        state = rememberUpdatedMarkerState(
            position = LatLng(photoBooth.latitude, photoBooth.longitude),
        ),
        captionText = "${photoBooth.brandName}\n${photoBooth.branchName}",
        captionTextSize = 12.sp,
        captionColor = NekiTheme.colorScheme.gray900,
        onClick = {
            onClick()
            true
        },
    ) {
        PhotoBoothMarkerContent(
            cachedBitmap = cachedBitmap,
            isFocused = photoBooth.isFocused,
        )
    }
}

@Composable
internal fun PhotoBoothMarkerContent(
    modifier: Modifier = Modifier,
    cachedBitmap: ImageBitmap? = null,
    isFocused: Boolean = false,
) {
    val caretColor = if (isFocused) NekiTheme.colorScheme.gray900 else NekiTheme.colorScheme.white
    val bodySize = if (isFocused) {
        FOCUSED_MARKER_IMAGE_SIZE + FOCUSED_MARKER_PADDING * 2
    } else MARKER_IMAGE_SIZE + MARKER_PADDING * 2
    val totalHeight = bodySize + MARKER_TRIANGLE_HEIGHT - 1

    Box(
        modifier = modifier
            .padding(1.dp)
            .size(
                width = bodySize.dp,
                height = totalHeight.dp,
            ),
        contentAlignment = Alignment.TopCenter,
    ) {
        // 브랜드 이미지 그림자
        Box(
            modifier = Modifier
                .size(bodySize.dp)
                .pinShadow(
                    shape = RoundedCornerShape(
                        if (isFocused) FOCUSED_MARKER_BACKGROUND_RADIUS.dp else MARKER_BACKGROUND_RADIUS.dp,
                    ),
                ),
        )

        // 꼬리
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(
                    width = MARKER_TRIANGLE_WIDTH.dp,
                    height = MARKER_TRIANGLE_HEIGHT.dp,
                ),
        ) {
            val shadowColor = Color.Black.copy(alpha = 0.4f)
            val offsetY = 1.dp.toPx()
            val blurRadius = 2.5.dp.toPx()

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2, size.height)
                close()
            }

            // 삼각형 그림자
            drawIntoCanvas { canvas ->
                val shadowPaint = Paint().apply {
                    asFrameworkPaint().apply {
                        color = android.graphics.Color.TRANSPARENT
                        setShadowLayer(
                            blurRadius,
                            0f,
                            offsetY,
                            shadowColor.toArgb(),
                        )
                    }
                }
                canvas.drawPath(path, shadowPaint)
            }

            drawPath(path, caretColor)
        }

        // 브랜드 이미지
        Box(
            modifier = Modifier
                .then(
                    if (isFocused) {
                        Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        NekiTheme.colorScheme.gray600,
                                        NekiTheme.colorScheme.gray900,
                                    ),
                                ),
                                shape = RoundedCornerShape(FOCUSED_MARKER_BACKGROUND_RADIUS.dp),
                            )
                            .padding(FOCUSED_MARKER_PADDING.dp)
                    } else {
                        Modifier
                            .background(
                                color = NekiTheme.colorScheme.white,
                                shape = RoundedCornerShape(MARKER_BACKGROUND_RADIUS.dp),
                            )
                            .padding(MARKER_PADDING.dp)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (cachedBitmap != null) {
                Image(
                    modifier = Modifier
                        .size(if (isFocused) FOCUSED_MARKER_IMAGE_SIZE.dp else MARKER_IMAGE_SIZE.dp)
                        .clip(
                            RoundedCornerShape(
                                if (isFocused) FOCUSED_MARKER_IMAGE_RADIUS.dp else MARKER_IMAGE_RADIUS.dp,
                            ),
                        ),
                    bitmap = cachedBitmap,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            } else {
                Image(
                    modifier = Modifier
                        .size(if (isFocused) FOCUSED_MARKER_IMAGE_SIZE.dp else MARKER_IMAGE_SIZE.dp)
                        .clip(
                            RoundedCornerShape(
                                if (isFocused) FOCUSED_MARKER_IMAGE_RADIUS.dp else MARKER_IMAGE_RADIUS.dp,
                            ),
                        ),
                    painter = painterResource(R.drawable.icon_photo_booth_empty),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
            }
        }
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothMarkerPreview() {
    NekiTheme {
        PhotoBoothMarkerContent()
    }
}

@ComponentPreview
@Composable
private fun PhotoBoothMarkerSelectedPreview() {
    NekiTheme {
        PhotoBoothMarkerContent(
            isFocused = true,
        )
    }
}

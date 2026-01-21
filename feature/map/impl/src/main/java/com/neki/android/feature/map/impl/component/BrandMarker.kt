package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
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
internal fun BrandMarker(
    modifier: Modifier = Modifier,
    key: Array<String>,
    latitude: Double,
    longitude: Double,
    brandImageRes: Int,
    isFocused: Boolean = false,
    onClick: () -> Unit = {},
) {
    MarkerComposable(
        keys = arrayOf(*key, isFocused),
        state = rememberUpdatedMarkerState(
            position = LatLng(latitude, longitude),
        ),
        captionText = "인생네컷",
        captionTextSize = 12.sp,
        captionColor = NekiTheme.colorScheme.gray900,
        onClick = {
            onClick()
            true
        },
    ) {
        BrandMarkerContent(
            brandImageRes = brandImageRes,
            isFocused = isFocused,
        )
    }
}

@Composable
internal fun BrandMarkerContent(
    modifier: Modifier = Modifier,
    brandImageRes: Int,
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
                .buttonShadow(
                    color = Color.Black.copy(alpha = 0.38f),
                    shape = RoundedCornerShape(
                        if (isFocused) FOCUSED_MARKER_BACKGROUND_RADIUS.dp else MARKER_BACKGROUND_RADIUS.dp,
                    ),
                    offsetY = 1.18.dp,
                    blurRadius = 2.55.dp,
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
            val shadowColor = Color.Black.copy(alpha = 0.38f)
            val offsetY = 1.18.dp.toPx()
            val blurRadius = 2.55.dp.toPx()

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
            AsyncImage(
                modifier = Modifier
                    .size(if (isFocused) FOCUSED_MARKER_IMAGE_SIZE.dp else MARKER_IMAGE_SIZE.dp)
                    .clip(
                        RoundedCornerShape(
                            if (isFocused) FOCUSED_MARKER_IMAGE_RADIUS.dp else MARKER_IMAGE_RADIUS.dp,
                        ),
                    ),
                model = brandImageRes,
                contentDescription = null,
            )
        }
    }
}

@ComponentPreview
@Composable
private fun BrandMarkerContentPreview() {
    NekiTheme {
        BrandMarkerContent(
            brandImageRes = R.drawable.icon_life_four_cut,
        )
    }
}

@ComponentPreview
@Composable
private fun BrandMarkerContentSelectedPreview() {
    NekiTheme {
        BrandMarkerContent(
            brandImageRes = R.drawable.icon_life_four_cut,
            isFocused = true,
        )
    }
}

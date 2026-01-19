package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.const.FourCutBrand
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_BACKGROUND_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_BACKGROUND_SIZE
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_IMAGE_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_IMAGE_SIZE
import com.neki.android.feature.map.impl.const.MapConst.FOCUSED_MARKER_PADDING
import com.neki.android.feature.map.impl.const.MapConst.MARKER_BACKGROUND_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.MARKER_BACKGROUND_SIZE
import com.neki.android.feature.map.impl.const.MapConst.MARKER_IMAGE_RADIUS
import com.neki.android.feature.map.impl.const.MapConst.MARKER_IMAGE_SIZE
import com.neki.android.feature.map.impl.const.MapConst.MARKER_PADDING
import com.neki.android.feature.map.impl.const.MapConst.MARKER_TRIANGLE_SIZE

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun BrandMarker(
    modifier: Modifier = Modifier,
    key: Array<String>,
    latitude: Double,
    longitude: Double,
    brand: FourCutBrand,
    isFocused: Boolean = false,
    onClick: () -> Unit = {},
) {
    MarkerComposable(
        keys = arrayOf(*key, isFocused),
        state = rememberUpdatedMarkerState(
            position = LatLng(latitude, longitude),
        ),
        captionText = "인생네컷\n강남점",
        captionTextSize = 12.sp,
        captionColor = NekiTheme.colorScheme.gray900,
        onClick = {
            onClick()
            true
        }
    ) {
        BrandMarkerContent(
            brand = brand,
            isFocused = isFocused,
        )
    }
}

@Composable
internal fun BrandMarkerContent(
    modifier: Modifier = Modifier,
    brand: FourCutBrand,
    isFocused: Boolean = false,
) {
    Box(
        modifier = modifier
            .then(
                if (isFocused) {
                    Modifier.height(FOCUSED_MARKER_BACKGROUND_SIZE.dp + MARKER_TRIANGLE_SIZE.dp + 1.dp)
                } else Modifier.height(MARKER_BACKGROUND_SIZE.dp + MARKER_TRIANGLE_SIZE.dp + 1.dp)
            )
            .padding(1.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(MARKER_TRIANGLE_SIZE.dp)
                .buttonShadow(
                    color = Color.Black.copy(alpha = 0.38f),
                    shape = TriangleShape,
                    offsetY = 1.18.dp,
                    blurRadius = 2.55.dp
                )
        )
        Box(
            modifier = Modifier
                .buttonShadow(
                    color = Color.Black.copy(alpha = 0.38f),
                    shape = RoundedCornerShape(
                        if (isFocused) FOCUSED_MARKER_BACKGROUND_RADIUS.dp else MARKER_BACKGROUND_RADIUS.dp
                    ),
                    offsetY = 1.18.dp,
                    blurRadius = 2.55.dp
                )
                .then(
                    if (isFocused) {
                        Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        NekiTheme.colorScheme.gray600,
                                        NekiTheme.colorScheme.gray900
                                    )
                                ),
                                shape = RoundedCornerShape(FOCUSED_MARKER_BACKGROUND_RADIUS.dp)
                            )
                            .padding(FOCUSED_MARKER_PADDING.dp)
                    } else {
                        Modifier
                            .background(
                                color = NekiTheme.colorScheme.white,
                                shape = RoundedCornerShape(MARKER_BACKGROUND_RADIUS.dp)
                            )
                            .padding(MARKER_PADDING.dp)

                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(if (isFocused) FOCUSED_MARKER_IMAGE_SIZE.dp else MARKER_IMAGE_SIZE.dp)
                    .clip(
                        RoundedCornerShape(
                            if (isFocused) FOCUSED_MARKER_IMAGE_RADIUS.dp else MARKER_IMAGE_RADIUS.dp
                        )
                    ),
                model = brand.logoRes,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(MARKER_TRIANGLE_SIZE.dp)
                .background(
                    color = if (isFocused) NekiTheme.colorScheme.gray900 else NekiTheme.colorScheme.white,
                    shape = TriangleShape
                )
        )
    }
}

private val TriangleShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width / 2, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@ComponentPreview
@Composable
private fun BrandMarkerContentPreview() {
    NekiTheme {
        BrandMarkerContent(
            brand = FourCutBrand.LIFE_FOUR_CUT
        )
    }
}

@ComponentPreview
@Composable
private fun BrandMarkerContentSelectedPreview() {
    NekiTheme {
        BrandMarkerContent(
            brand = FourCutBrand.LIFE_FOUR_CUT,
            isFocused = true
        )
    }
}

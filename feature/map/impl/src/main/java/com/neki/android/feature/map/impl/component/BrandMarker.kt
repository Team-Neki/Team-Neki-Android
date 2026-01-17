package com.neki.android.feature.map.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MarkerComposable
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.Marker
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.extension.buttonShadow
import com.neki.android.core.designsystem.extension.clickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.map.impl.const.FourCutBrand

private val IMAGE_BOX_SIZE = 53.6.dp // 49dp + 2.3dp padding * 2
private val IMAGE_SIZE = 49.dp // 49dp + 2.3dp padding * 2
private val TRIANGLE_WIDTH = 10.dp
private val TRIANGLE_HEIGHT = 9.dp

@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun BrandMarker(
    modifier: Modifier = Modifier,
    key: Array<String>,
    latitude: Double,
    longitude: Double,
    brand: FourCutBrand,
    onClick: () -> Unit = {},
) {
    MarkerComposable(
        keys = key,
        state = rememberUpdatedMarkerState(
            position = LatLng(latitude, longitude),
        ),
        onClick = {
            onClick()
            true
        }
    ) {
        BrandMarkerContent(
            brand = brand,
        )
    }
}

@Composable
internal fun BrandMarkerContent(
    modifier: Modifier = Modifier,
    brand: FourCutBrand,
) {
    Box(
        modifier = modifier
            .height(IMAGE_BOX_SIZE + TRIANGLE_HEIGHT - 1.dp)
//            .size(
//                width = IMAGE_BOX_SIZE,
//                height = IMAGE_BOX_SIZE + TRIANGLE_HEIGHT - 1.dp
//            )
            .padding(1.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = TRIANGLE_WIDTH, height = TRIANGLE_HEIGHT)
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
                    shape = RoundedCornerShape(20.dp),
                    offsetY = 1.18.dp,
                    blurRadius = 2.55.dp
                )
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = NekiTheme.colorScheme.white
                )
                .padding(2.3.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(IMAGE_SIZE)
                    .clip(RoundedCornerShape(20.dp)),
                model = brand.logoRes,
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = TRIANGLE_WIDTH, height = TRIANGLE_HEIGHT)
                .background(
                    color = NekiTheme.colorScheme.white,
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

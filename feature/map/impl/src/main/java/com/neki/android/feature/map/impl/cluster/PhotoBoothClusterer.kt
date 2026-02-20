package com.neki.android.feature.map.impl.cluster

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.res.ResourcesCompat
import com.naver.maps.geometry.LatLng
import com.neki.android.core.designsystem.R as DesignSystemR
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.NaverMap
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.neki.android.core.model.PhotoBooth
import androidx.core.graphics.toColorInt

/**
 * PhotoBooth 마커 클러스터링을 위한 Clusterer 빌더
 *
 * 참고: https://velog.io/@mraz3068/Android-NaverMap-Compose-Clurstering-Implementation
 */
internal object PhotoBoothClusterer {

    private const val MIN_CLUSTERING_ZOOM = 9
    private const val MAX_CLUSTERING_ZOOM = 16
    private const val MAX_SCREEN_DISTANCE = 200.0

    // 클러스터 마커 스타일 (Figma: btn_pin_map)
    private const val CLUSTER_TEXT_SIZE = 20f // title20SemiBold
    private const val CLUSTER_LETTER_SPACING = -0.02f // letterSpacing: -0.02em
    private const val CLUSTER_CORNER_RADIUS = 18f
    private const val CLUSTER_BOX_SIZE = 54f // 테두리 포함 고정 크기
    private const val CLUSTER_STROKE_WIDTH = 2f // 흰색 테두리
    // pinShadow: offset (0, 1), blur 2.5, alpha 0.40
    private const val PIN_SHADOW_OFFSET_Y = 1f
    private const val PIN_SHADOW_BLUR = 2.5f
    private const val PIN_SHADOW_ALPHA = 0.40f

    /**
     * 클러스터 매니저 생성
     *
     * 클러스터 마커와 개별(leaf) 마커 모두 Clusterer가 관리합니다.
     * Composable 마커는 사용하지 않습니다.
     *
     * @param getBrandImage 브랜드 이미지 캐시에서 이미지를 가져오는 함수
     */
    fun create(
        context: Context,
        naverMap: NaverMap,
        onClusterClick: (LatLngBounds) -> Unit,
        onLeafMarkerClick: (PhotoBooth) -> Unit,
        getBrandImage: (String) -> ImageBitmap?,
    ): Clusterer<PhotoBoothClusterItem> {
        return Clusterer.ComplexBuilder<PhotoBoothClusterItem>()
            .minClusteringZoom(MIN_CLUSTERING_ZOOM)
            .maxClusteringZoom(MAX_CLUSTERING_ZOOM)
            .maxScreenDistance(MAX_SCREEN_DISTANCE)
            .tagMergeStrategy { cluster ->
                // 클러스터에 포함된 모든 PhotoBooth를 List로 병합
                cluster.children.flatMap { child ->
                    when (val tag = child.tag) {
                        is PhotoBooth -> listOf(tag)
                        is List<*> -> tag.filterIsInstance<PhotoBooth>()
                        else -> emptyList()
                    }
                }
            }
            .clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
                override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                    // 마커는 재사용되기 때문에 줌인/줌아웃 시 marker 속성 초기화
                    marker.apply {
                        captionText = ""
                        subCaptionText = ""
                        icon = createClusterIcon(context, info.size)
                        anchor = Marker.DEFAULT_ANCHOR
                    }

                    marker.setOnClickListener {
                        @Suppress("UNCHECKED_CAST")
                        val photoBooths = info.tag as? List<PhotoBooth> ?: emptyList()
                        if (photoBooths.isNotEmpty()) {
                            val bounds = LatLngBounds.Builder()
                                .include(photoBooths.map { LatLng(it.latitude, it.longitude) })
                                .build()
                            onClusterClick(bounds)
                        }
                        true
                    }
                }
            })
            .leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
                override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                    val clusterItem = info.key as PhotoBoothClusterItem
                    val photoBooth = clusterItem.photoBooth
                    val brandImage = getBrandImage(photoBooth.imageUrl)

                    // 마커는 재사용되기 때문에 줌인/줌아웃 시 marker 속성 초기화
                    marker.apply {
                        subCaptionText = ""
                        icon = createLeafMarkerIcon(context, brandImage, photoBooth.isFocused)
                        captionText = "${photoBooth.brandName}\n${photoBooth.branchName}"
                        anchor = Marker.DEFAULT_ANCHOR
                        tag = photoBooth
                    }

                    marker.setOnClickListener {
                        onLeafMarkerClick(photoBooth)
                        true
                    }
                }
            })
            .build()
            .apply { this.map = naverMap }
    }

    /**개별 마커 아이콘 생성**/
    private fun createLeafMarkerIcon(
        context: Context,
        brandImage: ImageBitmap?,
        isFocused: Boolean,
    ): OverlayImage {
        val density = context.resources.displayMetrics.density
        val imageSize = if (isFocused) 60 else 50
        val padding = if (isFocused) 6 else 2
        val cornerRadius = if (isFocused) 26 else 20
        val triangleHeight = 10
        val shadowPadding = 4 // 그림자를 위한 여백

        val bodyWidth = imageSize + padding * 2
        val bodyHeight = imageSize + padding * 2
        val totalWidth = ((bodyWidth + shadowPadding * 2) * density).toInt()
        val totalHeight = ((bodyHeight + triangleHeight + shadowPadding) * density).toInt()

        val bitmap = android.graphics.Bitmap.createBitmap(
            totalWidth,
            totalHeight,
            android.graphics.Bitmap.Config.ARGB_8888,
        )
        val canvas = android.graphics.Canvas(bitmap)

        val offsetX = shadowPadding * density
        val offsetY = shadowPadding * density / 2

        // 그림자 Paint
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = Color.TRANSPARENT
            setShadowLayer(
                4 * density,  // blur radius
                0f,           // dx
                2 * density,  // dy
                Color.parseColor("#40000000"),  // shadow color
            )
        }

        // 배경 사각형 (둥근 모서리)
        val backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = if (isFocused) Color.parseColor("#333333") else Color.WHITE
            style = Paint.Style.FILL
        }

        val bodyRect = android.graphics.RectF(
            offsetX,
            offsetY,
            offsetX + bodyWidth * density,
            offsetY + bodyHeight * density,
        )

        // 그림자 그리기
        canvas.drawRoundRect(bodyRect, cornerRadius * density, cornerRadius * density, shadowPaint)
        // 배경 그리기
        canvas.drawRoundRect(bodyRect, cornerRadius * density, cornerRadius * density, backgroundPaint)

        // 삼각형 꼬리
        val trianglePath = android.graphics.Path().apply {
            val triangleWidth = 12 * density
            val startX = offsetX + (bodyWidth * density - triangleWidth) / 2
            val topY = offsetY + bodyHeight * density - 1
            moveTo(startX, topY)
            lineTo(startX + triangleWidth, topY)
            lineTo(offsetX + bodyWidth * density / 2, offsetY + bodyHeight * density + triangleHeight * density)
            close()
        }
        // 삼각형 그림자
        canvas.drawPath(trianglePath, shadowPaint)
        // 삼각형 배경
        canvas.drawPath(trianglePath, backgroundPaint)

        // 브랜드 이미지
        val imageLeft = offsetX + padding * density
        val imageTop = offsetY + padding * density
        val imageRight = imageLeft + imageSize * density
        val imageBottom = imageTop + imageSize * density
        val imageRadius = (if (isFocused) 21 else 18) * density

        if (brandImage != null) {
            val imagePaint = Paint().apply { isAntiAlias = true }
            val srcBitmap = brandImage.asAndroidBitmap()

            // 이미지 둥근 모서리 클리핑
            val imagePath = android.graphics.Path().apply {
                addRoundRect(
                    android.graphics.RectF(imageLeft, imageTop, imageRight, imageBottom),
                    imageRadius,
                    imageRadius,
                    android.graphics.Path.Direction.CW,
                )
            }
            canvas.save()
            canvas.clipPath(imagePath)
            canvas.drawBitmap(
                srcBitmap,
                null,
                android.graphics.RectF(imageLeft, imageTop, imageRight, imageBottom),
                imagePaint,
            )
            canvas.restore()
        } else {
            // placeholder 이미지 (회색 박스)
            val placeholderPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#EEEEEE")
            }
            canvas.drawRoundRect(
                android.graphics.RectF(imageLeft, imageTop, imageRight, imageBottom),
                imageRadius,
                imageRadius,
                placeholderPaint,
            )
        }

        return OverlayImage.fromBitmap(bitmap)
    }

    /**클러스터 마커 아이콘 생성 (둥근 사각형 배경 + 숫자)**/
    private fun createClusterIcon(context: Context, count: Int): OverlayImage {
        val density = context.resources.displayMetrics.density

        // 배경 크기 (고정 54dp)
        val shadowPadding = 4 * density
        val bgSize = CLUSTER_BOX_SIZE * density
        val totalSize = (bgSize + shadowPadding * 2).toInt()

        // 비트맵 생성
        val bitmap = android.graphics.Bitmap.createBitmap(
            totalSize,
            totalSize,
            android.graphics.Bitmap.Config.ARGB_8888,
        )
        val canvas = android.graphics.Canvas(bitmap)

        // 그림자 Paint (pinShadow 스타일: offset (0, 1), blur 2.5, alpha 0.40)
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = Color.TRANSPARENT
            setShadowLayer(
                PIN_SHADOW_BLUR * density,
                0f,
                PIN_SHADOW_OFFSET_Y * density,
                Color.argb((255 * PIN_SHADOW_ALPHA).toInt(), 0, 0, 0),
            )
        }

        // 배경 Paint (NekiColors.primary400: #FF5647)
        val backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FF5647")
            style = Paint.Style.FILL
        }

        // 테두리 Paint (흰색 2px)
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            this.strokeWidth = CLUSTER_STROKE_WIDTH * density
        }

        // 배경 영역 (정사각형 54dp)
        val bgRect = android.graphics.RectF(
            shadowPadding,
            shadowPadding,
            shadowPadding + bgSize,
            shadowPadding + bgSize,
        )

        val cornerRadius = CLUSTER_CORNER_RADIUS * density

        // 텍스트 크기 및 Paint 설정 (title20SemiBold 스타일)
        val textPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = CLUSTER_TEXT_SIZE * density
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            typeface = ResourcesCompat.getFont(context, DesignSystemR.font.pretendard_semibold)
            letterSpacing = CLUSTER_LETTER_SPACING
        }

        val textHeight = textPaint.descent() - textPaint.ascent()
        val textX = shadowPadding + bgSize / 2
        val textY = shadowPadding + bgSize / 2 + (textHeight / 2 - textPaint.descent())
        val text = if (count > 99) "99+" else count.toString()

        canvas.apply {
            drawRoundRect(bgRect, cornerRadius, cornerRadius, shadowPaint)
            drawRoundRect(bgRect, cornerRadius, cornerRadius, backgroundPaint)
            drawRoundRect(bgRect, cornerRadius, cornerRadius, strokePaint)
            drawText(text, textX, textY, textPaint)
        }

        return OverlayImage.fromBitmap(bitmap)
    }
}

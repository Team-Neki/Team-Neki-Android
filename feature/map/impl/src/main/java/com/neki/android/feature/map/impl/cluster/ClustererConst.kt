package com.neki.android.feature.map.impl.cluster

internal object ClustererConst {
    // 클러스터링 설정
    const val MIN_CLUSTERING_ZOOM = 9 // 클러스터링 시작 줌 레벨
    const val MAX_CLUSTERING_ZOOM = 16 // 클러스터링 종료 줌 레벨 (이 이상이면 개별 마커 표시)
    const val MAX_SCREEN_DISTANCE = 200.0 // 클러스터로 묶이는 최대 화면 거리 (px)

    // 클러스터 마커 스타일 (Figma: btn_pin_map)
    const val CLUSTER_TEXT_SIZE = 20f // title20SemiBold
    const val CLUSTER_LETTER_SPACING = -0.02f // letterSpacing: -0.02em
    const val CLUSTER_CORNER_RADIUS = 18f // 둥근 모서리 반경 (dp)
    const val CLUSTER_BOX_SIZE = 54f // 테두리 포함 고정 크기 (dp)
    const val CLUSTER_STROKE_WIDTH = 2f // 흰색 테두리 두께 (dp)

    // clusterShadow: offset (0, 1), blur 2.5, alpha 0.40
    const val CLUSTER_SHADOW_OFFSET_Y = 1f // 그림자 Y축 오프셋 (dp)
    const val CLUSTER_SHADOW_BLUR = 2.5f // 그림자 블러 반경 (dp)
    const val CLUSTER_SHADOW_ALPHA = 0.40f // 그림자 투명도 (0.0 ~ 1.0)

    // leafShadow: offset (0, 2), blur 4, alpha 0.25
    const val LEAF_SHADOW_OFFSET_Y = 2f // 그림자 Y축 오프셋 (dp)
    const val LEAF_SHADOW_BLUR = 4f // 그림자 블러 반경 (dp)
    const val LEAF_SHADOW_ALPHA = 0.25f // 그림자 투명도 (0.0 ~ 1.0)
}

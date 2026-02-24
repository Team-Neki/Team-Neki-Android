package com.neki.android.feature.map.impl.cluster

internal object ClustererConst {
    // 클러스터링 설정
    const val MIN_CLUSTERING_ZOOM = 9 // 클러스터링 시작 줌 레벨
    const val MAX_CLUSTERING_ZOOM = 16 // 클러스터링 종료 줌 레벨 (이 이상이면 개별 마커 표시)
    const val MAX_SCREEN_DISTANCE = 200.0 // 클러스터로 묶이는 최대 화면 거리 (px)

    // 클러스터 마커 스타일
    const val CLUSTER_TEXT_SIZE = 20f // title20SemiBold
    const val CLUSTER_LETTER_SPACING = -0.02f // letterSpacing: -0.02em
    const val CLUSTER_CORNER_RADIUS = 18f // 둥근 모서리 반경 (dp)
    const val CLUSTER_BOX_SIZE = 54f // 테두리 포함 고정 크기 (dp)
    const val CLUSTER_STROKE_WIDTH = 2f // 흰색 테두리 두께 (dp)
    const val CLUSTER_BACKGROUND_COLOR = "#FF5647" // NekiColors.primary400
    const val CLUSTER_COUNT_THRESHOLD = 100 // 99+ 표시 임계값

    // clusterShadow: offset (0, 1), blur 2.5, alpha 0.40
    const val CLUSTER_SHADOW_OFFSET_Y = 1f // 그림자 Y축 오프셋 (dp)
    const val CLUSTER_SHADOW_BLUR = 2.5f // 그림자 블러 반경 (dp)
    const val CLUSTER_SHADOW_ALPHA = 0.40f // 그림자 투명도 (0.0 ~ 1.0)

    // 개별 마커(Leaf) 일반 상태
    const val LEAF_NORMAL_IMAGE_SIZE = 50f // 이미지 크기 (dp)
    const val LEAF_NORMAL_PADDING = 2f // 패딩 (dp)
    const val LEAF_NORMAL_CORNER_RADIUS = 20f // 배경 둥근 모서리 반경 (dp)
    const val LEAF_NORMAL_IMAGE_RADIUS = 18f // 이미지 둥근 모서리 반경 (dp)

    // 개별 마커(Leaf) 포커스 상태
    const val LEAF_FOCUSED_IMAGE_SIZE = 60f // 이미지 크기 (dp)
    const val LEAF_FOCUSED_PADDING = 6f // 패딩 (dp)
    const val LEAF_FOCUSED_CORNER_RADIUS = 26f // 배경 둥근 모서리 반경 (dp)
    const val LEAF_FOCUSED_IMAGE_RADIUS = 21f // 이미지 둥근 모서리 반경 (dp)

    // 개별 마커(Leaf) 삼각형 꼬리
    const val LEAF_TRIANGLE_WIDTH = 12f // 삼각형 너비 (dp)
    const val LEAF_TRIANGLE_HEIGHT = 10f // 삼각형 높이 (dp)

    // leafShadow: offset (0, 2), blur 4, alpha 0.25
    const val LEAF_SHADOW_OFFSET_Y = 2f // 그림자 Y축 오프셋 (dp)
    const val LEAF_SHADOW_BLUR = 4f // 그림자 블러 반경 (dp)
    const val LEAF_SHADOW_ALPHA = 0.25f // 그림자 투명도 (0.0 ~ 1.0)

    // 개별 마커(Leaf) 색상
    const val LEAF_FOCUSED_GRADIENT_START = "#616575" // 선택된 마커 gray600 (그라데이션 시작)
    const val LEAF_FOCUSED_GRADIENT_END = "#202227" // 선택된 마커 gray900 (그라데이션 끝)

    // 공통
    const val SHADOW_PADDING = 4f // 그림자 패딩 (px)
}

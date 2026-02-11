package com.neki.android.core.common.permission

enum class NekiPermission(
    val title: String,
    val subTitle: String,
    val dialogContent: String,
) {
    CAMERA(
        title = "카메라",
        subTitle = "QR 촬영에 필요해요.",
        dialogContent = "QR 인식을 위해 카메라 접근이 필요해요",
    ),
    LOCATION(
        title = "위치",
        subTitle = "주변 포토부스 탐색에 필요해요.",
        dialogContent = "주변 포토부스를 찾기 위해 \n위치 사용 권한이 필요해요",
    ),
    NOTIFICATION(
        title = "알림",
        subTitle = "저장 사진 및 추억 리마인드에 필요해요.",
        dialogContent = "사진 저장 완료·오류 안내를 알려드리기 위해\n알림 권한이 필요해요",
    ),
}

package com.neki.android.feature.mypage.impl.permission.const

enum class NekiPermission(
    val title: String,
    val subTitle: String,
    val dialogContent: String,
) {
    CAMERA(
        title = "카메라",
        subTitle = "QR 촬영에 필요해요.",
        dialogContent = "카메라 권한을 허용해주세요.",
    ),
    LOCATION(
        title = "위치",
        subTitle = "주변 포토부스 탐색에 필요해요.",
        dialogContent = "위치 권한을 허용해주세요.",
    ),
    NOTIFICATION(
        title = "알림",
        subTitle = "저장 사진 및 추억 리마인드에 필요해요.",
        dialogContent = "알림 권한을 허용해주세요.",
    ),
}

package com.neki.android.feature.mypage.impl.main.const

enum class ServiceInfoMenu(
    val text: String,
    val url: String,
) {
    INQUIRY(
        text = "Neki에 문의하기",
        url = "https://www.naver.com",
    ),
    TERMS_OF_SERVICE(
        text = "이용약관",
        url = "https://lydian-tip-26b.notion.site/2ee0d9441db0807c8684ce3e2d4b8aca?source=copy_link",
    ),
    PRIVACY_POLICY(
        text = "개인정보 처리방침",
        url = "https://lydian-tip-26b.notion.site/2ee0d9441db0807cb850f78145db6dd3?pvs=74",
    ),
    OPEN_SOURCE_LICENSE(
        text = "오픈소스 라이선스",
        url = "https://www.naver.com",
    ),
}

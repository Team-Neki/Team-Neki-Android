package com.neki.android.feature.auth.impl.term.model

import kotlinx.collections.immutable.toPersistentSet

enum class TermAgreement(
    val title: String,
    val isRequired: Boolean,
    val url: String,
) {
    SERVICE_TERMS(
        title = "서비스 이용 약관",
        isRequired = true,
        url = "https://lydian-tip-26b.notion.site/2ee0d9441db0807c8684ce3e2d4b8aca?source=copy_link",
    ),
    PRIVACY_POLICY(
        title = "개인정보 수집/이용 동의",
        isRequired = true,
        url = "https://lydian-tip-26b.notion.site/2ee0d9441db0807cb850f78145db6dd3?pvs=74",
    ),
    LOCATION_POLICY(
        title = "위치정보 수집 및 이용 동의",
        isRequired = true,
        url = "https://lydian-tip-26b.notion.site/2ee0d9441db080b48223fb0b3263da08?pvs=74",
    );

    companion object {
        val allRequiredTerms = entries.filter { it.isRequired }.toPersistentSet()
    }
}

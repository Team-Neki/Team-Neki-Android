package com.neki.android.core.common.permission

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 자동 안내(업데이트 후 첫 진입 등)에서 띄울 알림 안내 모달 종류
 */
enum class NotificationGuideType {
    /** OS 알림 꺼짐 + 이벤트·혜택 미동의: 동의 저장 후 OS 알림 팝업 */
    INTEGRATED,

    /** OS 알림 꺼짐 + 이벤트·혜택 동의: OS 알림 팝업 */
    ENABLE_NOTIFICATION,

    /** OS 알림 켜짐 + 이벤트·혜택 미동의: 동의만 저장 */
    MARKETING_CONSENT,

    /** OS 알림 팝업을 더 띄울 수 없음: 설정 이동 (미동의면 동의도 저장) */
    OPEN_SETTINGS,
    ;

    companion object {
        /**
         * OS 알림 권한과 이벤트·혜택 알림 동의 상태로 띄울 모달을 고른다.
         * 둘 다 켜져 있으면 안내하지 않으므로 null을 반환한다.
         *
         * @param isNotificationGranted OS 알림 권한 허용 여부
         * @param isMarketingAgreed 이벤트·혜택 알림 수신 동의 여부
         * @param canRequestNotificationPermission OS 알림 팝업을 아직 띄울 수 있는지 여부
         */
        fun resolve(
            isNotificationGranted: Boolean,
            isMarketingAgreed: Boolean,
            canRequestNotificationPermission: Boolean,
        ): NotificationGuideType? = when {
            isNotificationGranted && isMarketingAgreed -> null
            isNotificationGranted -> MARKETING_CONSENT
            !canRequestNotificationPermission -> OPEN_SETTINGS
            isMarketingAgreed -> ENABLE_NOTIFICATION
            else -> INTEGRATED
        }
    }
}

private val MARKETING_AGREEMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM.dd")

/**
 * 이벤트·혜택 알림 동의 안내 토스트 문구
 *
 * 예시: "10.20 이벤트·혜택 알림에 동의했어요."
 */
fun marketingAgreementToastMessage(agreedDate: LocalDate = LocalDate.now()): String =
    "${agreedDate.format(MARKETING_AGREEMENT_DATE_FORMATTER)} 이벤트·혜택 알림에 동의했어요."

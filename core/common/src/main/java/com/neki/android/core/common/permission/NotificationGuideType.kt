package com.neki.android.core.common.permission

import android.os.Build
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

    /** OS 알림 팝업을 더 띄울 수 없음 + 이벤트·혜택 동의: 설정 이동 */
    OPEN_SETTINGS,

    /** OS 알림 팝업을 더 띄울 수 없음 + 이벤트·혜택 미동의: 동의 저장 후 설정 이동 */
    OPEN_SETTINGS_WITH_CONSENT,
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
            !canRequestNotificationPermission && isMarketingAgreed -> OPEN_SETTINGS
            !canRequestNotificationPermission -> OPEN_SETTINGS_WITH_CONSENT
            isMarketingAgreed -> ENABLE_NOTIFICATION
            else -> INTEGRATED
        }

        /**
         * 업데이트 후 첫 진입 안내를 판정할지 결정한다.
         * 오늘 이미 자동 안내를 띄웠거나 이번 버전에서 이미 판정했으면 판정하지 않는다.
         */
        fun shouldCheckUpdateGuide(
            todayEpochDay: Long,
            lastAutoGuideEpochDay: Long,
            currentVersionCode: Long,
            checkedVersionCode: Long,
        ): Boolean = todayEpochDay != lastAutoGuideEpochDay && currentVersionCode != checkedVersionCode

        /**
         * OS 알림 팝업을 아직 띄울 수 있는지 판정한다.
         * Android 13 미만은 런타임 권한이 없어 팝업을 띄울 수 없다.
         * Android 13 이상은 거부를 2번 하면 rationale이 false가 되므로, 요청 이력이 있는데 rationale이 false면 불가로 본다.
         */
        fun canRequestNotificationPermission(
            sdkInt: Int,
            hasRequestedBefore: Boolean,
            shouldShowRationale: Boolean,
        ): Boolean = sdkInt >= Build.VERSION_CODES.TIRAMISU && (!hasRequestedBefore || shouldShowRationale)
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

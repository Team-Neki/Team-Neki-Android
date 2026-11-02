package com.neki.android.core.common.permission

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class NotificationGuideTypeTest {

    @Test
    fun `OS 알림과 이벤트·혜택 동의가 모두 켜져 있으면 안내하지 않는다`() {
        listOf(true, false).forEach { canRequest ->
            val result = NotificationGuideType.resolve(
                isNotificationGranted = true,
                isMarketingAgreed = true,
                canRequestNotificationPermission = canRequest,
            )

            assertNull(result)
        }
    }

    @Test
    fun `OS 알림이 켜져 있고 미동의면 동의만 묻는 모달을 띄운다`() {
        listOf(true, false).forEach { canRequest ->
            val result = NotificationGuideType.resolve(
                isNotificationGranted = true,
                isMarketingAgreed = false,
                canRequestNotificationPermission = canRequest,
            )

            assertEquals(NotificationGuideType.MARKETING_CONSENT, result)
        }
    }

    @Test
    fun `OS 알림이 꺼져 있고 미동의면 통합 모달을 띄운다`() {
        val result = NotificationGuideType.resolve(
            isNotificationGranted = false,
            isMarketingAgreed = false,
            canRequestNotificationPermission = true,
        )

        assertEquals(NotificationGuideType.INTEGRATED, result)
    }

    @Test
    fun `OS 알림이 꺼져 있고 동의했으면 알림 켜기 모달을 띄운다`() {
        val result = NotificationGuideType.resolve(
            isNotificationGranted = false,
            isMarketingAgreed = true,
            canRequestNotificationPermission = true,
        )

        assertEquals(NotificationGuideType.ENABLE_NOTIFICATION, result)
    }

    @Test
    fun `OS 알림이 꺼져 있고 OS 팝업을 띄울 수 없으면 동의 여부와 관계없이 설정 모달을 띄운다`() {
        listOf(true, false).forEach { isMarketingAgreed ->
            val result = NotificationGuideType.resolve(
                isNotificationGranted = false,
                isMarketingAgreed = isMarketingAgreed,
                canRequestNotificationPermission = false,
            )

            assertEquals(NotificationGuideType.OPEN_SETTINGS, result)
        }
    }

    @Test
    fun `동의 토스트 문구는 MM_dd 형식의 동의 날짜를 포함한다`() {
        val message = marketingAgreementToastMessage(LocalDate.of(2026, 1, 5))

        assertEquals("01.05 이벤트·혜택 알림에 동의했어요.", message)
    }
}

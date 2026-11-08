package com.neki.android.core.common.permission

import android.os.Build
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun `OS 알림이 꺼져 있고 OS 팝업을 띄울 수 없으며 동의했으면 설정 모달을 띄운다`() {
        val result = NotificationGuideType.resolve(
            isNotificationGranted = false,
            isMarketingAgreed = true,
            canRequestNotificationPermission = false,
        )

        assertEquals(NotificationGuideType.OPEN_SETTINGS, result)
    }

    @Test
    fun `OS 알림이 꺼져 있고 OS 팝업을 띄울 수 없으며 미동의면 동의를 포함한 설정 모달을 띄운다`() {
        val result = NotificationGuideType.resolve(
            isNotificationGranted = false,
            isMarketingAgreed = false,
            canRequestNotificationPermission = false,
        )

        assertEquals(NotificationGuideType.OPEN_SETTINGS_WITH_CONSENT, result)
    }

    @Test
    fun `오늘 자동 안내를 띄우지 않았고 이번 버전에서 판정하지 않았으면 업데이트 안내를 판정한다`() {
        val result = NotificationGuideType.shouldCheckUpdateGuide(
            todayEpochDay = 100L,
            lastAutoGuideEpochDay = 99L,
            currentVersionCode = 39L,
            checkedVersionCode = 38L,
        )

        assertTrue(result)
    }

    @Test
    fun `오늘 이미 자동 안내를 띄웠으면 업데이트 안내를 판정하지 않는다`() {
        val result = NotificationGuideType.shouldCheckUpdateGuide(
            todayEpochDay = 100L,
            lastAutoGuideEpochDay = 100L,
            currentVersionCode = 39L,
            checkedVersionCode = 38L,
        )

        assertFalse(result)
    }

    @Test
    fun `이번 버전에서 이미 판정했으면 업데이트 안내를 판정하지 않는다`() {
        val result = NotificationGuideType.shouldCheckUpdateGuide(
            todayEpochDay = 100L,
            lastAutoGuideEpochDay = 99L,
            currentVersionCode = 39L,
            checkedVersionCode = 39L,
        )

        assertFalse(result)
    }

    @Test
    fun `Android 13 미만은 OS 알림 팝업을 띄울 수 없다`() {
        val result = NotificationGuideType.canRequestNotificationPermission(
            sdkInt = Build.VERSION_CODES.S_V2,
            hasRequestedBefore = false,
            shouldShowRationale = false,
        )

        assertFalse(result)
    }

    @Test
    fun `Android 13 이상에서 요청한 적이 없으면 OS 알림 팝업을 띄울 수 있다`() {
        val result = NotificationGuideType.canRequestNotificationPermission(
            sdkInt = Build.VERSION_CODES.TIRAMISU,
            hasRequestedBefore = false,
            shouldShowRationale = false,
        )

        assertTrue(result)
    }

    @Test
    fun `Android 13 이상에서 1번 거부해 rationale이 true면 OS 알림 팝업을 띄울 수 있다`() {
        val result = NotificationGuideType.canRequestNotificationPermission(
            sdkInt = Build.VERSION_CODES.TIRAMISU,
            hasRequestedBefore = true,
            shouldShowRationale = true,
        )

        assertTrue(result)
    }

    @Test
    fun `Android 13 이상에서 요청 이력이 있고 rationale이 false면 OS 알림 팝업을 띄울 수 없다`() {
        val result = NotificationGuideType.canRequestNotificationPermission(
            sdkInt = Build.VERSION_CODES.TIRAMISU,
            hasRequestedBefore = true,
            shouldShowRationale = false,
        )

        assertFalse(result)
    }

    @Test
    fun `동의 토스트 문구는 MM_dd 형식의 동의 날짜를 포함한다`() {
        val message = marketingAgreementToastMessage(LocalDate.of(2026, 1, 5))

        assertEquals("01.05 이벤트·혜택 알림에 동의했어요.", message)
    }
}

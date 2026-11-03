---
name: implementing-analytics
description: Use when adding or changing Amplitude Analytics events, AnalyticsEvent definitions, AnalyticsLogger calls, screen view logs, click logs, filter logs, or completion logs in this Android repository.
---

# Implementing Analytics

analytics 구현은 `core:analytics`의 이벤트 정의와 feature ViewModel의 로깅 호출을 함께 맞춘다.
이벤트는 `NekiAnalyticsLogger`를 통해 Amplitude와 서버(`POST /api/logs`)로 함께 전달한다.

## 작업 범위

- `core/analytics/src/main/kotlin/com/neki/android/core/analytics/event/*AnalyticsEvent.kt`
- `core/analytics/src/main/kotlin/com/neki/android/core/analytics/logger/NekiAnalyticsLogger.kt`
- analytics를 호출하는 `feature/<feature-name>/impl/src/main/.../*ViewModel.kt`
- 화면 진입 로그 호출 지점이 있는 `feature/<feature-name>/impl/src/main/.../*Screen.kt`
- 앱 수준 이벤트(노티피케이션 클릭 등)를 로깅하는 `app/src/main/java/com/neki/android/app/MainActivity.kt`
- NEKI_DEV와 NEKI_PROD의 Amplitude tracking plan

## 구현 흐름

이벤트 정의 작업과 서버 전송 작업(logger 구현, 로그 저장·전송)이 함께 있으면 이벤트 정의와 ViewModel 로깅 호출부터 끝낸 뒤 서버 전송 작업을 한다.

1. 이벤트가 속한 기능 단위 `*AnalyticsEvent`를 고른다.
2. parameter 없는 이벤트는 `data object`, parameter 있는 이벤트는 `data class`로 추가한다.
3. event `name`과 parameter key는 snake_case로 둔다. 클래스명이 곧 이벤트명은 아니므로(`Logout` → `"mypage_logout"`) 도메인별 기존 네이밍 관례를 따른다.
4. ViewModel에 `AnalyticsLogger`를 주입한다.
5. 로그가 발생하는 trigger에 맞춰 intent 분기, private handler, API 성공 분기에 `analyticsLogger.log(...)`를 둔다.
6. 화면 진입 로그가 composition 진입 기준이면 Route의 `LaunchedEffect(Unit)`에서 ViewModel의 `log<이벤트명>()` 함수만 호출한다(`logMapView`, `logPoseView`, `logArchivingView`). ViewModel 생성 기준이면 `init` 블록에서 로깅한다(`PhotoDetailView`).
7. 신규 로그인과 토큰 기반 자동 로그인 성공 시 `setUserId(...)`, 로그아웃·탈퇴·인증 만료 시 `clearUserId()`로 사용자 식별을 관리한다.
8. 새 event나 parameter key를 추가하면 NEKI_DEV와 NEKI_PROD Amplitude tracking plan에 같은 이름, 유형, 필수 여부를 등록한다.

## Logger

`AnalyticsLogger` 구현체는 `NekiAnalyticsLogger`다. 이벤트마다 `eventId`와 발생 시각을 한 번 만들어 `AmplitudeAnalyticsLogger`와 `ServerAnalyticsLogger`에 같이 넘긴다.
`AmplitudeAnalyticsLogger`는 `AnalyticsEvent.name`과 `AnalyticsEvent.params`를 Amplitude `track(...)`으로 전달하고, `eventId`·발생 시각을 `insert_id`·`timestamp`로 넣는다.
`ServerAnalyticsLogger`는 같은 이벤트를 영속 큐에 저장한 뒤 서버로 보낸다. `sessionId`는 `AmplitudeSessionPlugin`이 넘겨주는 Amplitude 세션을 쓴다.
feature ViewModel은 `AnalyticsLogger`를 주입받아 호출한다.

## Event 정의

```kotlin
sealed interface FeatureAnalyticsEvent : AnalyticsEvent {

    data object FeatureView : FeatureAnalyticsEvent {
        override val name = "feature_view"
    }

    data class ConfirmClick(
        val source: String,
        val selectedCount: Int,
    ) : FeatureAnalyticsEvent {
        override val name = "confirm_click"
        override val params = mapOf(
            "source" to source,
            "selected_count" to selectedCount,
        )
    }
}
```

Amplitude event property value는 `String`, `Int`, `Long`, `Double`, `Boolean`을 쓴다.
tracking plan의 Enum 값과 Number 범위가 있으면 코드의 실제 값도 동일해야 한다.

## 대표 로깅 케이스

|구분|예시|
|---|---|
|화면 진입|`MapView`, `PoseView`, `ArchivingView`, `PhotoDetailView`|
|앱 진입|`AppOpen`, `NotificationClick`(MainActivity에서 로깅)|
|버튼/CTA|`MapRouteClick`, `PoseBookmark`, `Logout`, `Withdraw`|
|검색/필터/선택|`MapReSearch`, `MapBrandFilterToggle`, `PoseFilterToggle`, `BoothSelect`|
|작업 완료|`PhotoUpload`, `AlbumCreate`, `AlbumAddFromDetail`, `AlbumAddFromMulti`, `PhotoMemoCreate`|
|이동/복사|`PhotoMove`, `PhotoCopy`|

## ViewModel 로깅

```kotlin
@HiltViewModel
internal class FeatureViewModel @Inject constructor(
    private val analyticsLogger: AnalyticsLogger,
) : ViewModel()
```

```kotlin
when (intent) {
    FeatureIntent.ClickConfirmButton -> {
        analyticsLogger.log(FeatureAnalyticsEvent.ConfirmClick(...))
        ...
    }
}
```

```kotlin
repository.request(...)
    .onSuccess {
        analyticsLogger.log(FeatureAnalyticsEvent.FeatureComplete)
        ...
    }
```

## 사용자 속성

로그인 성공 시 서버의 안정적인 사용자 ID를 설정한다. 토큰을 보유한 자동 로그인 경로는 새 토큰을 저장한 뒤 사용자 정보를 조회해 `app_open`보다 먼저 user ID를 복원하며, 조회 실패가 앱 진입을 막지는 않게 한다. Amplitude SDK가 `Platform`과 `Version`을 기본 수집하므로 같은 의미의 커스텀 사용자 속성을 중복 설정하지 않는다.

로그아웃 이벤트는 기존 사용자 ID로 먼저 전송하고, 로그아웃 성공 후 `clearUserId()`를 호출한다. 회원 탈퇴 성공과 refresh token 만료에서도 사용자 ID를 해제한다.

세션은 로그인한 회원 기준이다. `clearUserId()`나 다른 회원으로의 `setUserId(...)`는 Amplitude와 서버 로그에 새 세션을 열고, 로그아웃 상태에서의 로그인은 세션을 잇는다.

회원 탈퇴는 `flush()` → 탈퇴 API → 성공 시 `deletePendingUserEvents()` → `log(Withdraw)` → 토큰 삭제 → `clearUserId()` 순서로 호출한다. 삭제는 탈퇴 이벤트보다 먼저 호출해야 탈퇴 이벤트가 남는다.

```kotlin
analyticsLogger.setUserId(userId.toString())
analyticsLogger.log(MypageAnalyticsEvent.Logout)
analyticsLogger.clearUserId()
```

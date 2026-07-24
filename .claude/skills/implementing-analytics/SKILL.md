---
name: implementing-analytics
description: Use when adding or changing Firebase Analytics events, AnalyticsEvent definitions, AnalyticsLogger calls, screen view logs, click logs, filter logs, or completion logs in this Android repository.
---

# Implementing Analytics

analytics 구현은 `core:analytics`의 이벤트 정의와 feature ViewModel의 로깅 호출을 함께 맞춘다.
이벤트는 `FirebaseAnalyticsLogger`를 통해 Firebase Analytics로 전달한다.

## 작업 범위

- `core/analytics/src/main/kotlin/com/neki/android/core/analytics/event/*AnalyticsEvent.kt`
- `core/analytics/src/main/kotlin/com/neki/android/core/analytics/logger/FirebaseAnalyticsLogger.kt`
- analytics를 호출하는 `feature/<feature-name>/impl/src/main/.../*ViewModel.kt`
- 화면 진입 로그 호출 지점이 있는 `feature/<feature-name>/impl/src/main/.../*Screen.kt`
- 앱 수준 이벤트(노티피케이션 클릭 등)를 로깅하는 `app/src/main/java/com/neki/android/app/MainActivity.kt`

## 구현 흐름

1. 이벤트가 속한 기능 단위 `*AnalyticsEvent`를 고른다.
2. parameter 없는 이벤트는 `data object`, parameter 있는 이벤트는 `data class`로 추가한다.
3. event `name`과 parameter key는 snake_case로 둔다. 클래스명이 곧 이벤트명은 아니므로(`Logout` → `"mypage_logout"`) 도메인별 기존 네이밍 관례를 따른다.
4. ViewModel에 `AnalyticsLogger`를 주입한다.
5. 로그가 발생하는 trigger에 맞춰 intent 분기, private handler, API 성공 분기에 `analyticsLogger.log(...)`를 둔다.
6. 화면 진입 로그가 composition 진입 기준이면 Route의 `LaunchedEffect(Unit)`에서 ViewModel의 `log<이벤트명>()` 함수만 호출한다(`logMapView`, `logPoseView`, `logArchivingView`). ViewModel 생성 기준이면 `init` 블록에서 로깅한다(`PhotoDetailView`).
7. 사용자 식별/속성은 `setUserId(...)`, `setUserProperty(...)`를 쓴다.
8. 새 parameter key를 추가하면 GA4 속성에 맞춤 측정기준(custom dimension) 등록이 필요하다. 루트에 `GA4_EVENTS.md`가 있으면 명세도 함께 갱신한다.

## Logger

`FirebaseAnalyticsLogger`는 `AnalyticsEvent`를 Firebase Analytics의 `logEvent(...)`로 전달한다.
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

Firebase Analytics parameter value는 `String`, `Int`, `Long`, `Double`, `Boolean`을 쓴다.
그 외 타입은 컴파일 타임에 걸러지지 않고 `toString()` 문자열로 변환되어 전송되므로 위 5개 타입만 넣는다.

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

`setUserId`와 `platform`은 로그인 성공 시, `app_version`은 스플래시 진입 시 설정한다. 비로그인 이벤트는 user_id가 빈 상태로 수집된다.

```kotlin
analyticsLogger.setUserId(userId.toString())
analyticsLogger.setUserProperty("platform", "android")
analyticsLogger.setUserProperty("app_version", currentAppVersion)
```

---
name: firebase-analytics-event
description: Firebase Analytics 이벤트 로깅 작업을 위한 스킬. 이 스킬은 Neki Android 프로젝트에서 GA 이벤트 정의 및 ViewModel에 로깅 코드를 추가할 때 반드시 사용해야 합니다. "analytics 이벤트", "GA 이벤트", "이벤트 로깅", "firebase analytics", "logEvent", "AnalyticsEvent", "이벤트 추가", "이벤트 정의" 등의 키워드가 포함된 모든 요청에 이 스킬을 적극적으로 활용하세요.
---

# Firebase Analytics 이벤트 로깅 스킬

Neki Android 프로젝트의 Firebase Analytics 이벤트를 정의하고 ViewModel에 로깅 코드를 추가하는 작업을 안내합니다.

## 프로젝트 Analytics 구조

```
core/analytics/
└── src/main/kotlin/com/neki/android/core/analytics/
    ├── event/
    │   ├── AnalyticsEvent.kt           ← 루트 sealed interface
    │   ├── ArchiveAnalyticsEvent.kt    ← 아카이브 이벤트
    │   ├── PoseAnalyticsEvent.kt       ← 포즈 이벤트
    │   ├── MapAnalyticsEvent.kt        ← 지도 이벤트
    │   ├── MypageAnalyticsEvent.kt     ← 마이페이지 이벤트
    │   └── GlobalAnalyticsEvent.kt     ← 앱 전역 이벤트 (app_open, notification_click)
    └── logger/
        ├── AnalyticsLogger.kt          ← 인터페이스 (log, setUserId, setUserProperty)
        ├── FirebaseAnalyticsLogger.kt  ← Firebase 구현체
        └── AnalyticsModule.kt          ← Hilt DI 모듈
```

## Step 1: 이벤트 정의

먼저 해당 피처의 `*AnalyticsEvent.kt`를 읽어 기존 이벤트 구조를 파악한 뒤, 새 이벤트를 추가합니다.

### 이벤트 구조 패턴

```kotlin
// 피처별 최상위 sealed interface
sealed interface ArchiveAnalyticsEvent : AnalyticsEvent {

    // 파라미터 없는 이벤트
    data object ArchivingView : ArchiveAnalyticsEvent {
        override val name = "archiving_view"
    }

    // 파라미터 있는 이벤트
    data class PhotoUpload(val method: String, val count: Int) : ArchiveAnalyticsEvent {
        override val name = "photo_upload"
        override val params = mapOf(
            "method" to method,
            "count" to count,
        )
    }
}
```

### 이벤트 네이밍 규칙
- `name`: snake_case (예: `photo_upload`, `map_view`)
- 파라미터 키: snake_case (예: `photo_count`, `brand_name`)
- 파라미터 값은 Kotlin 원시 타입 그대로 사용 (`String`, `Int`, `Long`, `Double`, `Boolean`)
- `FirebaseAnalyticsLogger`가 타입별로 `putString`/`putInt`/`putBoolean` 등을 자동으로 처리

### 피처별 파일
| 피처 | 파일 | sealed interface |
|------|------|-----------------|
| 아카이브 | `ArchiveAnalyticsEvent.kt` | `ArchiveAnalyticsEvent` |
| 포즈 | `PoseAnalyticsEvent.kt` | `PoseAnalyticsEvent` |
| 네컷지도 | `MapAnalyticsEvent.kt` | `MapAnalyticsEvent` |
| 마이페이지 | `MypageAnalyticsEvent.kt` | `MypageAnalyticsEvent` |
| 앱 전역 | `GlobalAnalyticsEvent.kt` | `GlobalAnalyticsEvent` |

새 피처라면 새로운 `*AnalyticsEvent.kt` 파일을 추가합니다.

## Step 2: ViewModel에 AnalyticsLogger 주입

```kotlin
@HiltViewModel
class SomeViewModel @Inject constructor(
    private val someRepository: SomeRepository,
    private val analyticsLogger: AnalyticsLogger,  // ← 추가
) : ViewModel() {
```

`AnalyticsLogger`는 이미 Hilt에 바인딩되어 있으므로 import 후 생성자에 추가만 하면 됩니다.

필요한 import:
```kotlin
import com.neki.android.core.analytics.event.ArchiveAnalyticsEvent  // 피처에 맞게
import com.neki.android.core.analytics.logger.AnalyticsLogger
```

## Step 3: 이벤트 로깅 위치 결정

### 탭 진입 이벤트 (`*_view`)
탭 복귀 시마다 찍혀야 하므로 ViewModel `init`이 아닌 Route `LaunchedEffect`에서 호출합니다.

```kotlin
// ViewModel에 별도 함수로 노출
fun logSomeView() {
    analyticsLogger.log(SomeAnalyticsEvent.SomeView)
}

// Route Composable에서 호출
@Composable
fun SomeRoute(viewModel: SomeViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.logSomeView()
    }
}
```

### 화면 진입 이벤트 (ViewModel 생성 시 1회)
`init`에서 intent를 호출하고, 해당 intent 핸들러 내부에서 로깅합니다.

```kotlin
init {
    store.onIntent(SomeIntent.EnterSomeScreen)
}

is SomeIntent.EnterSomeScreen -> {
    analyticsLogger.log(SomeAnalyticsEvent.SomeScreenEnter)
    // 나머지 초기화 로직
}
```

### 사용자 액션 이벤트
Intent 처리 내부에서 직접 로깅합니다.

```kotlin
is SomeIntent.ClickSomething -> {
    analyticsLogger.log(SomeAnalyticsEvent.SomeAction(param = intent.value))
    // 나머지 로직
}
```

### reduce 람다 내부 로깅
상태 변경 후 로깅이 필요한 경우 (예: `map_brand_filter_toggle`):
```kotlin
reduce {
    val updatedItems = items.map { ... }
    analyticsLogger.log(SomeAnalyticsEvent.SomeEvent(count = updatedItems.size))
    copy(items = updatedItems)
}
```

## Step 4: 작업 체크리스트

1. [ ] 해당 피처의 `*AnalyticsEvent.kt`에 이벤트 정의 추가
2. [ ] 해당 ViewModel 생성자에 `analyticsLogger: AnalyticsLogger` 추가
3. [ ] 필요한 import 추가
4. [ ] 적절한 위치에 `analyticsLogger.log(...)` 호출 추가
5. [ ] 탭 진입 이벤트라면 ViewModel에 `log*View()` 함수 노출 후 Route에서 `LaunchedEffect(Unit)`으로 호출
6. [ ] 동일 액션을 수행하는 ViewModel이 여러 개인지 확인 후 모두 로깅

## 기존 패턴 참고 파일

- `core/analytics/src/main/kotlin/com/neki/android/core/analytics/event/` (이벤트 정의)
- `feature/map/impl/src/main/java/com/neki/android/feature/map/impl/MapViewModel.kt` (다양한 로깅 패턴)
- `feature/archive/impl/src/main/kotlin/com/neki/android/feature/archive/impl/main/ArchiveMainViewModel.kt` (view 이벤트 패턴)

---

## 이벤트 스펙 레퍼런스

**반드시 작업 전 읽을 것:** [analytics-spec.md](analytics-spec.md)

전체 이벤트 정의, 트리거 시점, 파라미터 값 규칙, 피처별 로깅 위치가 정리되어 있습니다.
새 이벤트 추가 또는 기존 이벤트 수정 시 스펙과 대조하여 일관성을 유지하세요.

### 주요 결정사항

- `FirebaseAnalyticsLogger`가 타입별 `putString`/`putInt`/`putLong`/`putDouble`/`putBoolean`으로 전송 → 파라미터 값은 Kotlin 원시 타입 그대로 사용
- **탭 진입 이벤트** (`*_view`): ViewModel `init`의 `EnterScreen` intent와 분리, Route `LaunchedEffect(Unit)`에서 `log*View()` 함수 호출
- **화면 진입 이벤트** (ViewModel 생성 시 1회): `init`에서 intent 호출 → 핸들러 내부에서 로깅
- analytics 전용 카운터(`totalSwipeCount` 등)는 State가 아닌 ViewModel 필드로 관리
- `app_open`: 로그인/자동 로그인 **완료** 시점에만 로깅
- 동일 액션을 수행하는 ViewModel이 여러 개면 모두 로깅 (예: `album_create` - 3곳)
- `album_add_from_detail` / `album_add_from_multi`: `SelectAlbumViewModel.performAction()` CopyPhotos 성공 시, 사진 수(1개 vs 다수)로 구분
- `photo_add_to_album`: `AlbumDetailViewModel.handleConfirmImport()` Import 성공 시 로깅 (`photo_copy`와 동시에 로깅됨)
- `favorite_booth_view`: 즐겨찾기 마커 ON 전환 시에만 로깅 (OFF 시 미로깅)
- `favorite_booth_filter_toggle`: ON/OFF 모두 로깅, `action`: `on`/`off`
- `booth_favorite_add` / `booth_favorite_remove`: API 성공 콜백에서 로깅 (낙관적 업데이트 아님)

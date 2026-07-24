---
name: implementing-ui
description: Use when adding or changing Compose Screen UI, Preview, feature UI components, dialogs, bottom sheets, loading UI, paging UI, or shared UI placement in this Android repository.
---

# Implementing UI

feature UI는 `<Feature>Screen`, preview, feature component를 같은 Screen 파일 흐름에 맞춘다.

## 작업 범위

- `feature/<feature-name>/impl/src/main/.../*Screen.kt`
- `feature/<feature-name>/impl/src/main/.../component/*`
- 공통 UI 위치 판단: `core:designsystem`, `core:ui`, feature 내부 `component/`

## 구현 흐름

1. loading, dialog, bottom sheet는 Screen 영역에 필요에 따라 둔다.
2. 화면 전용 UI 조각은 feature 내부 `component/`에 둔다.
3. preview는 화면 preview와 component preview를 케이스에 따라 구분해 둔다.
4. paging 목록은 Screen에서 `collectAsLazyPagingItems()`로 수집하고, `LazyPagingItems`를 하위 component에 파라미터로 전달한다.

## Preview

- Screen 전체 레이아웃 preview는 `DevicePreview`를 쓴다. 기존 코드에는 `ComponentPreview`·원시 `Preview`가 혼재하나 신규 코드는 `DevicePreview`로 통일한다.
- 빈 화면, 선택 모드, 에러 상태, 로딩 상태는 케이스에 따라 preview를 나눠 둔다.
- feature 내부 component만 독립적으로 확인하면 component 파일에 component preview를 둔다.
- component preview는 `ComponentPreview`를 쓴다.
- preview function은 private으로 둔다.

## UI 공유 위치

- `core:designsystem`: feature/domain/platform 동작을 모르는 순수 디자인 컴포넌트와 토큰. 예: `NekiTheme`, top bar, action bar, button, dialog, bottom sheet, modifier, preview helper.
- `core:ui`: 앱 전역에서 재사용하지만 기능/도메인/플랫폼 동작이 섞인 UI/Compose 유틸. 예: `MviIntentStore`, `collectWithLifecycle`, `NekiToast`(WindowManager 기반 표시 class — 동명의 순수 composable은 designsystem에 있음), `PhotoComponent`, `SelectDialog`, `DoubleButtonOptionBottomSheet`, loading/selection overlay.
- feature 내부 `component/`: 해당 feature 문맥에서만 의미 있는 UI 조각. 예: archive 전용 album/photo item, pose 전용 bottom sheet, auth 약관 content.

---
name: changing-feature-behavior
description: Use when adding or changing behavior in an existing feature screen, including MVI State, Intent, Effect, ViewModel reducers, one-shot effects, API result handling, or UI event wiring in this Android repository.
---

# Changing Feature Behavior

기존 화면 동작 변경은 Contract, ViewModel, Route/Screen 연결을 함께 맞춘다.

## 작업 범위

- `feature/<feature-name>/impl/src/main/.../*Contract.kt`
- `feature/<feature-name>/impl/src/main/.../*ViewModel.kt`
- `feature/<feature-name>/impl/src/main/.../*Screen.kt`
- 화면 전용 callback을 받는 feature 내부 `component/*`
- `navigation/*EntryProvider.kt` (결과 수신 로직을 수정하는 경우)
- 일부 화면(mypage의 profile 계열 등)은 별도 Contract 없이 상위 화면의 ViewModel/Contract를 공유하므로, effect 추가 시 이를 수신하는 모든 Route를 함께 확인한다.

## 구현 흐름

1. Contract에서 변경 대상이 `State`, `Intent`, `Effect` 중 어디에 속하는지 정한다.
2. Screen 또는 component callback에서 `onIntent(...)`를 호출한다.
3. ViewModel의 `onIntent` 분기에 새 intent를 추가한다.
4. 화면 상태 변경은 `reduce { copy(...) }`로 처리한다.
5. navigation, toast, launcher, platform action은 `postSideEffect(...)`로 보낸다.
6. repository/usecase 결과는 `onSuccess`와 `onFailure`로 나눈다.
7. Route의 effect collect에서 외부 동작을 실행한다.
8. 새 repository/service/DataStore 구현이 붙으면 `implementing-remote-api` 또는 `implementing-local-preference`를 함께 적용한다.

## Contract

- `State`: Screen이 그릴 화면 상태이다.
- `Intent`: Screen 또는 component에서 ViewModel로 보내는 입력이다.
- `Effect`/`SideEffect`: ViewModel에서 Route로 보내는 one-shot output이다. 접미사는 화면마다 혼재하므로 기존 화면 수정 시 해당 Contract의 기존 접미사를 따른다.

## ViewModel 처리 기준

- 초기 로딩은 `Enter<Screen>Screen` intent를 `init { store.onIntent(...) }`에서 발행해 처리한다(다수). 일부 화면은 `mviIntentStore(initialFetchData = ...)`로 uiState 첫 구독 시 발행하며, 화면 진입 동작 변경은 이 intent 분기에서 수정한다.
- `onIntent`는 intent 분기와 짧은 state/effect 처리에 둔다.
- 조건이 있거나 비동기 작업이 있으면 private 함수로 분리한다.
- private 함수는 필요한 값만 `state`, `reduce`, `postSideEffect`로 받는다.
- 함수 역할은 `handle...`, `fetch...`, `load...`, `perform...`, `save...`, `update...`, `delete...`, `preload...` 계열로 드러낸다.
- 연속 입력을 합쳐 처리하는 동작은 `MutableSharedFlow`와 `debounce`를 쓴다.

## Effect 이름

같은 성격의 effect가 이미 있으면 해당 Contract의 기존 이름을 따른다.

- 뒤로 이동: `NavigateBack`
- 특정 화면 이동: `NavigateTo<Destination>` (기존 URL 이동에는 `NavigateUrl`, `NavigatePlayStore`도 있다)
- 기본 toast: `ShowToastMessage(val message: String)` 또는 `ShowToast(val message: String)` — 화면마다 혼재한다
- action toast: `ShowActionToast(...)`
- 권한 요청: `Request<Permission>Permission` 또는 `RequestPermission(...)` (Map은 `LaunchLocationPermission`)
- 앱 설정 이동: `MoveAppSettings(...)` (Map은 `NavigateToAppSettings`)
- 외부 앱 실행: `Launch<ExternalAction>(...)`
- 외부 링크 열기: `Open<ExternalTarget>(...)` — 일부 화면은 바텀시트·갤러리 열기에도 쓴다
- 이전 화면 변경 통지: `NotifyResult`, `Notify<Something>Changed`
- 결과 전달: `Send<X>Result`, `Set<X>Result`
- 목록 갱신: `Refresh<Target>`

## Screen 연결

```kotlin
FeatureButton(
    onClick = { onIntent(FeatureIntent.ClickConfirmButton) },
)

FeatureDialog(
    onDismissRequest = { onIntent(FeatureIntent.DismissDialog) },
)
```

## ViewModel 분기

```kotlin
private fun onIntent(
    intent: FeatureIntent,
    state: FeatureState,
    reduce: (FeatureState.() -> FeatureState) -> Unit,
    postSideEffect: (FeatureEffect) -> Unit,
) {
    when (intent) {
        FeatureIntent.ClickConfirmButton -> submit(state, reduce, postSideEffect)
        FeatureIntent.DismissDialog -> reduce { copy(isShowDialog = false) }
    }
}
```

## API 결과 처리

```kotlin
private fun submit(
    state: FeatureState,
    reduce: (FeatureState.() -> FeatureState) -> Unit,
    postSideEffect: (FeatureEffect) -> Unit,
) {
    viewModelScope.launch {
        repository.request(...)
            .onSuccess { data ->
                ...
            }
            .onFailure { e ->
                Timber.e(e)
                ...
            }
    }
}
```

## Effect 처리

```kotlin
viewModel.store.sideEffects.collectWithLifecycle { effect ->
    when (effect) {
        FeatureEffect.NavigateBack -> navigateBack()
        is FeatureEffect.ShowToastMessage -> nekiToast.showToast(effect.message)
    }
}
```

## 화면 간 결과 전달

이전 화면 갱신이 필요하면 `LocalResultEventBus.current`로 얻은 bus에 결과를 보낸다.

송신 화면 Route:

```kotlin
FeatureSideEffect.NotifyResult -> resultEventBus.sendResult(result = FeatureResult, allowDuplicate = false)
```

수신 화면 entry:

```kotlin
ResultEffect<FeatureResult>(resultBus) {
    viewModel.store.onIntent(OtherFeatureIntent.RefreshItems)
}
```

---
name: creating-feature-screen
description: Use when adding a new Compose feature screen with Contract, ViewModel, Route, Screen, Preview, or MVI store in this Android repository.
---

# Creating Feature Screen

새 화면은 `<Feature>Contract.kt`, `<Feature>ViewModel.kt`, `<Feature>Screen.kt`를 같은 화면 이름으로 만든다.

## 작업 범위

- `feature/<feature-name>/impl/src/main/.../<Feature>Contract.kt`
- `feature/<feature-name>/impl/src/main/.../<Feature>ViewModel.kt`
- `feature/<feature-name>/impl/src/main/.../<Feature>Screen.kt`
- `feature/<feature-name>/api/src/main/.../<Feature>NavKey.kt`
- `feature/<feature-name>/impl/src/main/.../navigation/<Feature>EntryProvider.kt`

다중 화면 feature는 `impl/<screen>/` 서브패키지에 Contract/ViewModel/Screen을 두고, 화면 전용 컴포넌트는 `impl/<screen>/component/`에 둔다.

## 구현 흐름

1. Contract에 `State`, `Intent`, `Effect`를 만든다.
2. ViewModel에 `MviIntentStore`를 만든다.
3. Screen 파일에 `Route`, `Screen`, private preview를 둔다.
4. api 모듈의 `<Feature>NavKey`에 `@Serializable` NavKey와 `MainNavigator.navigateToXxx()` 확장을 추가한다.
5. impl 모듈의 EntryProvider에 `entry<NavKey> { ...Route(...) }`를 등록한다. 등록하지 않으면 화면에 도달할 수 없다.

## Contract

```kotlin
data class FeatureState(
    val isLoading: Boolean = false,
)

sealed interface FeatureIntent {
    data object ClickBackIcon : FeatureIntent
}

sealed interface FeatureEffect {
    data object NavigateBack : FeatureEffect
}
```

기존 코드에는 `SideEffect`(다수)와 `Effect` 접미사가 혼재한다. 기존 화면을 수정할 때는 해당 화면의 접미사를 따른다.

## ViewModel

```kotlin
@HiltViewModel
internal class FeatureViewModel @Inject constructor() : ViewModel() {

    val store: MviIntentStore<FeatureState, FeatureIntent, FeatureEffect> =
        mviIntentStore(
            initialState = FeatureState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: FeatureIntent,
        state: FeatureState,
        reduce: (FeatureState.() -> FeatureState) -> Unit,
        postSideEffect: (FeatureEffect) -> Unit,
    ) {
        when (intent) {
            FeatureIntent.ClickBackIcon -> postSideEffect(FeatureEffect.NavigateBack)
        }
    }
}
```

화면 진입 시 데이터 로딩이 필요하면 `initialFetchData`를 사용한다.

```kotlin
mviIntentStore(
    initialState = FeatureState(),
    onIntent = ::onIntent,
    initialFetchData = { store.onIntent(FeatureIntent.EnterFeatureScreen) },
)
```

## Route

```kotlin
@Composable
internal fun FeatureRoute(
    viewModel: FeatureViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            FeatureEffect.NavigateBack -> navigateBack()
        }
    }

    FeatureScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}
```

`hiltViewModel()`은 `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`을 import한다. `androidx.hilt.navigation.compose`는 이 레포에 의존성이 없어 컴파일되지 않는다.

## Screen

```kotlin
@Composable
internal fun FeatureScreen(
    uiState: FeatureState,
    onIntent: (FeatureIntent) -> Unit = {},
) {
    ...
}
```

## Preview

```kotlin
@DevicePreview
@Composable
private fun FeatureScreenPreview() {
    NekiTheme {
        FeatureScreen(
            uiState = FeatureState(),
            onIntent = {},
        )
    }
}
```

## EntryProvider 등록

impl 모듈의 `navigation/<Feature>EntryProvider.kt`에 Hilt 모듈로 entry를 등록한다.

```kotlin
@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureEntryProviderModule {

    @IntoSet
    @Provides
    fun provideFeatureEntryBuilder(
        mainNavigator: MainNavigator,
    ): EntryProviderInstaller = {
        entry<FeatureNavKey.Feature> {
            FeatureRoute(
                navigateBack = mainNavigator::goBack,
            )
        }
    }
}
```

## 새 feature 모듈 생성 (기존 모듈에 화면만 추가하면 생략)

1. `settings.gradle.kts`에 `include(":feature:<name>:api")`, `include(":feature:<name>:impl")`를 등록한다.
2. api `build.gradle.kts`에 `alias(libs.plugins.neki.android.feature.api)`와 `namespace`를 선언한다.
3. impl `build.gradle.kts`에 `alias(libs.plugins.neki.android.feature.impl)`, `namespace`, `implementation(projects.feature.<name>.api)`를 선언한다.
4. `app/build.gradle.kts`에 api, impl 모듈을 모두 `implementation`으로 등록한다.

impl convention plugin이 core:designsystem, core:ui, core:data-api, core:common, core:domain, core:analytics와 hilt를 자동으로 추가한다.

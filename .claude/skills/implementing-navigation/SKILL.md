---
name: implementing-navigation
description: Use when adding or changing feature navigation, NavKey, navigator extensions, EntryProvider, assisted ViewModel creation, or result bus handling in this Android repository.
---

# Implementing Navigation

feature navigation은 API 모듈의 `NavKey`/navigator extension과 impl 모듈의 `EntryProvider`를 함께 맞춘다.

## 작업 범위

- `feature/<feature-name>/api/src/main/.../*NavKey.kt`
- 필요한 경우 `feature/<feature-name>/api/src/main/.../*Result.kt`
- 필요한 경우 `feature/<feature-name>/api/src/main/.../*Action.kt`
- `feature/<feature-name>/impl/src/main/.../navigation/*EntryProvider.kt` (예외: photo-upload만 `di/PhotoUploadEntryProvider.kt`. 신규 작업은 `navigation/` 패키지와 `<Feature>EntryProviderModule` 네이밍을 따른다)
- runtime nav argument를 받는 화면의 `ViewModel` factory 생성부

## 구현 흐름

1. API 모듈에 `sealed interface <Feature>NavKey : NavKey`를 정의한다.
2. 화면 key는 `@Serializable data object` 또는 `@Serializable data class`로 둔다.
3. `MainNavigator` 또는 `AuthNavigator` extension으로 이동 함수를 정의한다.
4. impl 모듈의 `EntryProvider`에 `entry<<Feature>NavKey.X>`를 등록한다.
5. runtime nav argument가 필요한 ViewModel은 entry 안에서 assisted factory로 생성한다.
6. root flow 이동이 있는 entry는 `RootNavigator`를 함께 받는다.
7. 뒤로가기로 돌아오면 안 되는 화면은 entry 람다의 `key`를 받아 이동 전 `navigator.remove(key)`로 스택에서 제거한다.
8. 다른 화면 변경 결과로 현재 화면을 갱신하는 entry는 `ResultEffect`를 둔다.

## NavKey

```kotlin
sealed interface FeatureNavKey : NavKey {

    @Serializable
    data object FeatureMain : FeatureNavKey

    @Serializable
    data class FeatureDetail(
        val id: Long,
        val title: String,
    ) : FeatureNavKey
}

fun MainNavigator.navigateToFeature() {
    navigate(FeatureNavKey.FeatureMain)
}

fun MainNavigator.navigateToFeatureDetail(id: Long, title: String) {
    navigate(FeatureNavKey.FeatureDetail(id = id, title = title))
}
```

## Main EntryProvider

```kotlin
@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureEntryProviderModule {

    @IntoSet
    @Provides
    fun provideFeatureEntryBuilder(
        navigator: MainNavigator,
    ): EntryProviderInstaller = {
        featureEntry(navigator)
    }
}

private fun EntryProviderScope<NavKey>.featureEntry(navigator: MainNavigator) {
    entry<FeatureNavKey.FeatureMain> {
        FeatureRoute(...)
    }
}
```

## Auth EntryProvider

```kotlin
typealias AuthEntryProviderInstaller = EntryProviderScope<NavKey>.() -> Unit

fun authEntryProvider(
    rootNavigator: RootNavigator,
    authNavigator: AuthNavigator,
): AuthEntryProviderInstaller = {
    authEntry(rootNavigator, authNavigator)
}
```

Main은 `@IntoSet`으로 등록된 `EntryProviderInstaller`를 `MainActivity`가 set으로 주입받아 자동 조립한다. Auth는 DI set이 아니라 `MainActivity`가 `authEntryProvider(...)`를 직접 호출해 조립하므로, 신규 auth 화면은 `AuthEntryProvider.kt`의 `authEntry`에 entry만 추가하면 되고 app 모듈 수정은 불필요하다.

## Assisted ViewModel

```kotlin
@HiltViewModel(assistedFactory = FeatureDetailViewModel.Factory::class)
internal class FeatureDetailViewModel @AssistedInject constructor(
    @Assisted private val key: FeatureNavKey.FeatureDetail,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(key: FeatureNavKey.FeatureDetail): FeatureDetailViewModel
    }
}
```

assisted ViewModel은 entry에서 생성해 Route의 필수 파라미터로 전달한다. `viewModel: XxxViewModel = hiltViewModel()` 기본값은 runtime nav argument가 없는 화면의 Route에만 둔다.

```kotlin
@Composable
internal fun FeatureDetailRoute(
    viewModel: FeatureDetailViewModel,
    navigateBack: () -> Unit,
) {
    ...
}
```

```kotlin
entry<FeatureNavKey.FeatureDetail> { key ->
    val viewModel = hiltViewModel<FeatureDetailViewModel, FeatureDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(key) },
    )

    FeatureDetailRoute(
        viewModel = viewModel,
        navigateBack = navigator::goBack,
    )
}
```

## 공유 ViewModel

여러 화면이 하나의 ViewModel을 공유하는 경우 부모 entry에 `clazzContentKey`를, 자식 entry에 `HiltSharedViewModelStoreNavEntryDecorator.parent`를 지정한다. 실제 예시는 `MyPageEntryProvider.kt` 참고.

```kotlin
entry<MyPageNavKey.MyPage>(
    clazzContentKey = { key -> key.toString() },
) { ... }

entry<MyPageNavKey.Profile>(
    metadata = HiltSharedViewModelStoreNavEntryDecorator.parent(
        MyPageNavKey.MyPage.toString(),
    ),
) { ... }
```

## Result 통신

result는 다른 화면에서 변경된 데이터 때문에 현재 화면을 갱신해야 하는 경우에 쓴다.

consumer는 result를 받아 현재 화면의 intent를 실행하는 쪽이다.

relay는 하위 화면의 result를 현재 flow의 result로 바꿔 다시 보내는 쪽이다.

relay 흐름: 하위 화면 result -> 현재 flow EntryProvider -> 상위 화면 result

payload가 필요한 result는 `data class` 또는 `sealed interface`로 정의하고 `ResultEffect<T>(resultBus) { result -> ... }` 람다 인자로 소비한다. relay 재발행은 `allowDuplicate = false`로 같은 result의 중복 발행을 막는다.

```kotlin
val resultBus = LocalResultEventBus.current

ResultEffect<FeatureResult>(resultBus) {
    viewModel.store.onIntent(FeatureIntent.RefreshFeature)
}
```

```kotlin
ResultEffect<ChildResult>(resultBus) {
    resultBus.sendResult(result = ParentResult, allowDuplicate = false)
}
```

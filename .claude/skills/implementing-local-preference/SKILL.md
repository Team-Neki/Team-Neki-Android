---
name: implementing-local-preference
description: Use when adding or changing DataStore Preferences, local flags, token/auth/user persistence, preference keys, or local repository methods in this Android repository.
---

# Implementing Local Preference

local preference 구현은 `core:data-api` repository contract와 `core:data`의 DataStore qualifier, DataStore provider, repository impl을 함께 맞춘다.

## 작업 범위

- `core/data-api/src/main/java/com/neki/android/core/dataapi/repository/*Repository.kt`
- `core/data/src/main/java/com/neki/android/core/data/local/di/DataStoreQualifier.kt`
- `core/data/src/main/java/com/neki/android/core/data/local/di/DataStoreModule.kt`
- `core/data/src/main/java/com/neki/android/core/data/repository/impl/*RepositoryImpl.kt`
- `core/data/src/main/java/com/neki/android/core/data/repository/di/RepositoryModule.kt`
- data layer는 feature 모듈 참조 없이 core 계층 안에서 구현한다.

## 구현 흐름

1. `core:data-api`에 repository method 또는 `Flow` property를 정의한다.
2. 기존 DataStore(`@AuthDataStore`/`@TokenDataStore`/`@UserDataStore`)에 키 추가로 해결되는지 먼저 확인하고, 새 DataStore가 필요할 때만 qualifier와 provider를 함께 추가한다.
3. repository impl에 `@<Qualifier> DataStore<Preferences>`를 주입한다.
4. 새 repository contract면 `RepositoryModule`에 `@Binds @Singleton`으로 impl을 바인딩한다.
5. preference key는 repository impl의 `companion object`에 둔다.
6. 읽기는 `dataStore.data.map { ... }`으로 노출한다. 단발 조회는 `suspend fun`에서 `dataStore.data.map { ... }.first()`를 쓴다.
7. 쓰기는 `dataStore.edit { ... }`으로 처리한다.
8. token처럼 민감한 값은 저장/조회 시 `CryptoManager`를 거친다.
9. remote API와 함께 쓰는 repository면 local method와 remote method를 같은 repository contract 안에 둔다.

## DataStore 추가

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FeatureDataStore
```

```kotlin
private const val FEATURE_DATASTORE = "feature_datastore"
private val Context.featureDataStore: DataStore<Preferences> by preferencesDataStore(
    name = FEATURE_DATASTORE,
)

@FeatureDataStore
@Singleton
@Provides
fun provideFeatureDataStore(
    @ApplicationContext context: Context,
): DataStore<Preferences> = context.featureDataStore
```

## Repository 형태

```kotlin
interface FeatureRepository {
    val hasSeenFeatureGuide: Flow<Boolean>
    suspend fun setFeatureGuideSeen()
}
```

```kotlin
class FeatureRepositoryImpl @Inject constructor(
    @FeatureDataStore private val dataStore: DataStore<Preferences>,
) : FeatureRepository {
    override val hasSeenFeatureGuide: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[HAS_SEEN_FEATURE_GUIDE] ?: false
        }

    override suspend fun setFeatureGuideSeen() {
        dataStore.edit { preferences ->
            preferences[HAS_SEEN_FEATURE_GUIDE] = true
        }
    }

    companion object {
        private val HAS_SEEN_FEATURE_GUIDE = booleanPreferencesKey("has_seen_feature_guide")
    }
}
```

## Token

```kotlin
override suspend fun saveTokens(
    accessToken: String,
    refreshToken: String,
) {
    dataStore.edit { preferences ->
        preferences[ACCESS_TOKEN] = CryptoManager.encrypt(accessToken)
        preferences[REFRESH_TOKEN] = CryptoManager.encrypt(refreshToken)
    }
}
```

```kotlin
override fun getAccessToken(): Flow<String> {
    return dataStore.data.map { preferences ->
        runCatching {
            preferences[ACCESS_TOKEN]?.let { CryptoManager.decrypt(it) } ?: ""
        }.getOrElse { "" }
    }
}
```

복호화 실패 시에는 손상된 키를 `dataStore.edit { it.remove(...) }`로 제거하고 기본값을 반환한다 (`TokenRepositoryImpl.hasTokens()` 참고).

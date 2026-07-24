---
name: implementing-remote-api
description: Use when adding or changing remote API calls, Ktor services, request/response DTOs, repository contracts, or repository implementations in this Android repository.
---

# Implementing Remote API

원격 API 구현은 `core:data-api`의 repository contract와 `core:data`의 Ktor service, request/response DTO, repository impl을 함께 맞춘다.

## 작업 범위

- `core/data-api/src/main/java/com/neki/android/core/dataapi/repository/*Repository.kt`
- `core/data/src/main/java/com/neki/android/core/data/remote/api/*Service.kt`
- `core/data/src/main/java/com/neki/android/core/data/remote/model/request/*Request.kt`
- `core/data/src/main/java/com/neki/android/core/data/remote/model/response/*Response.kt`
- `core/data/src/main/java/com/neki/android/core/data/repository/impl/*RepositoryImpl.kt`
- paging API면 `core/data/src/main/java/com/neki/android/core/data/paging/*PagingSource.kt`
- 새 repository를 추가하면 `core/data/src/main/java/com/neki/android/core/data/repository/di/RepositoryModule.kt`
- data layer는 feature 모듈 참조 없이 core 계층 안에서 구현한다.
- 기본 API 서버 외 호스트(presigned 업로드, webhook 등)를 호출하면 기본 `HttpClient` 대신 `remote/qualifier`의 `@UploadHttpClient`·`@WebhookHttpClient`가 붙은 client를 주입한다.

## 구현 흐름

1. `core:data-api`에 repository interface를 정의한다.
2. `core:data`에 Ktor service method를 추가한다.
3. request/response DTO와 mapper를 추가한다.
4. repository impl에서 service를 호출하고 `Result<core model>`로 반환한다.
5. paging API면 `PagingSource`와 `Pager` 흐름을 함께 맞춘다.
6. 기본 client는 Bearer 토큰 첨부·갱신을 자동 처리하므로 service/repository에서 토큰을 직접 다루지 않는다. auth 없이 호출되는 endpoint면 `core/data/src/main/java/com/neki/android/core/data/remote/di/NetworkModule.kt`의 `sendWithoutAuthUrls`에 path를 추가한다(encodedPath 정확 일치 매칭이라 path variable 없는 고정 경로만 가능).
7. 새 repository면 `RepositoryModule`에 `@Binds @Singleton` binding을 추가한다.
8. 여러 repository를 조합하는 로직은 `core/domain/src/main/java/com/neki/android/core/domain/usecase`의 UseCase로 분리한다.

## 기본 형태

```kotlin
interface FeatureRepository {
    suspend fun getFeature(id: Long): Result<Feature>
}
```

```kotlin
class FeatureService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getFeature(id: Long): BasicResponse<FeatureResponse> {
        return client.get("/api/features/$id").body()
    }
}
```

```kotlin
@Serializable
data class FeatureResponse(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
) {
    internal fun toModel(): Feature = Feature(
        id = id,
        name = name,
    )
}
```

```kotlin
class FeatureRepositoryImpl @Inject constructor(
    private val featureService: FeatureService,
) : FeatureRepository {
    override suspend fun getFeature(id: Long): Result<Feature> = runSuspendCatching {
        featureService.getFeature(id).data.toModel()
    }
}
```

## Nullable Response

```kotlin
class FeatureService @Inject constructor(
    private val client: HttpClient,
) {
    suspend fun getOptionalFeature(): BasicNullableResponse<FeatureResponse> {
        return client.get("/api/features/optional").body()
    }
}
```

## Request Body

```kotlin
override suspend fun updateFeature(
    id: Long,
    name: String,
): Result<Unit> = runSuspendCatching {
    featureService.updateFeature(
        requestBody = UpdateFeatureRequest(
            id = id,
            name = name,
        ),
    )
}
```

## Unit Response

등록/수정/삭제처럼 반환 model이 없으면 service는 `BasicNullableResponse<Unit>`, repository는 `Result<Unit>`로 둔다.

```kotlin
suspend fun updateFeature(requestBody: UpdateFeatureRequest): BasicNullableResponse<Unit> {
    return client.put("/api/features") { setBody(requestBody) }.body()
}
```

```kotlin
override suspend fun updateFeature(name: String): Result<Unit> = runSuspendCatching {
    featureService.updateFeature(
        requestBody = UpdateFeatureRequest(name = name),
    )
}
```

## 에러 처리

기본 client는 `expectSuccess = true`라 non-2xx 응답은 예외로 던져지고, `runSuspendCatching`이 `Result` 실패로 감싼다(`CancellationException`은 재던져 취소를 전파). 특정 상태코드를 도메인 에러로 다루려면 repository impl에서 `ClientRequestException`을 잡아 `core:common`의 `NekiApiException` 하위 예외(`ApiErrorCode` 상수 사용)로 변환해 던진다.

## Paging

```kotlin
interface FeatureRepository {
    fun getFeaturesFlow(): Flow<PagingData<Feature>>
}
```

```kotlin
override fun getFeaturesFlow(): Flow<PagingData<Feature>> {
    return Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { FeaturePagingSource(featureService) },
    ).flow
}
```

`PAGE_SIZE`·`PREFETCH_DISTANCE`는 repository impl 파일 상단에 `private const val`로 둔다(현행 20/10).

# NEKI GA4 이벤트 트래킹 스펙

## 공통 규칙

- 이벤트명: snake_case, `동사_객체` 형식
- 트리거 원칙: 사용자 행동이 **완료된 시점** (버튼 클릭이 아닌 실제 데이터 반영 완료 시점)
- 파라미터 타입: `FirebaseAnalyticsLogger`가 지원하는 원시 타입(`Int`/`Long`/`Double`/`Boolean`/`String`)을 유지
- 단순 클릭 이벤트 제외, 의미 있는 행동 중심으로 수집

### 공통 파라미터 (Firebase 자동 수집)

| 파라미터 | 타입 | 설명 |
|---------|------|------|
| `user_id` | string | 사용자 식별자 |
| `platform` | string | `ios` / `android` |
| `app_version` | string | 앱 버전 |

---

## 아카이빙 이벤트

| 이벤트명 | 트리거 시점 | 파라미터 |
|---------|-----------|---------|
| `archiving_view` | 아카이빙 탭 진입 시 (탭 복귀 포함) | - |
| `photo_upload` | 업로드 완료 시 | `method`: `gallery`/`qr`, `count`: int (업로드된 사진 수) |
| `album_create` | 앨범 생성 완료 시 | - |
| `album_add_from_detail` | 상세에서 앨범 추가 완료 시 | `album_count`: int (사진이 추가된 앨범 수) |
| `album_add_from_multi` | 다중 선택 후 앨범 추가 완료 시 | `photo_count`: int (선택된 사진 수), `album_count`: int (선택한 앨범 수) |
| `photo_move` | 이동 완료 시 | - |
| `photo_copy` | 복제 완료 시 | - |
| `photo_detail_view` | 상세 진입 시 | - |
| `photo_memo_create` | 메모 저장 시 | - |
| `photo_add_to_album` | 앨범 상세에서 사진 가져오기 완료 시 | `photo_count`: int (가져온 사진 수), `album_count`: int (항상 1) |

### 로깅 위치 (아카이빙)

| 이벤트 | ViewModel | 비고 |
|--------|-----------|------|
| `archiving_view` | `ArchiveMainViewModel.logArchivingView()` | Route `LaunchedEffect`에서 호출 |
| `photo_upload` | `SelectAlbumViewModel.performAction()` | UploadFromQR / UploadFromGallery 성공 시 |
| `album_create` | `ArchiveMainViewModel`, `AllAlbumViewModel`, `SelectAlbumViewModel` | createFolder 성공 시 |
| `album_add_from_detail` | `SelectAlbumViewModel.performAction()` | CopyPhotos 성공 시, 단일 사진(photoIds.size == 1) |
| `album_add_from_multi` | `SelectAlbumViewModel.performAction()` | CopyPhotos 성공 시, 다중 사진(photoIds.size > 1) |
| `photo_move` | `SelectAlbumViewModel.performAction()` | MovePhotos 성공 시 |
| `photo_copy` | `SelectAlbumViewModel.performAction()`, `AlbumDetailViewModel.handleConfirmImport()` | CopyPhotos 성공 시 |
| `photo_detail_view` | `PhotoDetailViewModel` init | ViewModel 생성 시 |
| `photo_memo_create` | `PhotoDetailViewModel` | 메모 저장 성공 시 |
| `photo_add_to_album` | `AlbumDetailViewModel.handleConfirmImport()` | 앨범 상세에서 사진 가져오기(Import) 성공 시 |

---

## 지도 이벤트

| 이벤트명 | 트리거 시점 | 파라미터 |
|---------|-----------|---------|
| `map_view` | 지도 탭 진입 시 (탭 복귀 포함) | - |
| `map_re_search` | 재검색 버튼 클릭 시 | `has_filter`: boolean, `region_changed`: boolean |
| `map_brand_filter_toggle` | 브랜드 필터 선택/해제 시 | `action`: `select`/`deselect`, `selected_count`: int (액션 후 기준), `brand_name`: string |
| `booth_select` | 부스 클릭 시 | `entry_point`: `map`/`bottom_sheet`, `brand_name`: string |
| `map_route_click` | 외부 지도 앱 선택 시 | `map_type`: `kakao_map`/`naver_map`/`google_map` |
| `brand_filter_manage_view` | 브랜드 순서 편집 화면 진입 시 | - |
| `favorite_booth_view` | 드래그 패널 '저장한 포토부스' 탭 선택 시 | `favorite_booth_count`: int (즐겨찾기 수) |
| `favorite_booth_filter_on` | 즐겨찾기 마커 표시 ON 시 | `favorite_booth_count`: int |
| `favorite_booth_filter_off` | 즐겨찾기 마커 표시 OFF 시 | - |
| `booth_favorite_add` | 즐겨찾기 추가 완료 시 | `booth_name`: string (지점명), `brand_name`: string |
| `booth_favorite_remove` | 즐겨찾기 해제 완료 시 | `booth_name`: string (지점명), `brand_name`: string |

### 로깅 위치 (지도)

| 이벤트 | ViewModel | 비고 |
|--------|-----------|------|
| `map_view` | `MapViewModel.logMapView()` | Route `LaunchedEffect`에서 호출 |
| `map_re_search` | `MapViewModel.onIntent` ClickRefreshButton | `isRegionChanged()` 함수로 계산, `lastSearchCenter` 갱신 |
| `map_brand_filter_toggle` | `MapViewModel.handleClickBrand()` | reduce 내부에서 updatedBrands 기준으로 로깅 |
| `booth_select` | `MapViewModel.handleClickNearPhotoBooth()` (`bottom_sheet`), `handleClickPhotoBoothMarker()` (`map`) | |
| `map_route_click` | `MapViewModel.handleClickDirectionItem()` | `when(app)`으로 analyticsName 구분 |
| `brand_filter_manage_view` | `MapViewModel.onIntent` ClickEditBrandOrder | postSideEffect 직전 로깅 |
| `favorite_booth_view` | `MapViewModel.onIntent` SelectTab | `MapTab.FAVORITE` 선택 시 로깅 |
| `favorite_booth_filter_on` | `MapViewModel.onIntent` ClickShowFavoriteIcon | ON 전환 시 로깅 |
| `favorite_booth_filter_off` | `MapViewModel.onIntent` ClickShowFavoriteIcon | OFF 전환 시 로깅 |
| `booth_favorite_add` | `MapViewModel.updateFavorite()` | API 성공 후 `photoBooth.favorite == true`일 때 |
| `booth_favorite_remove` | `MapViewModel.updateFavorite()` | API 성공 후 `photoBooth.favorite == false`일 때 |

---

## 포즈 이벤트

| 이벤트명 | 트리거 시점 | 파라미터 |
|---------|-----------|---------|
| `pose_view` | 포즈 탭 진입 시 (탭 복귀 포함) | - |
| `pose_random_start` | 랜덤 포즈 화면 진입 시 | - |
| `pose_random_session_end` | 랜덤 포즈 화면 이탈 시 | `total_swipe_count`: int (세션 동안 총 스와이프 횟수) |
| `pose_filter_toggle` | 인원수 필터 선택/변경 시 (해제는 미포함) | `people_count`: int (선택한 인원수) |
| `pose_bookmark_filter` | 북마크 필터 선택/해제 시 | - |
| `pose_bookmark` | 개별 포즈 북마크/해제 시 | - |

### 로깅 위치 (포즈)

| 이벤트 | ViewModel | 비고 |
|--------|-----------|------|
| `pose_view` | `PoseViewModel.logPoseView()` | Route `LaunchedEffect`에서 호출 |
| `pose_random_start` | `RandomPoseViewModel.onIntent` EnterRandomPoseScreen | init에서 intent 호출 |
| `pose_random_session_end` | `RandomPoseViewModel.onCleared()` | 프로세스 강제종료 시 유실 가능 (스펙 허용) |
| `pose_filter_toggle` | `PoseViewModel.handlePeopleCountSheetItem()` | 동일 필터 재클릭(해제)은 로깅 제외 |
| `pose_bookmark_filter` | `PoseViewModel.onIntent` ClickBookmarkChip | |
| `pose_bookmark` | `PoseViewModel`, `RandomPoseViewModel`, `PoseDetailViewModel` | ClickBookmarkIcon 시점 |

---

## 앱 전역 이벤트

| 이벤트명 | 트리거 시점 | 로깅 위치 |
|---------|-----------|---------|
| `app_open` | 로그인/자동 로그인 완료 시 | `LoginViewModel.checkTermAgreementState()` (약관 동의 완료 유저만), `SplashViewModel.fetchAuthState()` (자동 로그인 성공 시) |

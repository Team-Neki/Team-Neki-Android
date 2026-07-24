# 논의 사항

프로젝트 convention으로 확정하기 전에 팀 합의가 필요한 항목을 정리한다.

## Feature effect 타입 네이밍

논의할 것:

- Contract의 one-shot output 타입 이름을 `<Screen>Effect`로 통일할지, `<Screen>SideEffect`를 유지할지 결정한다.

현재 코드 기준:

| 모듈 | 화면 | 타입 |
|---|---|---|
| `feature/auth` | `Splash`, `Login`, `Term` | `SideEffect` |
| `feature/archive` | `ArchiveMain`, `AllAlbum`, `AllPhoto`, `AlbumDetail`, `PhotoDetail` | `SideEffect` |
| `feature/select-album` | `SelectAlbum` | `SideEffect` |
| `feature/photo-upload` | `QRScan` | `SideEffect` |
| `feature/pose` | `Pose`, `RandomPose`, `PoseDetail` | `Effect` 2개, `SideEffect` 1개 |
| `feature/map` | `Map`, `PhotoBoothOrderChange` | `Effect` 1개, `SideEffect` 1개 |
| `feature/mypage` | `MyPage`, `Permission` | `Effect` |

현재 정리된 방향:

- 새 화면의 one-shot output 타입은 `<Screen>Effect`를 쓴다.
- `MviIntentStore`의 API 이름은 기존 코드 그대로 `postSideEffect`, `sideEffects`를 사용한다.

팀에서 정할 지점:

- 신규 화면 기준 타입명을 `<Screen>Effect`로 확정할지
- 기존 `<Screen>SideEffect` 화면을 수정할 때 타입명 변경까지 포함할지
- 기존 화면은 유지하고 신규 화면만 `<Screen>Effect`로 갈지

논의 결과 반영 위치:

- `.claude/skills/creating-feature-screen/SKILL.md`의 `Contract`에 반영한다.
- `.claude/skills/changing-feature-behavior/SKILL.md`의 `Contract`, `Effect 이름`에 반영한다.

## Feature ViewModel 초기 로딩 방식

논의할 것:

- 초기 로딩이 필요한 화면에서 `init { store.onIntent(...) }`, `Enter<Screen>Screen`, `initialFetchData`를 어떤 기준으로 사용할지 결정한다.

현재 코드 기준:

| 방식 | ViewModel |
|---|---|
| `initialFetchData`에서 `Enter...Screen` 호출 | `MapViewModel`, `SelectAlbumViewModel` |
| `init { store.onIntent(Enter...Screen) }` | `ArchiveMainViewModel`, `AllAlbumViewModel`, `MyPageViewModel`, `PermissionViewModel`, `TermViewModel`, `RandomPoseViewModel`, `PoseDetailViewModel` |
| Route에서 `Enter...Screen` 호출 | `SplashViewModel` |
| Enter intent는 있으나 초기 fetch 없음 | `PoseViewModel`, `AllPhotoViewModel` |
| Enter intent 없음 | `AlbumDetailViewModel`, `PhotoDetailViewModel`, `LoginViewModel`, `QRScanViewModel`, `PhotoBoothOrderChangeViewModel` |

- `PhotoDetailViewModel`은 init 블록이 있으나 초기 fetch가 아니라 analytics 로그와 favorite debounce 수집만 수행한다. `PhotoBoothOrderChangeViewModel`은 초기 데이터를 생성자 주입으로 받는다.

팀에서 정할 지점:

- 신규 화면의 기본 초기 로딩 방식을 무엇으로 할지
- 화면 재진입 시 자동 refetch가 필요한 화면과 최초 1회만 필요한 화면을 어떻게 구분할지
- result로 받은 refresh와 5초 subscription 기반 재조회가 겹칠 때 어떤 흐름 하나로 처리할지
- paging flow나 nav key로 초기 데이터가 구성되는 화면에 `Enter...Screen`을 둘지

논의 결과 반영 위치:

- `.claude/skills/creating-feature-screen/SKILL.md`의 `ViewModel`에 반영한다.
- `.claude/skills/changing-feature-behavior/SKILL.md`의 `ViewModel 처리 기준`에 반영한다.
- result 기반 refresh 방식이 함께 정해지면 `.claude/skills/implementing-navigation/SKILL.md`의 `Result 통신`에도 반영한다.

## Feature result/notify effect 네이밍

논의할 것:

- 화면 밖으로 결과나 변경사항을 전달하는 effect 이름을 어떻게 구분할지 결정한다.

현재 코드 기준:

| 이름 | 사용 |
|---|---|
| `NotifyResult` | `AllAlbumSideEffect`, `AllPhotoSideEffect`, `AlbumDetailSideEffect` |
| `NotifyPhotoUpdated` | `PhotoDetailSideEffect` |
| `NotifyBookmarkChanged` | `PoseDetailSideEffect` |
| `SendUploadResult` | `SelectAlbumSideEffect` |
| `SendAlbumCreatedResult` | `SelectAlbumSideEffect` |
| `SendPhotoMovedResult` | `SelectAlbumSideEffect` |
| `SendPhotoCopiedResult` | `SelectAlbumSideEffect` |
| `SendBrandsOrderChangeResult` | `PhotoBoothOrderChangeSideEffect` |
| `SetQRScannedResult` | `QRScanSideEffect` |
| `SetOpenGalleryResult` | `QRScanSideEffect` |
| `RefreshPhotos` | `AllPhotoIntent`, `AllPhotoSideEffect`, `AlbumDetailIntent`, `AlbumDetailSideEffect` |
| `RefreshAlbums` | `AllAlbumIntent` |
| `RefreshArchiveMain` | `ArchiveMainIntent` |

팀에서 정할 지점:

- result bus로 외부 화면에 알리는 effect를 `Notify<ResultName>`로 맞출지
- payload를 직접 보내는 effect를 `Send<ResultName>`로 맞출지
- platform/result launcher 결과 설정은 `Set<ResultName>`로 둘지
- result가 아닌 intent에도 `Set` 접두어가 쓰이고 있어(`SetAppVersion`, `SetViewType`) 접두어 충돌을 어떻게 정리할지
- 현재 화면 refresh 트리거인 `Refresh...`를 effect 이름 목록에 포함할지

논의 결과 반영 위치:

- `.claude/skills/changing-feature-behavior/SKILL.md`의 `Effect 이름`에 반영한다.
- Route callback 이름이나 result bus 처리 방식이 함께 정해지면 `.claude/skills/implementing-navigation/SKILL.md`의 `EntryProvider`, `Result 통신`에도 반영한다.

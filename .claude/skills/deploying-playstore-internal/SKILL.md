---
name: deploying-playstore-internal
description: Use when deploying this repository to Play Store production through a release PR, explicitly requested internal testing, or checking automatic release status.
---

# Deploying Play Store

별도 트랙 지정 없이 "배포"라고 하면 **프로덕션 배포**를 뜻한다. 내부테스트는 사용자가 명시한 경우에만 진행한다.

## 프로덕션 배포

1. 릴리즈 PR의 diff에 `build-logic/src/main/java/com/neki/android/buildlogic/const/BuildConst.kt`의 `VERSION_NAME`과 `release-notes/<version>/ko-KR.txt`를 함께 포함한다. 버전은 이전 main보다 높은 `major.minor.patch`이고, 노트는 사용자용 문구 1~500자다. `versionCode`는 전체 Play 트랙의 최댓값 다음 번호로 자동 계산하므로 직접 올리지 않는다.
2. 기본 head는 `develop`, base는 `main`, 제목은 `[release] v<version>`, 라벨은 `release`다. 버전·제목·노트 검증과 CI를 확인하고 merge commit으로 머지한다. 세부 규칙은 `.claude/skills/git-conventions/references/pull-request.md`를 따른다.
3. 동일 버전·동일 git tree의 게시된 내부테스트 후보가 있으면 기존 versionCode의 AAB를 프로덕션으로 승급한다. 소스가 달라졌거나 출처를 확인할 수 없는 기존 내부 빌드는 재사용하지 않고 새로 빌드한다.
4. `.github/workflows/deploy-playstore.yml`이 **release 라벨이 있는 main PR의 실제 머지**만 처리한다. 머지 커밋을 고정해 CI를 다시 실행하고, 통과한 정확한 소스를 프로덕션에 제출한다. PR 생성·닫기만으로는 배포하지 않는다.
5. Play API에서 검토 중·승인 후 게시 대기·게시됨 중 하나로 제출 성공을 확인한 뒤 해당 커밋에 `v<version>` 태그와 GitHub Release를 생성한다. 태그 push는 배포 트리거가 아니다. 태그를 수동 생성하거나 기존 태그를 이동·삭제하지 않는다.
6. GitHub Release에는 사용자용 노트, GitHub가 생성한 PR 변경 내역, 버전 코드·소스 SHA·Play 상태가 기록된다. `.github/workflows/playstore-status.yml`이 약 15분 간격으로 상태를 갱신한다. 검토 중을 게시 완료로 보고하지 않는다. '게시됨'은 Google의 트랙 제공 상태이며 출시 비율·중지 여부의 상세는 Console에서 확인한다.
7. 성공 후 develop이 배포 커밋의 조상이면 자동 fast-forward한다. 새 개발 커밋으로 갈라졌다면 자동 동기화하지 않는다.

## 재시도와 확인

- Actions에서 실패한 배포 run을 재실행한다. 동일 버전·소스의 프로덕션 제출이 이미 확인되면 업로드를 반복하지 않고 태그·Release 생성을 이어간다.
- 미제출 빌드가 있으면 같은 versionCode로 다시 제출한다. 다른 소스의 동일 버전, 태그 충돌, 다른 프로덕션 검토 진행 중, 검토 거절은 자동 진행하지 않는다.
- 업로드 후 제출 상태를 확인할 수 없으면 실패로 남기고 태그를 만들지 않는다. 상태 조회 실패를 게시 완료로 치환하지 않는다.
- 실제 배포 없이 연결을 확인하려면 `.github/workflows/verify-github-release.yml`을 실행한다. 테스트·Fastlane 파싱·Google Play 읽기 전용 조회만 수행한다.

```bash
gh run list --repo Team-Neki/Team-Neki-Android --workflow=deploy-playstore.yml --limit 3
gh workflow run playstore-status.yml --repo Team-Neki/Team-Neki-Android
gh workflow run verify-github-release.yml --repo Team-Neki/Team-Neki-Android
```

## 내부테스트 배포

`.github/workflows/deploy-playstore-internal.yml`을 명시적으로 수동 실행한다. 입력 ref를 커밋 SHA로 고정하고 CI 통과 후 내부 트랙에 업로드한다. 프로덕션용 태그·GitHub Release는 생성하지 않는다. Firebase QA는 `.claude/skills/deploying-firebase/SKILL.md`의 별도 debug APK 경로다.

```bash
gh workflow run deploy-playstore-internal.yml --repo Team-Neki/Team-Neki-Android -f ref=develop
```

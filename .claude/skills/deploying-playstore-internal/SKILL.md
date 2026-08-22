---
name: deploying-playstore-internal
description: Use when deploying to Play Store Internal Testing in this repository, including creating and pushing a release tag.
---

# Deploying Play Store Internal Testing

이 레포에서 Play Store 내부테스트 배포는 `v*` 형식의 태그를 push하면 `.github/workflows/deploy-playstore.yml`이 트리거되어 `fastlane deploy` (`internal` lane → `create_github_release` lane)를 실행하는 방식이다.

## 사전 확인

- 배포 대상은 항상 `main` 브랜치의 HEAD여야 한다. 워크플로우의 "Verify tag points to main HEAD" 단계가 태그 커밋과 `origin/main` HEAD가 다르면 배포를 실패시킨다.
- 배포 전 `main`의 CI(`android-ci.yml`)가 통과했는지 확인한다.
- 태그 push는 되돌리기 어려운 동작이므로 실행 전 사용자에게 확인받는다.

## 절차

1. 태그명은 `v<versionName>` 형식을 쓴다 (예: `v1.3.5`). `versionCode`는 fastlane이 `production, beta, alpha, internal` 전체 트랙의 기존 최대값을 조회해 자동으로 다음 값을 계산하므로 직접 지정하지 않는다.
2. 로컬 `main`을 최신 `origin/main`으로 맞춘 뒤 태그를 생성하고 push한다.

```bash
git fetch origin main
git checkout main && git reset --hard origin/main
git tag v<versionName>
git push origin v<versionName>
```

3. push 후 `gh run list --repo Team-Neki/Team-Neki-Android --workflow=deploy-playstore.yml --limit 1`로 워크플로우가 트리거됐는지 확인한다.
4. `gh run view <run-id> --repo Team-Neki/Team-Neki-Android`로 진행 상태를 확인하고, 실패 시 `gh run view <run-id> --log-failed`로 원인을 파악한다.
5. 완료되면 `create_github_release` lane이 직전 태그 대비 커밋 로그로 GitHub Release도 함께 생성한다.

## 흔한 실패 원인

- 태그 커밋이 `main` HEAD와 다름 → 원격과 로컬 태그를 모두 지우고(`git push origin --delete v<versionName>`, `git tag -d v<versionName>`) `main` 최신 커밋에 다시 붙인다. 로컬 태그를 지우지 않으면 이후 `git tag v<versionName>`가 실패한다.
- `neki_key_store.jks`를 찾지 못함 → "Restore release keystore" 단계(`secrets.RELEASE_KEYSTORE_BASE64`)가 워크플로우에 있는지 확인한다.

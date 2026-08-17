---
name: deploying-firebase
description: Use when deploying to Firebase App Distribution in this repository, including dispatching the deploy-firebase GitHub Actions workflow.
---

# Deploying Firebase App Distribution

이 레포에서 Firebase App Distribution 배포는 GitHub Actions에서 `.github/workflows/deploy-firebase.yml`을 `workflow_dispatch`로 수동 실행하는 방식이다. `fastlane deploy_qa` (`firebase_distribute` lane)가 debug APK를 빌드해 "네키들" 테스터 그룹에 배포한다.

## 사전 확인

- 배포 대상 `ref`(브랜치 또는 커밋, 기본값 `develop`)를 확인한다.
- 배포 전 대상 브랜치의 CI(`android-ci.yml`)가 통과했는지 확인한다.

## 절차

1. 워크플로우를 실행한다.

```bash
gh workflow run deploy-firebase.yml --repo Team-Neki/Team-Neki-Android -f ref=<브랜치명>
```

2. 실행된 run을 찾아 상태를 확인한다.

```bash
gh run list --repo Team-Neki/Team-Neki-Android --workflow=deploy-firebase.yml --limit 1
gh run view <run-id> --repo Team-Neki/Team-Neki-Android
```

3. 실패 시 `gh run view <run-id> --log-failed`로 원인을 파악한다.
4. `versionCode`/`versionName`은 CI가 `github.run_number` 기반으로 자동 주입하므로(`FASTLANE_VERSION_CODE`) 별도 지정이 필요 없다.

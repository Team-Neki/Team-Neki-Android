---
name: create-pr
description: Create GitHub pull requests for the current repository. Use this skill whenever the user wants to open, create, or submit a PR — including phrases like "PR 등록", "PR 만들어줘", "PR 올려줘", "풀리퀘 생성", "create PR", "open pull request", or "/create-pr". Also trigger when the user says they're done with a feature and want to merge it, or asks to submit their work for review. This skill inspects the current branch and commits, drafts the PR body in the project's established format, and returns the created PR URL.
---

# GitHub PR Creator

현재 브랜치를 기반으로 GitHub PR을 생성합니다 (YAPP-Github/27th-App-Team-2-Android).

## 워크플로우

### 1단계: 현재 브랜치 및 커밋 파악

```bash
git branch --show-current         # 현재 브랜치명
git log origin/develop..HEAD --oneline  # 이번 브랜치의 커밋 목록
git diff origin/develop...HEAD --stat   # 변경 파일 요약
```

베이스 브랜치는 기본적으로 `develop`이며, 브랜치명이나 사용자 언급에 따라 달라질 수 있습니다.

### 2단계: 정보 수집

브랜치명과 커밋에서 최대한 자동 추론하세요. 부족한 정보만 질문합니다.

**브랜치명 패턴으로 추론:**
- `feat/#123-some-feature` → 이슈 #123, 라벨 `feat`
- `fix/#456-some-bug` → 이슈 #456, 라벨 `fix`
- `refactor/#789-...` → 라벨 `refactor`
- `release/1.x.x` → 라벨 `release`, 베이스 브랜치 `main`

**필수:**
- **제목(Title)**: `[라벨] #이슈번호 작업 내용 한 줄 요약`
- **베이스 브랜치**: 기본 `develop` (release → `main`)

**선택:**
- 작업 설명 (커밋 메시지에서 자동 추출 가능)
- 테스트 내역
- 스크린샷/영상
- 추가 설명 or 리뷰 포인트

### 3단계: PR 본문 작성

아래 템플릿을 반드시 사용하세요:

```markdown
## 🔗 관련 이슈
- Close #<이슈번호>

## 📙 작업 설명
- <변경 내용을 bullet point로 정리>

## 🧪 테스트 내역
- <테스트 항목 없으면 "- 없음">

## 📸 스크린샷 또는 시연 영상
- <없으면 "- 없음">

## 💬 추가 설명 or 리뷰 포인트
- <없으면 "- 없음">
```

- 관련 이슈가 없으면 `🔗 관련 이슈` 섹션 생략
- 테스트/스크린샷/추가 설명은 내용이 없어도 섹션은 반드시 포함하고 `- 없음`으로 채움
- 작업 설명은 커밋 내용과 변경 파일을 보고 최대한 구체적으로 작성

### 4단계: detekt 검사

PR 생성 전에 반드시 detekt를 실행하여 통과 여부를 확인합니다:

```bash
./gradlew detekt --continue 2>&1 | tail -50
```

- **통과 시**: 다음 단계로 진행
- **실패 시**: 오류 메시지에서 파일 경로와 규칙명을 파악하고, 해당 파일을 수정한 뒤 다시 detekt를 실행하여 통과할 때까지 반복합니다. 수정 후에는 커밋도 함께 합니다.

### 5단계: PR 생성

```bash
gh pr create \
  --title "<제목>" \
  --body "$(cat <<'EOF'
<본문>
EOF
)" \
  --base <베이스브랜치> \
  --label "<라벨>"
```

### 6단계: 결과 전달

생성된 PR URL을 사용자에게 알려주세요.

## 제목 형식 예시

| 브랜치 | PR 제목 |
|--------|---------|
| `feat/#168-photo-detail-memo` | `[feat] #168 사진 상세 메모 기능 추가` |
| `fix/#161-archive-result` | `[fix] #161 아카이브 결과 화면 오류 수정` |
| `refactor/#45-mvi-store` | `[refactor] #45 MVI IntentStore 구조 개선` |
| `release/1.1.1` | `[release] v1.1.1` |

## 라벨 컨벤션

| 라벨 | 용도 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 |
| `chore` | 빌드, 설정, 의존성 |
| `docs` | 문서 작성/수정 |
| `design` | UI/UX 변경 |
| `test` | 테스트 추가/수정 |
| `release` | 릴리즈 |

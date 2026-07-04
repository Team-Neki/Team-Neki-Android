---
name: create-issue
description: Create GitHub issues for the current repository. Use this skill whenever the user wants to create, register, or file a GitHub issue — including phrases like "이슈 등록", "이슈 생성", "이슈 만들어줘", "create issue", "github issue", "새 이슈", "버그 리포트", "기능 요청", or "/issue". Also trigger when the user describes a bug, feature request, task, or improvement they want tracked in GitHub, even if they don't explicitly say "issue". This skill handles gathering all required fields, applying the right labels, and returning the created issue URL.
---

# GitHub Issue Creator

현재 저장소(YAPP-Github/27th-App-Team-2-Android)에 GitHub 이슈를 생성합니다.

## 워크플로우

### 1단계: 정보 수집

사용자가 제공하지 않은 필드가 있으면 질문하세요. 이미 충분한 정보가 있다면 바로 생성하세요.

**필수:**
- **제목(Title)**: 이슈를 한 줄로 요약

**선택:**
- **본문(Body)**: 상세 설명, 재현 방법, 기대 동작 등
- **라벨(Labels)**: 아래 컨벤션 참고
- **담당자(Assignees)**: GitHub 사용자명

### 2단계: 라벨 컨벤션

| 라벨 | 용도 |
|------|------|
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 리팩토링 (기능 변경 없음) |
| `chore` | 빌드, 설정, 의존성 등 |
| `docs` | 문서 작성/수정 |
| `design` | UI/UX 디자인 변경 |
| `test` | 테스트 추가/수정 |

사용자가 라벨을 명시하지 않았다면 이슈 내용을 보고 적절한 라벨을 추론해서 적용하세요.

### 3단계: 이슈 생성

```bash
gh issue create \
  --title "<제목>" \
  --body "<본문>" \
  --label "<라벨>" \
  --assignee "<담당자>"
```

본문이 길거나 마크다운이 필요한 경우:

```bash
gh issue create \
  --title "<제목>" \
  --body "$(cat <<'EOF'
## 설명
<내용>

## 재현 방법
1. ...

## 기대 동작
...
EOF
)" \
  --label "<라벨>"
```

### 4단계: 결과 전달

생성된 이슈 URL을 사용자에게 알려주세요.

## 예시

**사용자:** "카카오 로그인 버튼 중복 클릭 버그 이슈 등록해줘"
→ 제목: `[fix] 카카오 로그인 버튼 중복 클릭 이슈`, 라벨: `fix`

**사용자:** "지도 탭에 필터 기능 추가 이슈 만들어줘"
→ 제목: `[feat] 네컷지도 필터 기능 추가`, 라벨: `feat`

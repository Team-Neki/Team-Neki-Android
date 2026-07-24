---
name: git-conventions
description: Use when naming branches, writing commit messages, creating or editing GitHub issues, or creating or editing GitHub pull requests in this repository.
---

# Git Conventions

이 레포에서 브랜치, 커밋, 이슈, PR을 작성할 때 아래 규칙을 적용한다.

## References

- 이슈를 만들거나 수정할 때는 `references/issue.md`를 읽는다.
- PR을 만들거나 수정할 때는 `references/pull-request.md`를 읽는다.

## Type

브랜치와 커밋의 `type`은 작업 성격에 맞춰 아래에서 선택한다.

```text
feat
fix
refactor
design
chore
docs
test
ci
build
init
```

## Branch

작업 브랜치는 아래 형식을 사용한다.

```text
<type>/#<issue-number>-<summary>
```

작성 규칙:

- `<type>`은 소문자로 쓴다.
- type 뒤에는 `/`를 붙인다.
- 이슈 번호 앞에는 `#`를 붙인다.
- 이슈 번호와 summary는 `-`로 구분한다.
- summary는 영어 소문자 kebab-case로 쓴다.
- summary는 작업의 핵심 명사/동사만 짧게 쓴다.
- 여러 단어는 공백 대신 `-`로 연결한다.
- 여러 이슈를 한 브랜치에서 처리하면 이슈 번호를 `,`로 이어 쓴다(예: `fix/#165,#193-qr-scan`).

예:

```text
feat/#217-5th-map-ui
fix/#143-map-bound
refactor/#196-photo-detail-memo-outside-pager
docs/#45-create-md
```

이슈 번호가 없는 작업은 `<type>/<summary>` 형식을 쓴다. 릴리즈 브랜치는 `release/<version>`을 쓴다.

```text
docs/readme
release/1.3.1
```

## Commit

커밋 메시지는 아래 형식을 사용한다.

```text
[<type>] #<issue-number>: <summary>
```

작성 규칙:

- `<type>`은 소문자로 쓰고 `[]`로 감싼다.
- type 뒤에는 공백 하나를 둔다.
- 이슈 번호가 있으면 `#<issue-number>:`를 쓴다.
- 이슈 번호와 summary는 `: `로 구분한다.
- summary는 한국어로 작업 내용을 구체적으로 쓴다.
- summary는 한 줄로 작성한다.
- summary 끝에는 마침표를 붙이지 않는다.

예:

```text
[feat] #209: UserRepository에 마케팅 팝업 노출 기록 기능 추가
[fix] #211: 푸시 알림 본문이 두 줄 이상일 때 잘리는 현상 수정
[refactor] #210: 포즈 상세 북마크 커밋 상태 관리 정리
```

이슈 번호가 없는 작업은 이슈 번호 영역을 뺀다.

```text
[docs] README 수정
[chore] RepositoryModule 선언부 interface로 변경
```

## Issue

- `references/issue.md`를 따른다.

## Pull Request

- `references/pull-request.md`를 따른다.

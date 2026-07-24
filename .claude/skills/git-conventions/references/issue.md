# Issue

- 먼저 해야 할 일을 파악한다.
- 작업 성격에 맞는 `.github/ISSUE_TEMPLATE/` 템플릿을 사용한다.
- 제목 prefix, label, 본문 구조는 템플릿을 따른다.
- 필수 항목은 반드시 채운다.
- 작업 목록은 실제 수행할 일을 체크리스트로 작성한다.
- 템플릿의 placeholder 작업은 실제 작업 목록으로 바꾼다.
- 코드와 요청만으로 채울 수 없는 항목은 사용자에게 확인한다.

## 생성

`gh issue create`로 만든다. 제목은 `[<type>] <summary>` 형식이다.

```bash
gh issue create \
  --title "[<type>] <summary>" \
  --label "<type>" \
  --body "$(cat <<'EOF'
<본문>
EOF
)"
```

생성 후 이슈 URL을 사용자에게 보여준다.

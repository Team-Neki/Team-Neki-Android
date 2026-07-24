# Pull Request

먼저 diff, 커밋, 연결 이슈를 보고 변경 범위와 의도를 파악한다. 본문은 `.github/pull_request_template.md`의 섹션 구조를 사용한다.

## 제목

- `[<type>] #<issue-number> <summary>` 형식으로 쓴다. 커밋과 달리 이슈 번호 뒤에 콜론을 붙이지 않는다.
- summary는 한국어로 쓴다. 이슈 없는 PR은 이슈 번호를 뺀다.

예:

```text
[refactor] #226 5차 지도 UX 개선
[docs] README 수정
```

## 관련 이슈

형식: bullet list

- 연결된 이슈를 `Close #<issue-number>` 형식으로 작성한다.
- 여러 이슈가 연결되면 한 줄에 하나씩 작성한다.

예:

```markdown
- Close #123
- Close #124
```

## 작업 설명

형식: bullet list 또는 소제목 + bullet list

- 작업 내역과 설명을 함께 작성한다.
- 변경 내용을 기능/API/UI/리팩토링/버그 수정 같은 리뷰 단위로 묶는다.
- 연결 이슈의 요구사항을 기준으로 무엇을 구현했는지 드러나게 작성한다.
- 변경 범위가 크면 소제목으로 나눈다.

예:

```markdown
### API 연동
- 포토부스 즐겨찾기 조회 및 추가/해제 API를 연동했습니다.
- 즐겨찾기 활성화 시 서버에서 최신 즐겨찾기 목록을 재조회하도록 처리했습니다.

### UI
- 포토부스 개별 마커에 즐겨찾기 배지를 추가했습니다.
- 브랜드 순서 변경 화면에서 드래그 정렬을 지원하도록 구성했습니다.
```

## 테스트 내역

형식: checkbox list

- 수동 확인은 checkbox list로 작성한다.
- 기기/에뮬레이터 확인은 실행한 시나리오 기준으로 작성한다.
- 템플릿상 선택 항목이다. 확인한 내용이 없으면 생략하거나 `없음`으로 작성한다.

예:

```markdown
- [x] 즐겨찾기 추가 후 UI 즉시 반영 확인
- [x] 즐겨찾기 해제 실패 시 이전 상태로 롤백 확인
```

## 스크린샷 또는 시연 영상

형식: table

- UI 변경이 있으면 `기능 | 미리보기` 표로 작성한다.
- 여러 항목을 보여줄 때는 템플릿의 4열 표를 사용한다.
- 정적인 화면은 `<img>`, 동작 확인이 필요한 화면은 `<video>`를 사용한다.
- UI 변경이 없으면 `없음`으로 작성한다.

예:

```markdown
|기능|미리보기|기능|미리보기|
|:--:|:--:|:--:|:--:|
| 로그인 화면 | <img src="https://github.com/user-attachments/assets/..." width="300" /> | 즐겨찾기 추가 | <video src="https://github.com/user-attachments/assets/..." width="300" /> |
```

## 추가 설명 or 리뷰 포인트

형식: bullet list 또는 소제목 + bullet list

- 작업 내역을 리뷰하기 위해 리뷰어가 알아두면 좋은 맥락을 작성한다.
- 요구사항과 다르게 구현한 부분이 있으면 이유를 작성한다.
- 설계 판단, trade-off, 에러 처리, edge case, 성능/보안/호환성 리스크를 작성한다.
- 중점적으로 봐야 할 파일, 로직, follow-up 이슈를 작성한다.

예:

```markdown
### 상태 동기화
- 즐겨찾기 토글은 UI에 먼저 반영하고, API 실패 시 이전 상태로 롤백하도록 구성했습니다.
- 빠른 연속 클릭 시 서버 상태가 꼬이지 않는지 중점적으로 봐주세요.

### Follow-up
- 지도 하단 패널 로직 분리는 별도 이슈에서 진행할 예정입니다.
```

## Release PR

- 릴리즈 PR은 제목을 `[release] v<version>`으로 쓰고, 본문은 템플릿 하단 주석의 Release 양식을 사용한다.
- base는 `main`이다. 그 외 PR은 base가 `develop`이다.

## 생성

- 브랜치명에서 type과 이슈 번호를 추출해 사용자에게 확인받는다.
- working tree에 커밋되지 않은 변경사항이 있으면 먼저 커밋할지 사용자에게 확인한다.
- 아직 push하지 않았으면 `git push -u origin HEAD`로 push한다.

```bash
gh pr create \
  --base <base> \
  --title "[<type>] #<issue-number> <summary>" \
  --label "<type>" \
  --body "$(cat <<'EOF'
<본문>
EOF
)"
```

생성 후 PR URL을 사용자에게 보여준다.

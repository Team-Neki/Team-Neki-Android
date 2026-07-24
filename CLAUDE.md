# Project Instructions

## Agent Harness

- 이 레포의 프로젝트 전용 harness는 `.claude/skills`와 `.claude/agents`를 기준으로 한다.
- 작업 시작 시 변경 범위에 맞는 skill 전문을 읽고 적용한다.
- 코드 구현, 기존 구현 검증, 개발 완료 판정은 `.claude/skills/verification-loop/SKILL.md`를 따른다.
- QA 시나리오 정리는 `.claude/agents/qa.md`를 따른다.
- PR 생성 전 리뷰는 `.claude/agents/reviewer.md`를 따른다.
- 문서 변경도 같은 harness 기준으로 검토한다.
- harness 자체(skill, agent, `AGENTS.md`, `docs/discussions.md`) 수정은 `.claude/skills/maintaining-harness/SKILL.md`를 따른다.

## Skill Routing

- 구현 완료 후 테스트, 리뷰, QA, 회귀 검증: `.claude/skills/verification-loop/SKILL.md`
- branch, commit, issue, PR: `.claude/skills/git-conventions/SKILL.md`
- 원격 API, Ktor service, DTO, repository: `.claude/skills/implementing-remote-api/SKILL.md`
- domain usecase, model: `.claude/skills/implementing-remote-api/SKILL.md`
- DataStore, Preference, local 저장소: `.claude/skills/implementing-local-preference/SKILL.md`
- `NavKey`, navigator extension, `EntryProvider`, result bus: `.claude/skills/implementing-navigation/SKILL.md`
- Compose UI, component, preview, dialog, bottom sheet: `.claude/skills/implementing-ui/SKILL.md`
- 새 화면 skeleton: `.claude/skills/creating-feature-screen/SKILL.md`
- 기존 화면 기능 추가/수정: `.claude/skills/changing-feature-behavior/SKILL.md`
- Firebase Analytics: `.claude/skills/implementing-analytics/SKILL.md`

## Agents

- agent 원본은 `.claude/agents/<name>.md`다. Codex는 `.codex/agents/<name>.toml`에서 이 파일을 읽어 참조한다.
- `.claude/agents/<name>.md`를 수정하면 `.codex/agents/<name>.toml`의 요약 내용도 최신 상태인지 확인한다.
- verification loop가 `STANDARD` 또는 `FULL`로 분류한 작업은 빠른 검증 통과 후 `reviewer` agent를 사용한다.
- `STANDARD`와 `FULL`의 검증 범위 차이는 verification loop 정의를 따른다.
- reviewer가 `PASS`를 반환하면 `qa` agent를 사용한다.
- QA가 `PASS`를 반환하면 main agent는 verification loop의 완료 판정 기준으로 작업을 종료한다.
- reviewer 또는 QA가 `FIX_REQUIRED`를 반환하면 main agent가 수정하고 verification loop의 복귀 지점부터 다시 실행한다.
- agent 실행 후 코드가 변경되면 변경 전 Reviewer, QA, 검증 결과를 완료 근거로 사용하지 않는다.
- `LIGHT` 작업은 verification loop에 따라 main agent가 관련 검증을 실행하고 종료한다.
- Reviewer와 QA agent는 코드를 수정하지 않고 finding, 검증 결과, 판정을 main agent에 반환한다.
- Reviewer는 `.claude/agents/reviewer.md`를 기준으로 diff, 적용 skill, 영향 범위를 점검한다.
- QA agent는 `.claude/agents/qa.md`를 기준으로 검증 결과와 QA 시나리오를 작성한다.
- QA agent가 확인 질문을 반환하면 main agent는 사용자에게 한 항목씩 묻고 답변을 QA agent에 전달한다.
- PR 생성 전에는 reviewer 결과에서 PR 준비 상태와 적용한 skill을 확인한다.

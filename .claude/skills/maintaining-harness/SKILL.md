---
name: maintaining-harness
description: Use when a project decision changes or the codebase drifts from what a skill or agent documents, and this repository's harness (skills, agents, AGENTS.md, discussions.md) needs to be updated to match.
---

# Maintaining Harness

`.claude/skills`, `.claude/agents`, `.codex/agents`, `AGENTS.md`, `docs/discussions.md`를 최신 상태로 유지하는 절차다. 제품 코드를 검증하는 harness(verification-loop, reviewer, qa)와는 다른 층이므로 섞지 않는다.

## 언제 쓰는가

- 사용자가 명시적으로 harness 점검이나 특정 skill/agent 수정을 요청할 때만 시작한다.
- 코드 변경마다 자동으로 돌지 않는다. 상시 대조는 판단 오류와 불필요한 토큰 비용만 키운다.

## 트리거 두 가지

| 트리거 | 신호 | 절차 |
|---|---|---|
| 기획/컨벤션 변경 | 사용자가 결정 내용을 명시적으로 알려줌 | 기획 변경 절차 |
| 코드베이스 점검 | 사용자가 harness와 실제 코드의 일치 여부 점검을 요청 | 코드베이스 점검 절차 |

## 기획 변경 절차

1. 결정 내용을 확인 가능한 문장으로 정리한다. 모호하면 사용자에게 확인한다.
2. `AGENTS.md`의 Skill Routing과 각 skill 설명으로 영향받는 skill을 찾는다. 여러 skill에 걸치면 전부 나열한다.
3. `docs/discussions.md`에 같은 주제의 미결정 항목이 있으면 함께 본다.
4. 영향받는 skill 파일을 수정한다. 팀이 확정한 결정이므로 기존 사례를 병기하지 않고 새 기준만 남긴다.
5. `docs/discussions.md`에 같은 주제 항목이 있으면 해결됐다고 지우거나 결정 내용으로 갱신한다.
6. `.claude/agents/reviewer.md`, `.claude/agents/qa.md`의 Skill 선택 표나 리뷰 관점이 이 결정과 관련되면 함께 갱신한다.
7. 변경한 skill의 예시 코드가 있으면 새 결정과 일치하는지 확인한다.

## 코드베이스 점검 절차

skill 문서가 서술하는 패턴이 실제 코드와 맞는지 확인한다. 추측하지 않고 코드를 읽어서 검증한다.

1. 점검 범위를 정한다. 사용자가 영역을 지정하면 그 범위만, 아니면 전체 skill을 대상으로 한다.
2. 각 skill이 서술하는 구체적 패턴(파일 경로, 클래스/함수 이름, 명명 규칙, 호출 순서)을 코드베이스에서 grep과 Read로 확인한다.
3. skill 서술과 코드가 다르면 실수(한 곳만 다름)인지 컨벤션 전환(여러 곳이 새 패턴)인지 실제 코드 근거로 판단한다. 근거 없이 추측하지 않는다.
4. 판단이 서지 않거나 team 합의가 필요한 항목은 바로 고치지 않고 `docs/discussions.md`에 현재 코드 기준과 논의 지점을 추가한다.
5. skill이 명백히 틀렸거나 낡았을 때(코드가 이미 전부 새 패턴으로 넘어간 경우)만 사용자 확인 후 수정한다.
6. harness 밖의 관련 문서(`GA4_EVENTS.md` 등)에서도 코드와 다른 사실을 발견하면 코드와 대조해 수정 여부를 사용자에게 확인한다.

## 일관성 확인

skill이나 agent 파일을 수정한 뒤 아래를 확인한다.

- `.claude/agents/<name>.md`가 원본이므로, `.codex/agents/<name>.toml`의 `developer_instructions`가 같은 내용을 요약하고 있는지 확인한다. 서로 모순되면 안 된다.
- 새로 언급한 skill/agent 경로가 `AGENTS.md`, `reviewer.md`, `qa.md`, `reviewer.toml`, `qa.toml`의 관련 표에도 반영됐는지 확인한다.
- `.claude/agents/*.md`의 frontmatter(`name`, `description`, `tools`)가 유효한지 확인한다.
- `.codex/agents/*.toml`이 파싱되는지 확인한다.
- `.agents/skills` 심볼릭 링크가 깨지지 않았는지 확인한다.

## 하지 않는 것

- 코드 변경마다 자동으로 도는 훅이나 실시간 대조를 만들지 않는다.
- 한 곳의 예외적 구현만 보고 skill을 바로 고치지 않는다. 여러 사례나 명시적 결정이 근거일 때만 고친다.
- 사용자 확인 없이 팀 컨벤션(discussions.md의 미결정 항목, 코드 동작 스펙)을 단정해 수정하지 않는다.
- 작은 수정에 새 섹션, 새 표, 새 분류 체계를 얹지 않는다. 필요한 줄만 고친다.

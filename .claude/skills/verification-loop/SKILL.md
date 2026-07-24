---
name: verification-loop
description: Use after implementing an Android code change, when verifying an existing implementation, or before declaring development complete. Select project skills, run risk-based verification, coordinate Reviewer and QA agents, and repeat only when evidence requires a fix.
---

# Verification Loop

구현, 자동 검증, 리뷰, QA를 변경 위험도에 맞게 실행하고 검증된 상태에서 작업을 종료한다.

## 시작

1. 요청, 현재 branch, working tree, `origin/develop` 기준 diff를 확인한다.
2. 구현 여부와 관계없이 요청한 동작과 완료 조건을 정리한다.
3. 변경 파일에서 수정된 symbol, 직접 호출자, 공유 계약 사용처, 관련 화면과 진입 경로를 따라 영향 범위를 정한다.
4. `AGENTS.md`의 Skill Routing에 따라 변경 범위에 해당하는 skill 전문을 읽는다.
5. 위험도를 `LIGHT`, `STANDARD`, `FULL` 중 하나로 정한다.
6. Android runtime 동작을 변경하거나 실제 기기 확인으로 성공을 판별할 수 있는 작업은 구현 전에 Runtime QA 진행 여부를 사용자에게 묻는다. 사용자가 이미 정했다면 그 답을 사용한다.
7. 구현이 필요하면 선택한 skill을 적용해 구현하고 빠른 검증으로 이동한다. 구현이 끝난 작업은 바로 빠른 검증으로 이동한다.

## 위험도

변경 줄 수보다 계약의 공유 범위와 기존 동작에 미치는 영향을 기준으로 정한다. 검증 깊이는 변경 크기와 위험도에 비례한다.

| 단계 | 기준 | 실행 |
|---|---|---|
| `LIGHT` | 문서, 주석, 내부 이름, 문구·시각 요소, 기계적 정리처럼 사용자 flow와 state/data/navigation 계약이 바뀌지 않는 국소 수정 | main agent가 빠른 검증(변경 모듈 compile·detekt 수준)과 점검만 실행. Reviewer/QA 호출 없음 |
| `STANDARD` | feature 내부 UI interaction, ViewModel, API 사용, analytics 등 feature 동작이 바뀌는 수정 | 빠른 검증, Reviewer, QA, 최종 확인 |
| `FULL` | 공유 core 계약, navigation/result, auth/token, local 저장, permission, 여러 모듈에 걸친 변경 | 빠른 검증, Reviewer, QA, 영향 모듈 검증을 포함한 최종 확인 |

작은 diff라도 공유 API나 navigation 계약을 바꾸면 `FULL`로 본다. 확인 과정에서 영향 범위가 넓어진 근거가 나오면 단계를 올린다.
Runtime QA를 진행하기로 한 작업은 QA agent가 실행할 수 있도록 최소 `STANDARD`로 본다. 문서처럼 runtime 동작이 없는 `LIGHT` 작업은 Runtime QA를 제외한다.
agent 간 왕복이 검증 비용의 대부분이므로 `LIGHT`는 어떤 경우에도 Reviewer와 QA를 호출하지 않는다.

## 완료 조건

구현 전에 아래 내용을 확인 가능한 문장으로 정리한다.

- 요청한 기능 또는 수정 동작
- 유지되어야 하는 기존 동작
- 변경된 계약의 직접 사용처와 영향받는 진입 경로
- 적용할 project skill
- 자동 검증 범위
- Runtime QA 선택 결과와 대상 시나리오

코드와 요청만으로 정할 수 없는 항목은 사용자에게 한 번에 하나씩 묻는다.

## 빠른 검증

변경을 가장 빠르게 판정할 수 있는 검증부터 실행한다.

- 변경 모듈 compile
- 변경 모듈 detekt
- 관련 unit test가 있으면 실행. instrumentation test는 기기가 필요하므로 Runtime QA 결정에 따른다
- 문서 작업은 별도 검증 도구가 없으므로 참조 경로와 명령의 실재 여부를 직접 확인

변경과 관련 없는 전체 명령은 어떤 단계에도 추가하지 않는다. `FULL`도 영향 모듈 범위까지만 확장한다. 실패하면 원인을 수정하고 실패한 명령과 그 선행 검증부터 다시 실행한다.

## Agent 입력

Reviewer와 QA를 호출할 때 아래 정보를 전달한다.

- 요청과 완료 조건
- 위험도
- Runtime QA 선택 결과
- 기준 branch와 diff 범위
- 변경 파일과 영향 범위
- 적용한 project skill
- 마지막으로 성공한 검증 명령과 결과

두 agent는 전달받은 skill 전문과 실제 diff를 함께 확인한다.

## 흐름

```text
작업 시작
  -> 완료 조건 · 영향 범위 · 위험도 · 적용 skill
  -> 구현이 필요하면 Main 구현
  -> 빠른 검증
       ├─ 실패 -> Main 수정 -> 빠른 검증
       ├─ LIGHT 통과 -> PASS 조건 확인
       └─ STANDARD/FULL 통과 -> Reviewer
            ├─ FIX_REQUIRED -> Main 수정 -> 빠른 검증
            └─ PASS -> QA (선택한 Runtime QA 포함)
                 ├─ FIX_REQUIRED -> Main 수정 -> 빠른 검증 -> Reviewer -> QA
                 └─ PASS -> 최종 확인
                      ├─ 누락 검증 실패 -> Main 수정 -> 빠른 검증 -> Reviewer -> QA -> 최종 확인
                      └─ 통과 -> PASS
```

## 복귀 지점

- 빠른 검증 실패: main agent가 수정하고 빠른 검증을 다시 실행한다.
- Reviewer `FIX_REQUIRED`: main agent가 수정한 뒤 빠른 검증부터 다시 실행하고 Reviewer를 다시 호출한다.
- QA `FIX_REQUIRED`: main agent가 수정한 뒤 빠른 검증, Reviewer, QA 순서로 다시 실행한다.
- 최종 확인에서 누락된 검증이 실패하면 main agent가 수정한 뒤 빠른 검증, Reviewer, QA, 최종 확인 순서로 다시 실행한다.
- Reviewer 또는 QA `BLOCKED`: main agent가 필요한 조건을 사용자에게 보고하고, 확보되면 중단 지점부터 다시 호출한다.
- 코드가 변경되면 변경 전에 얻은 compile, test, review, QA 결과는 PASS 근거에서 제외한다.
- 코드가 변경되지 않았으면 이미 통과한 검증을 다시 실행하지 않는다.

Reviewer와 QA는 첫 통과에서 각각 한 번 호출한다. 반복 횟수를 채우기 위한 재호출은 없으며 실패, finding, 검증 후 코드 변경이 있을 때만 다시 호출한다.

## 최종 확인

Reviewer와 QA 이후 PASS 증거가 모두 최신인지 확인한다. 같은 diff에서 이미 성공한 명령은 다시 실행하지 않고, 누락된 검증만 실행한다. 마지막 검증 이후 코드가 변경됐으면 빠른 검증부터 다시 시작한다. `LIGHT`는 Reviewer와 QA 없이 PASS 조건의 해당 항목만 확인한다.

## 회귀 점검

아래 순서로 수정 범위 밖의 영향을 확인한다.

```text
변경 파일
-> 변경된 symbol과 계약
-> 직접 호출자와 의존 모듈
-> 관련 화면과 진입 경로
-> 유지되어야 하는 기존 동작
-> 회귀 검증 시나리오
```

공유 계약의 producer, consumer, relay가 있으면 함께 확인한다. 변경된 동작과 같은 state, 저장소, navigation, result를 사용하는 기존 기능을 회귀 대상으로 포함한다.

## PASS 조건

아래 조건을 모두 만족한 최신 결과가 있어야 `PASS`로 판정한다.

- 요청한 완료 조건을 충족한다.
- 적용한 project skill의 구조와 convention을 충족한다.
- 영향 범위의 직접 호출자와 공유 계약 사용처를 확인했다.
- 영향받는 기존 진입 경로와 주요 동작에서 회귀가 발견되지 않았다.
- 변경에 적용되는 compile, detekt, test 또는 문서 검증이 마지막 수정 이후 통과했다.
- `STANDARD`와 `FULL`은 Reviewer의 미해결 High/Medium finding이 없다.
- `STANDARD`와 `FULL`은 QA가 실행 대상으로 정한 자동·코드 검증을 통과했다.
- Runtime QA를 진행하기로 한 작업은 대상 시나리오를 통과했다.
- 요청 범위와 무관한 diff가 없다.
- 확인된 범위 밖의 기존 계약과 동작에 알려진 회귀가 없다.

## 상태

- `PASS`: 모든 성공 조건을 최신 검증 결과로 확인한 상태
- `FIX_REQUIRED`: 구현 또는 검증 실패를 수정하고 정해진 지점부터 다시 실행할 상태
- `BLOCKED`: 사용자 답변, 계정, 서버, 기기, 권한 또는 외부 환경이 있어야 검증을 계속할 수 있는 상태

같은 원인의 실패가 세 번 반복되면 `BLOCKED`로 판정하고 실패 명령, 핵심 오류, 시도한 수정, 필요한 조건을 전달한다.

## 결과

작업 종료 시 아래 항목을 간결하게 보고한다.

- 상태와 위험도
- 적용한 skill
- 변경 범위와 확인한 회귀 범위
- 실행한 검증과 결과
- Reviewer와 QA 판정 또는 `LIGHT` 제외 결과
- Runtime QA 결과 또는 제외 사유
- 남은 위험이나 blocker

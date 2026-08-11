# SPEC Documents Directory

이 디렉토리는 EARS 형식의 SPEC 문서를 저장합니다.

## Directory Structure

```
.kroot/specs/
├── README.md                    # This file
└── SPEC-{DOMAIN}-{NUMBER}/      # SPEC document directory
    ├── spec.md                  # EARS requirements
    ├── plan.md                  # Implementation plan
    └── acceptance.md            # Acceptance criteria
```

## SPEC ID Format

- **Pattern**: `SPEC-{DOMAIN}-{NUMBER}`
- **Domain**: Uppercase letters (AUTH, USER, API, DB, UI, PERF, SEC, etc.)
- **Number**: 3-digit zero-padded (001, 002, etc.)

### Examples

```
SPEC-AUTH-001/   # Authentication feature
SPEC-USER-002/   # User management feature
SPEC-API-003/    # API endpoint feature
```

## Creating a New SPEC

SPEC 문서는 KRoot Pipeline의 S2 기능정의서 단계에서 생성합니다. Claude Code에서 다음처럼 요청하세요.

```text
KRoot Pipeline S02 기능정의서 단계 실행해줘.
인증 기능을 SPEC-AUTH-001로 정리해줘.
```

S2를 실행하기 전에는 현재 프로젝트 워크플로우에서 S02 단계가 활성화되어 있어야 합니다. 활성화되지 않은 단계는 agent를 실행하지 않습니다.

## 3-File Structure

### spec.md (Requirements)

EARS format specification:
- Metadata (ID, Title, Status, Priority)
- Environment and Assumptions
- Requirements (5 EARS patterns)
- Specifications and Traceability

### plan.md (Implementation Plan)

- Milestones by priority
- Technical approach
- Implementation phases
- Risks and mitigations

### acceptance.md (Acceptance Criteria)

- Success criteria
- Test scenarios (Given-When-Then)
- Quality gates
- Definition of Done

## EARS Patterns

1. **Ubiquitous**: 시스템은 항상 [동작]해야 한다
2. **Event-driven**: WHEN [이벤트] THEN [동작]
3. **State-driven**: IF [조건] THEN [동작]
4. **Unwanted**: 시스템은 [동작]하지 않아야 한다
5. **Optional**: 가능하면 [동작]을 제공한다

## Workflow

```
S01 요구사항 분석
        ↓
S02 기능정의서/SPEC 생성
        ↓
S09 Frontend 구현 + S10 Backend 구현
        ↓
S11 Test(QC)
        ↓
S12 배포/문서 동기화
```

## Important Rules

1. **No Flat Files**: `.kroot/specs/SPEC-*.md` 단일 파일 금지
2. **3-File Required**: 모든 SPEC은 3파일 구조 필수
3. **EARS Format**: 요구사항은 EARS 패턴 사용
4. **Unique IDs**: SPEC ID는 고유해야 함

---

Version: 1.0.0
Last Updated: 2026-01-22

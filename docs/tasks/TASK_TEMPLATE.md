---
id: TASK-XXXX
title: Short task title
status: READY
priority: P1
branch: feat/TASK-XXXX-short-name
created: YYYY-MM-DD
updated: YYYY-MM-DD
owner: developer
codex:
  implementation_model: gpt-5.6-terra
  reasoning: medium
  reviewer_model: gpt-5.6-sol
  escalation_trigger: "Security risk, architecture conflict, or two failed focused attempts"
  may_commit: false
---

# TASK-XXXX — Short task title

## Business outcome

Describe the user/business result in one short paragraph.

## Context to read

- `AGENTS.md`
- `docs/PROJECT_CONTEXT.md`
- Add only task-relevant files here.

## In scope

- Exact behavior to implement.
- Exact module or endpoint.
- Tests required.

## Out of scope

- Explicitly excluded adjacent features.
- No deployment unless stated.
- No unrelated refactor.

## Acceptance criteria

- [ ] Observable criterion 1.
- [ ] Observable criterion 2.
- [ ] Failure behavior is defined and tested.
- [ ] Required tests pass.
- [ ] No secrets or unrelated changes are included.

## Expected files/modules

Likely files only; Codex may adjust after inspection but must explain deviations.

```text
backend/...
docs/...
```

## Technical constraints

- Architecture/integration constraints.
- Validation and error-handling rules.
- Database migration requirements.

## Required tests

- Unit tests:
- Integration tests:
- Regression cases:

## Verification commands

```powershell
cd backend
.\mvnw.cmd clean verify
```

## Commit

Suggested message:

```text
feat(scope): concise outcome
```

Codex may commit only after explicit user approval.

## Completion record

Fill after implementation.

- Result:
- Files changed:
- Tests:
- Commit:
- Known limitations:
- Follow-up task IDs (do not implement now):

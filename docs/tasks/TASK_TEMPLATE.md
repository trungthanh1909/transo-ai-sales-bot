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
  reviewer_reasoning: high
  escalation_trigger: "Security risk, architecture conflict, or two failed focused attempts"
  may_commit: false
documentation:
  adr_required: false
  changelog_required: false
---

# TASK-XXXX — Short task title

## 1. Business outcome

Describe the observable user or business result in one short paragraph. Explain why this task matters without turning this section into a full project overview.

## 2. Context to read

Required:

- `AGENTS.md`
- `docs/PROJECT_CONTEXT.md`
- This task file

Task-specific references only:

- Add architecture, ADR, integration, API, or source files that are directly relevant.
- Do not add every document in the repository.

## 3. Current state

Describe only the existing behavior or repository state needed to understand the task.

- What already exists:
- What is missing or incorrect:
- Relevant dependency or prerequisite:

## 4. In scope

- Exact behavior to implement.
- Exact endpoint, module, command, migration, or document to change.
- Required success behavior.
- Required failure behavior.
- Tests required by this task.

## 5. Out of scope

- Explicitly excluded adjacent features.
- No deployment unless stated.
- No unrelated refactor.
- No new dependency or architecture change unless stated.
- No follow-up task implementation.

## 6. Acceptance criteria

- [ ] Observable criterion 1.
- [ ] Observable criterion 2.
- [ ] Important failure behavior is defined and tested.
- [ ] Required tests and verification commands pass.
- [ ] No secrets, unrelated changes, or out-of-scope features are included.
- [ ] Task record is updated concisely before the approved commit.
- [ ] ADR is created or updated when `adr_required: true`.
- [ ] Changelog is updated only when `changelog_required: true`.

## 7. Expected files or modules

List likely files only. Codex may adjust after inspecting the repository but must explain material deviations.

```text
backend/...
infra/...
docs/...
```

## 8. Technical constraints

- Architecture and module-boundary constraints.
- Validation and error-handling rules.
- Security requirements.
- Database and Flyway requirements.
- Idempotency or retry requirements.
- Dependency restrictions.

## 9. Required tests

### Unit tests

- Test case:

### Integration tests

- Test case:

### Regression and failure cases

- Test case:

Use `Not required` only with a brief reason.

## 10. Verification commands

Use commands that can be run from the stated working directory.

```powershell
cd backend
.\mvnw.cmd clean verify
```

Optional local runtime checks:

```powershell
docker compose -f infra/docker-compose.yml config
docker compose -f infra/docker-compose.yml up -d
docker compose -f infra/docker-compose.yml ps
```

## 11. Stop and escalation conditions

Stop and report when:

- a required secret, external account, or permission is unavailable;
- a business rule is undefined;
- instructions conflict materially;
- a destructive data operation is required but not explicitly approved;
- two focused attempts fail without new evidence;
- the task cannot be completed without expanding scope.

## 12. Commit

Suggested message:

```text
feat(scope): concise outcome
```

Codex may commit only after explicit user approval, even when `may_commit: true` indicates that committing is permitted for this task.

## 13. Work-in-progress handoff

Use this section only when the task is interrupted or blocked. Keep it concise and factual. Replace stale content instead of appending a diary.

- Current state:
- Completed:
- Remaining:
- Tests and verification already run:
- Blocker or last relevant error:
- Working-tree state:
- Safe next action:

When no handoff is needed, write:

```text
Not applicable.
```

## 14. Completion record

Fill this section only when implementation and verification are complete. Do not include internal reasoning or a chronological transcript.

- Result:
- Principal files or modules changed:
- Tests and verification results:
- Commit: `Not committed yet` or commit hash if available without an extra commit
- Known limitations:
- Follow-up task IDs, without implementing them:
- ADR updated: `No` or path
- Changelog updated: `No` or path

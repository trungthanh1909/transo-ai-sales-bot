# Reusable Codex Prompts

Replace placeholders such as `<TASK_FILE>`, `<TASK_ID>`, `<FAILURE>`, and `<COMMIT_MESSAGE>` before use.

## 1. Plan only — new task

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and <TASK_FILE>.
Then inspect only source or documentation files required by that task.

Do not edit yet.

Return:
1. your understanding of the task goal,
2. in-scope and out-of-scope work,
3. files or modules you expect to inspect or change,
4. a bounded implementation plan,
5. tests and verification commands,
6. risks, blockers, or instruction conflicts.

Flag any conflict with AGENTS.md or accepted ADRs.
Do not propose adjacent features.
Do not start another task.
```

## 2. Resume an interrupted task

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and <TASK_FILE>.
Read the task's Work-in-progress handoff.
Inspect:
- git status,
- git diff --check,
- git diff,
- git log -5 --oneline.

Do not edit yet.

Return:
1. what is already complete,
2. what remains,
3. whether the handoff matches the current working tree,
4. the smallest safe continuation plan,
5. tests that must be rerun,
6. any blocker or unexplained change.

Use the repository as the source of truth, not memory of an earlier chat.
Do not expand scope or start another task.
```

## 3. Implement

```text
Proceed with <TASK_ID> exactly as written.
Implement the smallest complete change satisfying every acceptance criterion.
Add or update the required tests.
Run focused tests during implementation and all task verification commands before reporting completion.

Do not commit, push, merge, deploy, or start another task.
Do not update CHANGELOG.md unless the active task sets changelog_required: true.
Stop and create a concise Work-in-progress handoff if a required secret, external permission, unavailable dependency, business decision, or material instruction conflict blocks progress.
```

## 4. Focused fix

```text
The following acceptance criterion or test is failing:

<FAILURE>

Diagnose only this failure.
Inspect the smallest relevant set of files.
Fix the root cause without unrelated refactoring.
Run the focused failing test first, then rerun the task verification commands.
Do not commit or start another task.
```

## 5. Pause and record a handoff

```text
Pause the active task.
Do not perform more feature work.

Update the Work-in-progress handoff in <TASK_FILE> with only:
1. current state,
2. completed work,
3. remaining work,
4. tests and verification already run with results,
5. blocker or last relevant error,
6. current working-tree state,
7. the safest next action.

Set status to IN_PROGRESS if work can resume normally, or BLOCKED if external input or access is required.
Replace stale handoff content instead of appending a diary.
Do not include internal reasoning or a chronological conversation summary.
Do not commit unless explicitly authorized.
Do not start another task.
```

## 6. Self-review

```text
Stop feature work and review the current diff against <TASK_FILE>, AGENTS.md, and any ADR explicitly linked by the task.

Check:
- scope creep,
- correctness and failure behavior,
- missing or weak tests,
- security issues,
- accidental secret exposure,
- sensitive-data logging,
- idempotency and retry behavior,
- schema changes without a new Flyway migration,
- unnecessary dependencies,
- vendor coupling,
- unrelated refactoring.

Fix only verified issues belonging to this task.
After any fix, rerun the focused tests and all task verification commands.
Then summarize git status, the final diff, and test results.
Do not commit.
```

## 7. Update completion record — before commit

```text
Implementation and verification for <TASK_ID> are complete.
Do not add new feature work.

Update the Completion record in <TASK_FILE> with:
1. concise result,
2. principal files or modules changed,
3. exact tests and verification commands with results,
4. known limitations,
5. follow-up task IDs without implementing them,
6. ADR and changelog status.

Clear obsolete Work-in-progress handoff content or mark it Not applicable.
Keep the record concise and factual.
Do not include internal reasoning, failed attempts, or a chronological session log.
Do not commit yet.
```

## 8. Final pre-commit check

```text
Perform a final pre-commit check for <TASK_ID>.

Run or inspect:
- git status,
- git diff --check,
- git diff --stat,
- every verification command required by the task.

Confirm:
- all acceptance criteria are satisfied,
- the Completion record is accurate,
- no secret or unrelated file is included,
- no changelog or ADR was changed unless required.

Report findings only.
Do not edit or commit unless a verified issue must be fixed; if you fix one, rerun verification and report the new result.
```

## 9. Commit and stop

```text
The diff and Completion record are approved.
Create exactly one commit for <TASK_ID> with message:

<COMMIT_MESSAGE>

After committing:
1. show the commit hash,
2. show final git status,
3. report the tests that passed,
4. report any accepted limitation.

Do not push, merge, deploy, modify more files, amend the commit, or start another task.
End with:
Task stopped; no next task started.
```

## 10. Review only with a stronger model

```text
Review commit <HASH> against <TASK_FILE>, AGENTS.md, and any ADR explicitly linked by the task.
Do not edit files.

Prioritize:
- correctness,
- security,
- regression risk,
- missing tests,
- architecture violations,
- data integrity,
- scope creep.

Report findings by severity with file and line references.
Separate confirmed defects from residual risks or optional improvements.
If there are no material findings, state that clearly.
```

## 11. Create a follow-up task without implementing it

```text
Based on the completed work and known limitations of <TASK_ID>, draft one bounded follow-up task using docs/tasks/TASK_TEMPLATE.md.

The new task must include:
- business outcome,
- exact scope and exclusions,
- acceptance criteria,
- required tests,
- verification commands,
- model routing,
- ADR/changelog requirements.

Do not implement it.
Do not modify the completed task except to add the new task ID to its Completion record when explicitly authorized.
```

## 12. Changelog update — release task only

```text
The active task explicitly sets changelog_required: true.
Update CHANGELOG.md with only the user-visible release change introduced by <TASK_ID>.
Follow the existing changelog format.
Do not include internal implementation details, session history, per-file lists, reasoning, or work-in-progress notes.
Do not change unrelated release entries.
```

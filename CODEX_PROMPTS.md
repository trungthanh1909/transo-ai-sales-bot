# Reusable Codex Prompts

## Plan only

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and <TASK_FILE> only, plus files directly linked by the task. Do not edit yet.
Return: goal, scope, expected files, implementation steps, tests, verification commands, and risks.
Flag any conflict with AGENTS.md. Do not propose adjacent features.
```

## Implement

```text
Proceed with <TASK_ID> exactly as written. Make the smallest complete change satisfying all acceptance criteria. Add or update required tests. Run the task's verification commands. Do not commit, push, merge, deploy, or start another task.
```

## Focused fix

```text
The following acceptance criterion or test is failing:
<PASTE FAILURE>
Diagnose only this failure. Inspect the smallest relevant set of files, fix the root cause, rerun the focused test, then rerun the task verification commands. Do not refactor unrelated code.
```

## Self-review

```text
Stop feature work and review the current diff against <TASK_FILE> and AGENTS.md. Check scope creep, correctness, missing tests, security, secret exposure, logging of sensitive data, idempotency, database migrations, and vendor coupling. Fix only verified issues in this task, rerun tests, then summarize the final diff. Do not commit.
```

## Commit and stop

```text
The diff is approved. Create exactly one commit for <TASK_ID> with message:
<COMMIT_MESSAGE>
Then show the commit hash and final git status. Do not push, merge, deploy, modify more files, or start another task. End with: Task stopped; no next task started.
```

## Review only with a stronger model

```text
Review commit <HASH> against <TASK_FILE> and AGENTS.md. Do not edit files. Prioritize correctness, security, regression risk, missing tests, architecture violations, and scope creep. Report findings by severity with file and line references. If there are no material findings, state that clearly and list residual risks.
```

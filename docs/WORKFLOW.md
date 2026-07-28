# Codex Task Workflow

## Principle

One task, one branch, one bounded context window, one verification cycle, and normally one commit. A completed task is closed before another begins.

## Repository states

```text
BACKLOG -> READY -> IN_PROGRESS -> VERIFYING -> DONE
                                      |
                                      -> BLOCKED
```

Only one task may be `IN_PROGRESS` at a time for a developer/branch.

## Step 1: Prepare the task

Create a task from `docs/tasks/TASK_TEMPLATE.md`. It must contain:

- Business outcome.
- Exact scope and exclusions.
- Acceptance criteria.
- Expected files/modules.
- Required tests.
- Verification commands.
- Chosen model and reason.
- Whether Codex may commit.

## Step 2: Start a clean branch

```powershell
git switch main
git pull --ff-only
git switch -c feat/TASK-0001-bootstrap-backend
git status
```

Do not run `git pull` when local uncommitted changes exist.

## Step 3: Start Codex

From the repository root:

```powershell
codex
```

In an interactive session, use `/model` to select the model and reasoning level available in your account.

First prompt:

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and docs/tasks/TASK-XXXX-title.md only.
Do not edit yet.
Return:
1. your understanding of the goal,
2. files you expect to inspect or change,
3. implementation steps,
4. tests and verification commands,
5. risks or ambiguities.
Do not expand scope and do not start another task.
```

## Step 4: Authorize implementation

After reviewing the plan:

```text
Proceed with TASK-XXXX exactly as written.
Implement the smallest complete change that satisfies every acceptance criterion.
Run the required tests.
Do not commit yet.
Stop and report if a required secret, account permission, external API access, or business decision is missing.
```

## Step 5: Review before commit

Ask Codex:

```text
Stop editing. Review your own diff against TASK-XXXX and AGENTS.md.
Check for scope creep, missing tests, security issues, accidental secret exposure, schema changes without Flyway, and vendor coupling.
Run the verification commands again if any code changed during review.
Then summarize git status and the diff. Do not commit.
```

Manually inspect:

```powershell
git status
git diff --check
git diff
git diff --stat
```

## Step 6: Commit and stop

Only after manual approval:

```text
The diff is approved. Create exactly one commit for TASK-XXXX using this message:
<type>(<scope>): <summary>

After committing, show the commit hash and final git status.
Do not push, merge, deploy, or start another task.
```

Verify:

```powershell
git log -1 --oneline
git status
```

## When a task is blocked

Do not invent credentials or silently replace a real integration with a fake one unless the task permits it. Record:

- What is blocked.
- Evidence/error message.
- What was completed safely.
- Exact input or permission needed.
- Whether the working tree is clean.

## Context reset rule

Start a new Codex chat when:

- A task is committed.
- The next task concerns a different module.
- The conversation accumulated failed approaches or contradictory instructions.
- The model repeatedly edits outside scope.

Reuse the same chat only for fixing review findings inside the same task.

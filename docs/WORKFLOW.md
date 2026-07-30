# Codex Task Workflow

## 1. Principle

Use one task, one branch, one bounded context, one verification cycle, and normally one commit.

A task must be closed, paused, or marked blocked before another task begins. Chat context is temporary; the repository is the durable record.

## 2. Repository task states

```text
BACKLOG -> READY -> IN_PROGRESS -> VERIFYING -> DONE
                         |             |
                         +-----------> BLOCKED
```

Definitions:

- `BACKLOG`: idea exists but is not sufficiently specified.
- `READY`: scope, acceptance criteria, tests, and constraints are defined.
- `IN_PROGRESS`: one branch/session is actively implementing the task.
- `VERIFYING`: implementation is complete and is undergoing tests and review.
- `BLOCKED`: external access, a decision, a dependency, or a material conflict prevents progress.
- `DONE`: acceptance criteria, verification, review, task record, and approved commit are complete.

Only one task may be `IN_PROGRESS` for the same developer and branch.

## 3. Step 0 — Prepare the repository once

Place the workflow documents in the repository root and `docs/` directory.

The initial repository may contain only documentation. The first bootstrap task may create `backend/`, `infra/`, Maven Wrapper, Docker Compose, and other code directories.

Create an initial documentation commit before starting implementation:

```powershell
git add AGENTS.md CODEX_PROMPTS.md README.md docs
git commit -m "docs: add project and Codex workflow"
```

Do not create empty source folders merely to imitate the future tree unless a task requires them.

## 4. Step 1 — Prepare one task

Create a task from `docs/tasks/TASK_TEMPLATE.md`.

A task must define:

- business outcome;
- exact in-scope behavior;
- explicit exclusions;
- acceptance criteria;
- expected files or modules;
- technical constraints;
- required tests;
- verification commands;
- selected implementation and reviewer models;
- whether Codex may commit;
- whether a changelog or ADR update is required.

A task is ready only when another session can understand what success means without reading the original chat conversation.

Prefer tasks that can be implemented and verified in one focused session. Split broad features into independently testable tasks.

## 5. Step 2 — Start a clean branch

Example:

```powershell
git switch main
git pull --ff-only
git switch -c <type>/TASK-XXXX-short-name
git status
```

Rules:

- Do not run `git pull` with uncommitted changes.
- Do not reuse a branch containing unrelated work.
- Use one branch per task unless the user explicitly chooses a different strategy.

Set the task status to `IN_PROGRESS` and update the date when work begins.

## 6. Step 3 — Start Codex with bounded context

From the repository root:

```powershell
codex
```

Use `/model` in the interactive session to select a model and reasoning level available in the account.

First prompt:

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and docs/tasks/TASK-XXXX-title.md only.
Then inspect only source or documentation files required by that task.

Do not edit yet.

Return:
1. your understanding of the task goal,
2. in-scope and out-of-scope work,
3. files or modules you expect to inspect or change,
4. a bounded implementation plan,
5. tests and verification commands,
6. risks, blockers, or instruction conflicts.

Do not expand scope and do not start another task.
```

If resuming interrupted work, also instruct Codex to read the task's `Work-in-progress handoff` and inspect `git status`, `git diff`, and recent commits before proposing the continuation plan.

## 7. Step 4 — Review the plan

The user reviews the plan before allowing edits.

Reject or correct the plan when it:

- includes work listed as out of scope;
- adds unnecessary infrastructure or dependencies;
- changes architecture without an ADR requirement;
- relies on unavailable credentials;
- lacks tests or failure behavior;
- modifies unrelated files.

Do not authorize implementation until the plan is bounded and verifiable.

## 8. Step 5 — Authorize implementation

Use:

```text
Proceed with TASK-XXXX exactly as written.
Implement the smallest complete change that satisfies every acceptance criterion.
Add or update the required tests and run the task's verification commands.
Do not commit yet.
Do not push, merge, deploy, or start another task.
Stop and record a handoff if a required secret, external permission, unavailable dependency, or business decision blocks progress.
```

During implementation, Codex may run focused tests and inspect relevant files. It must not broaden the task.

## 9. Step 6 — Handle interruption or token/context limits

Do not rely on the current Codex conversation being available later.

Before intentionally ending an unfinished session, ask Codex to update the active task's `Work-in-progress handoff`.

The handoff must contain only:

- current state;
- completed work;
- remaining work;
- verification already run and results;
- blocker or last relevant error;
- working-tree state.

Example continuation procedure in a new Codex session:

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, and <ACTIVE_TASK_FILE>.
Read the Work-in-progress handoff.
Inspect git status, git diff --check, git diff, and git log -5 --oneline.
Do not edit yet.
Summarize what is complete, what remains, and the smallest safe continuation plan.
```

Use source code, tests, Git, and the task file as the source of truth. Do not reconstruct state from memory.

## 10. Step 7 — Self-review and verification

When implementation appears complete, set the task status to `VERIFYING`.

Ask Codex:

```text
Stop feature work. Review the current diff against the active task and AGENTS.md.
Check for scope creep, correctness issues, missing tests, security problems, accidental secrets, sensitive logging, missing Flyway migrations, broken idempotency, unnecessary dependencies, and vendor coupling.
Fix only verified issues belonging to this task.
Run the focused tests and all task verification commands again after any change.
Do not commit.
```

Then manually inspect:

```powershell
git status
git diff --check
git diff
git diff --stat
```

Use IntelliJ's Git diff view for line-by-line review.

## 11. Step 8 — Record completion before commit

When the implementation and tests are approved, ask Codex to update the active task's `Completion record`.

It must record:

- concise result;
- principal files or modules changed;
- exact test and verification commands with results;
- known limitations;
- follow-up task IDs, without implementing them.

Clear obsolete `Work-in-progress handoff` content or mark it as not applicable.

Do not write a chronological session log. Do not add hidden reasoning.

Do not update `CHANGELOG.md` unless the active task explicitly requires it or the change is being prepared for a release.

## 12. Step 9 — Commit and stop

Only after manual approval:

```text
The diff and Completion record are approved.
Create exactly one commit for TASK-XXXX using this message:
<type>(<scope>): <summary>

After committing:
1. show the commit hash,
2. show final git status,
3. report the tests that passed.

Do not push, merge, deploy, modify more files, or start another task.
End with: Task stopped; no next task started.
```

Verify independently:

```powershell
git log -1 --oneline
git status
```

Publishing the completed task is a separate action.

If the user explicitly authorizes publishing:

```powershell
git push
```

or, for a new remote branch:

```powershell
git push -u origin <type>/TASK-XXXX-short-name
```

A Draft Pull Request may then be created.

The Pull Request should contain:

- task ID and title;
- concise implementation summary;
- verification results;
- commit hash;
- known limitations.

Creating or updating a Draft Pull Request does **not** authorize merging.

Set `status: DONE` as part of the approved commit when practical. If the task record cannot include the final commit hash without a second commit, leave the hash in Git history and the final report instead of automatically amending the commit.

## 13. Step 10 — Merge separately

Merging is a separate user-controlled action.

Example:

```powershell
git switch main
git pull --ff-only
git merge --ff-only <type>/TASK-XXXX-short-name
git status
```

When Pull Requests are used, the user may merge through the GitHub interface instead of running a local merge command.

After the remote merge:

```powershell
git switch main
git pull --ff-only
```

Delete the branch only after confirming the merge and remote state.

Do not let Codex push, create or update a Pull Request, mark a Pull Request ready for review, or merge unless the user explicitly requests that action.

## 14. When a task is blocked

Do not invent credentials, fabricate external API results, or silently replace a real integration with a fake unless the task permits a fake implementation.

Update:

- task status to `BLOCKED`;
- `Work-in-progress handoff`;
- exact blocker and evidence;
- what was completed safely;
- exact input, access, or decision required;
- whether the working tree is clean.

A blocked task is not `DONE`.

## 15. Changelog policy

`CHANGELOG.md` is not task memory.

Use it only for release-level, user-visible changes, for example:

- a new product capability;
- a changed external behavior;
- a fixed user-visible defect;
- a breaking configuration or API change;
- a published version.

Do not add:

- internal session notes;
- every technical refactor;
- per-file change lists;
- work-in-progress state;
- reasoning or failed attempts.

A task must explicitly say `changelog_required: true` before Codex updates it.

## 16. ADR policy

Create or update an ADR when the task changes a durable architectural choice. The task must name the ADR requirement.

Examples:

- selecting a persistence or messaging strategy;
- changing module boundaries;
- adopting a major framework or integration pattern;
- changing the security or tenancy model.

Ordinary implementation details do not require an ADR.

## 17. Context reset rule

Start a new Codex chat when:

- a task is committed;
- the next task concerns a different feature or module;
- the conversation accumulated failed approaches or contradictory instructions;
- the context is near its limit;
- the model repeatedly edits outside scope.

Reuse the same chat only for focused fixes and review findings inside the same active task.

## 18. Standard pipeline

```text
Business requirement
        ↓
Write one TASK-XXXX.md
        ↓
Create one branch
        ↓
Codex plan only
        ↓
Human plan review
        ↓
Codex implementation
        ↓
Focused tests
        ↓
Self-review
        ↓
Full task verification
        ↓
Human diff review
        ↓
Completion record
        ↓
Approved commit
        ↓
STOP
        ↓
Optional separate merge/release workflow
```

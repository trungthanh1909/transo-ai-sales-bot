# AGENTS.md

## 1. Mission

Build the TRANSO AI Sales Bot as a secure, testable modular monolith. The MVP assists employees and does not autonomously make risky business decisions.

The agent's job is to execute one bounded task at a time, verify the result, record concise task state, and stop. The agent must not treat a task as permission to redesign or expand the whole project.

## 2. Required reading order

For every task, read in this order:

1. `AGENTS.md`.
2. `docs/PROJECT_CONTEXT.md`.
3. The single active task file named by the user.
4. Only architecture, ADR, integration, or source files explicitly linked by that task or required to understand the affected code.

Do not scan every file in `docs/` by default. Do not load old completed task files unless the active task explicitly depends on them.

If work is being resumed after interruption, also inspect:

```text
git status
git diff --check
git diff
git log -5 --oneline
```

Then read the active task's `Work-in-progress handoff` before planning further edits.

## 3. Source of truth

Use the following precedence when instructions conflict:

1. Explicit instruction from the user in the current session.
2. `AGENTS.md`.
3. The active `TASK-XXXX.md` file.
4. Accepted ADRs.
5. `docs/PROJECT_CONTEXT.md` and other architecture documents.
6. Existing source code and tests.

Report conflicts before editing. Do not silently choose one interpretation when the conflict can materially affect behavior, security, data, or architecture.

Chat history is temporary context. Git, source code, tests, ADRs, and task files are the durable project record.

## 4. Scope discipline

- Work on exactly one active task.
- Before editing, restate:
  - the task goal;
  - in-scope behavior and files;
  - out-of-scope work;
  - risks or blockers;
  - verification commands.
- Implement the smallest complete change that satisfies the acceptance criteria.
- Do not implement adjacent features merely because they appear useful.
- Do not refactor unrelated code.
- Do not add speculative abstractions or dependencies without a task requirement.
- Do not continue after the active task is complete.
- Never begin the next task automatically.

If the task is too large for a safe bounded change, stop and recommend smaller follow-up tasks without implementing them.

## 5. Architecture rules

- Use a modular monolith for the MVP.
- Keep business logic independent from Facebook, KiotViet, CRM, and LLM vendors.
- Controllers must not call KiotViet directly; use `InventoryProvider` or another approved application boundary.
- CRM integrations must use `CrmClient`.
- LLM integrations must use `AiProvider`.
- Prices and inventory must come from trusted backend data, never from an LLM.
- Require human handoff for ambiguity, complaints, negotiation, bulk orders, refunds, low confidence, or integration failures.
- Do not introduce microservices, Kafka, Kubernetes, a vector database, or a multi-agent framework unless a dedicated task and ADR approve them.

## 6. Code rules

- Use Java 21 and Spring Boot for the backend.
- Prefer small, cohesive classes and explicit names.
- Validate external input at system boundaries.
- Every business-logic change requires appropriate tests.
- Every database schema change requires a Flyway migration.
- Never edit an already-applied migration to change production history; create a new migration.
- Preserve idempotency for webhook, retry, and event-processing flows.
- Add structured logs for integration failures.
- Never log secrets or sensitive customer data unnecessarily.
- Keep error behavior explicit and test important failure paths.
- Avoid adding dependencies when the standard library or an existing dependency is sufficient.

## 7. Security rules

- Never read, print, modify, or commit real `.env` files, API keys, access tokens, passwords, cookies, private keys, or production customer data.
- Use environment variables and `.env.example` placeholders.
- Do not weaken authentication, authorization, webhook verification, input validation, or TLS to make tests pass.
- Do not use unofficial scraping or browser automation for banking access.
- Do not deploy to production unless the user creates a separate deployment task.
- Stop and report when a task requires unavailable credentials, external permissions, or a business decision.

## 8. Testing and verification rules

- Run the focused tests while implementing.
- Before completion, run every verification command required by the task.
- Do not claim a command passed unless it was actually run successfully.
- If a command cannot be run, state exactly why and record it as a limitation or blocker.
- After any fix made during self-review, rerun the affected tests and the task-level verification commands.
- Check for accidental secrets and malformed diffs before commit.

Minimum pre-commit inspection:

```text
git status
git diff --check
git diff --stat
```

The user performs or approves the final human diff review.

## 9. Git rules

- Never push directly to `main`.
- Do not change branches, commit, push, merge, rebase, reset, clean, or force-push unless explicitly authorized.
- Never discard uncommitted user changes.
- Before a commit, show `git status`, summarize the diff, and report test results.
- One task should normally produce one intentional commit.
- Use Conventional Commit style where practical.
- After an approved commit, show the commit hash and final working-tree state.
- Do not push, merge, deploy, or start another task after committing unless separately instructed.

## 10. Task state recording

The active task file is the source of truth for task-specific progress.

### 10.1 While work is active

Keep the task status as `IN_PROGRESS` while implementation is underway.

Do not continuously append a session diary. Record only durable state needed to resume or verify the work.

### 10.2 If work is interrupted or blocked

Before stopping, update the active task's `Work-in-progress handoff` with concise factual information:

- current state;
- completed work;
- remaining work;
- tests or verification already run and their results;
- blocker or last relevant error;
- current working-tree state.

Keep the status as:

- `IN_PROGRESS` if work can resume normally; or
- `BLOCKED` if external input, access, or a decision is required.

Do not include hidden reasoning, a chronological conversation transcript, abandoned thought processes, or long exploratory notes.

### 10.3 When the task is complete

Before the approved final commit, update the task's `Completion record` with:

- result;
- principal files or modules changed;
- tests and verification results;
- known limitations;
- follow-up task IDs, without implementing them.

Set the task status to `DONE` only when:

- all acceptance criteria are satisfied;
- required tests pass or an explicitly accepted limitation is recorded;
- the diff has been reviewed;
- the approved task commit has been created.

After committing, add the commit hash to the `Completion record` if this can be done without creating an unnecessary second commit. Otherwise, the commit hash may remain in the final session report and Git history; do not amend or create a documentation-only commit unless the user authorizes it.

### 10.4 Changelog policy

Do not update `CHANGELOG.md` unless the active task explicitly requires it or the task represents a user-visible release change.

`CHANGELOG.md` is for release-level product changes, not session memory, internal reasoning, per-file implementation notes, or work-in-progress handoff.

## 11. ADR rules

Create or update an ADR only when a task introduces or changes a durable architectural decision, such as:

- module boundaries;
- integration strategy;
- persistence approach;
- security model;
- major dependency or infrastructure choice.

Do not create an ADR for ordinary implementation details.

## 12. Stop conditions

Stop the task and report instead of guessing when:

- a required secret, account permission, external API access, or business rule is missing;
- instructions conflict materially;
- a migration or destructive operation risks data loss;
- two focused attempts fail without new evidence;
- the required change exceeds the task's scope;
- the working tree contains unexplained changes that may belong to the user or another task.

When stopping, update `Work-in-progress handoff` if files have been changed.

## 13. Completion response

At the end of a completed or paused task, report only:

1. What changed or what was completed.
2. Files or modules changed.
3. Tests and verification run, with results.
4. Remaining work, risks, blockers, or limitations.
5. Commit hash, if committed.
6. Working-tree state.
7. Explicit statement: `Task stopped; no next task started.`

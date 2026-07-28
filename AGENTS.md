# AGENTS.md

## Mission

Build the TRANSO AI Sales Bot as a secure, testable modular monolith. The MVP assists employees; it does not autonomously make risky business decisions.

## Required reading order

1. This file.
2. `docs/PROJECT_CONTEXT.md`.
3. The single active task named by the user.
4. Only the architecture or integration documents explicitly linked by that task.

Do not scan every document by default.

## Scope discipline

- Work on one task only.
- Before editing, restate the task goal, in-scope files, out-of-scope work, risks, and verification commands.
- Do not implement adjacent features merely because they appear useful.
- Do not refactor unrelated code.
- Stop after acceptance criteria pass, tests pass, the diff is reviewed, and the requested commit is created.
- Never begin the next task automatically.

## Architecture rules

- Use a modular monolith for the MVP.
- Keep business logic independent from Facebook, KiotViet, CRM, and LLM vendors.
- Controllers must not call KiotViet directly; use `InventoryProvider`.
- CRM integrations must use `CrmClient`.
- LLM integrations must use `AiProvider`.
- Prices and inventory must come from trusted backend data, never from an LLM.
- Require human handoff for ambiguity, complaints, negotiation, bulk orders, refunds, low confidence, or integration failures.

## Code rules

- Java 21 and Spring Boot.
- Prefer small, cohesive classes and explicit names.
- Validate external input at boundaries.
- Every business-logic change requires tests.
- Every schema change requires a Flyway migration.
- Preserve idempotency for webhook and retry flows.
- Add structured logs for integration failures; never log secrets or sensitive customer data unnecessarily.

## Security rules

- Never read, print, modify, or commit real `.env` files, API keys, access tokens, passwords, cookies, or production customer data.
- Use environment variables and `.env.example` placeholders.
- Do not weaken authentication, authorization, webhook verification, or TLS to make tests pass.
- Do not deploy to production unless the user creates a separate deployment task.

## Git rules

- Never push directly to `main`.
- Do not change branches, commit, push, rebase, reset, or force-push unless the task explicitly authorizes it.
- Before a commit, show `git status`, summarize the diff, and report test results.
- One task should normally produce one intentional commit.
- Use Conventional Commit style where practical.

## Completion response

Report only:

1. What changed.
2. Files changed.
3. Tests/verification run and results.
4. Remaining risks or limitations.
5. Commit hash, if committed.
6. Explicit statement: `Task stopped; no next task started.`

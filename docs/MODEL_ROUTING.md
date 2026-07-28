# Codex Model Routing

> Availability, limits, and names may depend on the current Codex version and workspace policy. Use `/model` in Codex to see what is actually available.

## Default policy

Use the least expensive/fastest model that can reliably complete the task. Escalate only when complexity or failed verification justifies it.

| Model | Use for | Avoid as first choice for |
|---|---|---|
| GPT-5.6 Luna | Repository search, reading files, formatting docs, renames, boilerplate, simple unit tests, small deterministic fixes | Architecture changes, subtle concurrency/security, unclear multi-module bugs |
| GPT-5.6 Terra | Default implementation model: normal Spring Boot features, CRUD, migrations, adapters, tests, moderate refactors | Very ambiguous architecture or high-risk cross-cutting changes |
| GPT-5.6 Sol | Architecture decisions, difficult debugging, security-sensitive flows, multi-module refactors, final review of risky work | Routine repetitive edits where Terra/Luna are sufficient |
| GPT-5.5 | Compatibility fallback; compare behavior when 5.6 is unavailable or a regression is suspected | Default new work when a suitable 5.6 model is available |
| GPT-5.4 | Low-cost fallback for ordinary coding and review | New complex design or difficult debugging |
| GPT-5.4 mini | Mechanical high-volume work, simple sub-tasks, test-data generation, lint/format fixes | Owning an end-to-end business-critical task |

## Recommended mapping for TRANSO

| Task type | Primary | Escalate/review with | Typical reasoning |
|---|---|---|---|
| Create folders, configs, README, `.env.example` | Luna | Terra if build fails | Low |
| Spring Boot endpoint + validation + unit tests | Terra | Sol for review if security-facing | Medium |
| JPA entity + Flyway migration + repository tests | Terra | Sol if data migration is destructive | Medium/High |
| Fake `InventoryProvider` and deterministic search | Terra | Sol only for ambiguous matching rules | Medium |
| Meta webhook signature verification/idempotency | Sol | Sol second-pass review | High |
| KiotViet OAuth/token/retry integration | Sol | Sol security review | High |
| Routine DTO mapping or test fixtures | Luna / 5.4 mini | Terra if failures persist | Low |
| Cross-module architecture refactor | Sol | Separate Sol review session | High/Extra High |
| CI failure with a clear compiler/test error | Terra | Sol after two focused failed attempts | Medium |
| Documentation update after code is final | Luna | — | Low |

## Escalation rule

Escalate one level when any of these is true:

- Two focused implementation attempts fail the same acceptance criterion.
- The task touches authentication, authorization, secrets, payments, webhook verification, idempotency, or destructive migrations.
- The change spans three or more business modules.
- Requirements conflict or architecture trade-offs are material.
- Tests pass individually but fail nondeterministically as a suite.

Do not switch models merely because the first output needs a small correction.

## Record model use in every task

Example:

```yaml
codex:
  implementation_model: gpt-5.6-terra
  reasoning: medium
  reviewer_model: gpt-5.6-sol
  escalation_trigger: "Security flaw, cross-module change, or two failed focused attempts"
```

## Model-selection commands

Interactive Codex:

```text
/model
```

Then choose the model and reasoning level shown by the interface. Keep task-level selection in the task file rather than relying only on a global default.

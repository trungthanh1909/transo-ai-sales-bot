# TRANSO AI Sales Bot — MVP Roadmap

## 1. Purpose

This roadmap defines the intended delivery order for the first employee-assisted MVP.

It records:

- milestone outcomes;
- dependencies;
- current status;
- candidate task boundaries.

It does not replace task files. No roadmap item may be implemented until a bounded `docs/tasks/TASK-XXXX-*.md` file is created and reviewed.

## 2. Product boundary

The first MVP assists employees rather than acting as a fully autonomous sales agent.

Included in the MVP:

- receive and persist Messenger events;
- store conversations and messages;
- classify a bounded set of customer intents;
- search trusted product data through `InventoryProvider`;
- retrieve real price and inventory;
- draft a response for employee review;
- support human handoff;
- record integration failures.

Excluded from the first MVP:

- multi-tenant SaaS;
- microservices;
- Kafka;
- Kubernetes;
- a vector database;
- a complex multi-agent framework;
- full CRM replacement;
- autonomous official order creation;
- autonomous refund or negotiation decisions;
- production deployment without a dedicated task.

## 3. Confirmed architecture

- Modular monolith
- Java 21
- Spring Boot
- Maven Wrapper
- PostgreSQL
- Flyway
- Docker Compose
- One backend and one primary database
- React/Vite/TypeScript dashboard only when the backend employee-review flow requires it

Stable integration boundaries:

- `InventoryProvider`
- `CrmClient`
- `AiProvider`

Principles:

- rules first;
- deterministic search second;
- LLM only when needed;
- LLM output must be structured and validated;
- price and inventory never come from an LLM;
- human handoff is a normal product outcome;
- webhook and retry flows require idempotency and observable failures;
- business logic must not be tightly coupled to Facebook, KiotViet, CRM, or LLM vendors.

## 4. Status legend

- `DONE`: completed, verified, committed, and available in a reviewed branch/PR.
- `NEXT`: strongest candidate for the next bounded task.
- `PLANNED`: intended later outcome; task file may not exist yet.
- `BLOCKED`: prerequisite or decision prevents planning or implementation.
- `DEFERRED`: intentionally outside the current MVP sequence.

## 5. Milestone 0 — Repository and backend foundation

**Outcome:** A reproducible backend foundation can build, test, start against PostgreSQL, apply migrations, and expose a health endpoint.

Status: `DONE`

Completed task:

- `TASK-0001 — Bootstrap backend and local database`
- Branch: `chore/TASK-0001-bootstrap-backend`
- Commit: `a39c314`
- Draft PR: `#1`

Delivered:

- Spring Boot and Java 21
- Maven Wrapper
- PostgreSQL through Docker Compose
- Flyway baseline migration
- environment-based local configuration
- health endpoint
- baseline automated tests
- local runtime verification

Accepted limitation:

- no Testcontainers integration test yet

Exit criteria:

- [x] clean Maven verification
- [x] healthy PostgreSQL container
- [x] successful Flyway baseline
- [x] HTTP 200 health response
- [x] clean task commit
- [x] remote branch and Draft PR

## 6. Milestone 1 — Inbound event foundation

**Outcome:** The backend can safely receive a bounded fake/local Messenger-style webhook event and persist it without depending on live Meta credentials.

Status: `NEXT`

Proposed first task:

### Candidate TASK-0002 — Persist fake Messenger webhook events

Business outcome:

A developer can submit a representative local event to the backend and verify that the raw event plus minimal processing metadata is stored exactly once.

Likely scope:

- one fake/local webhook endpoint;
- bounded request validation;
- raw payload persistence;
- external event ID or equivalent deduplication key;
- received timestamp and processing status;
- duplicate delivery behavior;
- Flyway migration;
- controller and persistence tests;
- local PostgreSQL runtime verification.

Explicitly out of scope:

- live Meta webhook verification;
- Graph API calls;
- outgoing messages;
- AI intent classification;
- product search;
- KiotViet;
- CRM;
- frontend;
- asynchronous queue infrastructure.

Prerequisites:

- TASK-0001 merged or an explicitly approved stacked-branch strategy;
- event shape and deduplication rule defined in TASK-0002.

Planned follow-up outcomes:

- webhook processing boundary separated from transport;
- conversation and message domain persistence;
- replay-safe processing state.

## 7. Milestone 2 — Conversation and message model

**Outcome:** Persisted inbound events can be translated into stable customer, conversation, and message records.

Status: `PLANNED`

Candidate task boundaries:

1. Create minimal conversation and message schema.
2. Map a persisted fake event into a conversation/message record.
3. Define idempotent replay behavior.
4. Record malformed or unsupported event outcomes.

Exit criteria:

- the same external event cannot create duplicate messages;
- conversation history can be queried by a stable identifier;
- failure state is observable;
- transport payload remains available for audit/debugging without leaking secrets.

## 8. Milestone 3 — Inventory abstraction with fake data

**Outcome:** Business logic can search products, price, and inventory through `InventoryProvider` without KiotViet coupling.

Status: `PLANNED`

Candidate task boundaries:

1. Define or formalize inventory application contracts.
2. Implement an in-memory or fixture-backed fake provider.
3. Add deterministic product search behavior.
4. Add explicit not-found and provider-failure behavior.

Exit criteria:

- no controller directly calls a vendor client;
- product data is deterministic and tested;
- price and inventory originate from trusted provider data;
- vendor failure maps to a human-reviewable result.

## 9. Milestone 4 — Rules-based employee-assist flow

**Outcome:** A stored customer message can produce a structured draft response from deterministic rules and fake inventory data.

Status: `PLANNED`

Candidate task boundaries:

1. Define a small intent set.
2. Implement rules-based intent recognition.
3. Connect intent to product search.
4. Generate a structured draft response.
5. Require human handoff for unsupported or ambiguous cases.

Exit criteria:

- no automated message is sent to a customer;
- draft includes source-backed price/inventory data;
- low-confidence or unsupported requests result in handoff;
- end-to-end flow is covered by tests using fake data.

## 10. Milestone 5 — Live Meta webhook integration

**Outcome:** The backend can verify and receive real Meta webhook requests while preserving the tested internal event-processing boundary.

Status: `PLANNED`

Prerequisites:

- fake webhook and persistence flow are stable;
- required Meta application access is available;
- security and verification rules are specified.

Candidate task boundaries:

1. webhook verification handshake;
2. signature or authenticity verification as required;
3. mapping live payloads into the internal event model;
4. retry and duplicate delivery handling;
5. integration error logging.

Out of scope until separately approved:

- production deployment;
- auto-reply;
- broad Graph API feature coverage.

## 11. Milestone 6 — KiotViet inventory integration

**Outcome:** `InventoryProvider` can use KiotViet to return trusted product, price, and stock data.

Status: `PLANNED`

Prerequisites:

- stable fake provider contract;
- KiotViet API access and documentation;
- credential handling plan;
- mapping and rate-limit rules.

Candidate task boundaries:

1. KiotViet authentication/client foundation.
2. Product search mapping.
3. Price and inventory retrieval.
4. timeout, rate-limit, retry, and error behavior.
5. contract tests against fixtures or a permitted test environment.

Exit criteria:

- business services depend only on `InventoryProvider`;
- secrets remain outside Git;
- failures trigger observable handoff behavior;
- no LLM-generated price or inventory.

## 12. Milestone 7 — Employee review interface

**Outcome:** An employee can inspect the conversation, trusted product data, draft response, and handoff reason before any response is sent.

Status: `PLANNED`

Candidate task boundaries:

1. backend review APIs;
2. minimal React/Vite/TypeScript shell;
3. conversation list and detail;
4. draft approval/edit state;
5. explicit handoff and integration-error display.

Out of scope:

- broad CRM dashboard;
- analytics suite;
- design-system expansion;
- autonomous sending without approval.

## 13. Milestone 8 — Controlled outgoing response

**Outcome:** An approved employee response can be sent through a bounded provider integration with auditability and failure handling.

Status: `PLANNED`

Prerequisites:

- employee review flow;
- Meta sending permissions;
- explicit authorization rules;
- idempotent send behavior.

Candidate task boundaries:

1. outgoing message provider abstraction;
2. approved-send command;
3. idempotency key and audit record;
4. provider failure and retry behavior;
5. status visible to the employee.

## 14. Milestone 9 — AI-assisted classification

**Outcome:** `AiProvider` may classify only the cases where deterministic rules are insufficient, using structured validated output.

Status: `PLANNED`

Prerequisites:

- rules-based flow is measured and stable;
- allowed intent schema exists;
- human handoff rules are enforced independently of the model.

Candidate task boundaries:

1. `AiProvider` contract and fake implementation;
2. structured output schema and validation;
3. provider integration behind the abstraction;
4. confidence threshold and fallback;
5. prompt and response safety tests.

Rules:

- AI cannot invent price or inventory;
- invalid output is rejected;
- low confidence produces handoff;
- AI integration must not bypass business rules.

## 15. Milestone 10 — CRM integration

**Outcome:** Approved customer and conversation information can be synchronized through `CrmClient` without coupling core logic to one CRM vendor.

Status: `PLANNED`

Prerequisites:

- customer/conversation model is stable;
- required CRM selection and access are available;
- sync ownership and retry rules are defined.

Out of scope:

- building a new CRM;
- making CRM the source of truth for inventory;
- broad two-way synchronization without separate tasks.

## 16. Deferred platform capabilities

Status: `DEFERRED`

Do not introduce these during the first MVP unless a dedicated roadmap revision, task, and ADR approve them:

- Redis
- RabbitMQ
- Kafka
- Kubernetes
- microservices
- vector database
- multi-agent orchestration
- multi-tenant SaaS
- billing and subscriptions
- advanced analytics
- full production observability platform

## 17. Immediate next decision

Before creating TASK-0002:

1. review and merge Draft PR #1 when ready;
2. confirm that fake event persistence is still the smallest useful next outcome;
3. define the representative event shape;
4. define the deduplication identifier and duplicate response behavior;
5. draft TASK-0002 from `docs/tasks/TASK_TEMPLATE.md`;
6. review architecture, scope, tests, and verification;
7. create the branch only after the task is `READY`.

## 18. Roadmap update rules

Update this file when:

- a milestone is completed;
- sequencing changes;
- a new prerequisite is discovered;
- scope is intentionally moved;
- a task is created and should be linked;
- a roadmap item is deferred or removed.

Do not use this file for:

- command transcripts;
- temporary implementation blockers;
- per-file diffs;
- session memory;
- internal reasoning;
- detailed acceptance criteria that belong in a task file.

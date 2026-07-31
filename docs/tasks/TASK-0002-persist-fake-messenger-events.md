---
id: TASK-0002
title: Persist fake Messenger webhook events
status: DONE
priority: P1
branch: feat/TASK-0002-persist-fake-messenger-events
created: 2026-07-30
updated: 2026-07-31
completed: 2026-07-31
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

# TASK-0002 — Persist fake Messenger webhook events

## 1. Business outcome

A developer can submit a representative fake Messenger-style webhook event to a local backend endpoint and verify that the complete raw event plus minimal receipt metadata is persisted exactly once. Repeated delivery of the same external event is acknowledged safely without creating a duplicate row.

## 2. Context to read

Required:

- `AGENTS.md`
- `docs/PROJECT_CONTEXT.md`
- `docs/ROADMAP.md`
- This task file

Task-specific references only:

- Existing backend configuration, persistence, migration, controller, and test files required to implement this task.
- `infra/docker-compose.yml` only for local PostgreSQL verification.

Do not inspect unrelated integrations, future modules, completed task files, or frontend code unless a material blocker requires escalation.

## 3. Current state

- TASK-0001 established the Java 21/Spring Boot backend, Maven Wrapper, PostgreSQL, Flyway, Docker Compose, baseline migration, health endpoint, and baseline tests.
- The backend does not yet expose an inbound Messenger-style event endpoint.
- No inbound webhook event persistence model or table exists yet.
- No live Meta credentials are required or permitted for this task.
- Prerequisite: TASK-0001 must be merged into `main`, or the user must explicitly approve a stacked-branch strategy before implementation begins.

## 4. In scope

### 4.1 Fake/local endpoint

Implement one local development endpoint:

```text
POST /api/local/webhooks/messenger
Content-Type: application/json
```

The endpoint must be available only in the local/test environment through the repository's existing profile or configuration approach. It must not silently become a production Meta webhook endpoint.

### 4.2 Representative request shape

Accept one fake Messenger-style event per request using the following minimum bounded shape:

```json
{
  "externalEventId": "mid.local-0001",
  "senderId": "customer-123",
  "recipientId": "page-456",
  "occurredAt": "2026-07-30T16:00:00Z",
  "message": {
    "text": "Bao gia loc dau ma 123"
  }
}
```

Validation requirements:

- `externalEventId`: required, non-blank, maximum 255 characters.
- `senderId`: required, non-blank, maximum 255 characters.
- `recipientId`: required, non-blank, maximum 255 characters.
- `occurredAt`: required, valid ISO-8601 timestamp with offset.
- `message`: required.
- `message.text`: required, non-blank, maximum 4,000 characters.
- Unknown additional JSON fields may be rejected or ignored according to the project's existing Jackson policy, but the behavior must be consistent and covered by tests if it is intentionally configured.

### 4.3 Persistence

Persist one inbound event record containing at least:

- generated internal primary key;
- `external_event_id`;
- complete raw request payload;
- server-generated `received_at` timestamp;
- processing status with initial value `RECEIVED`.

Persistence constraints:

- Store the raw request as PostgreSQL `jsonb` unless an existing repository convention requires another lossless JSON representation.
- Enforce uniqueness of `external_event_id` at the database level.
- Do not create customer, conversation, or message domain records in this task.
- Persist the original JSON request body losslessly, rather than reconstructing the raw payload only from the validated DTO.

### 4.4 Idempotent duplicate handling

For the first valid delivery:

- persist exactly one row;
- return HTTP `201 Created`;
- return a small response indicating `externalEventId`, `status: "RECEIVED"`, and `duplicate: false`.

For a later valid delivery with the same `externalEventId`:

- do not create or update another row;
- keep the originally stored raw payload, `received_at`, and status unchanged;
- return HTTP `200 OK`;
- return the same `externalEventId`, the existing status, and `duplicate: true`.

Duplicate handling must remain safe under concurrent delivery. A prior existence check alone is insufficient; the database uniqueness constraint must remain the final guard.

### 4.5 Invalid request handling

For malformed JSON or validation failure:

- return HTTP `400 Bad Request` using the repository's existing error-response convention, or introduce only the smallest task-local error representation if none exists;
- persist no event row;
- do not expose stack traces, SQL details, or sensitive configuration.

### 4.6 Database migration

Add a new Flyway migration that creates the inbound event table, required columns, processing-status constraint or equivalent representation, and unique constraint/index for `external_event_id`.

Do not edit the already-applied baseline migration.

### 4.7 Tests

Add focused persistence/repository tests and controller tests for the success, duplicate, and invalid-request behavior defined by this task.

Perform local PostgreSQL runtime verification using Docker Compose after automated tests pass.

## 5. Out of scope

- Live Meta webhook verification handshake.
- Meta request signature or authenticity verification.
- Facebook Graph API calls.
- Outgoing messages or automatic replies.
- Mapping into customer, conversation, or message domain records.
- Event processing beyond initial status `RECEIVED`.
- Replay jobs, retry schedulers, dead-letter handling, or queues.
- AI intent classification, RAG, or LLM integration.
- Product search or `InventoryProvider` work.
- KiotViet or CRM integration.
- Frontend or employee dashboard work.
- Authentication redesign.
- Production deployment.
- Testcontainers introduction unless repository inspection proves it is already available and using it stays within scope.
- Unrelated refactoring, dependency upgrades, or architecture changes.

## 6. Acceptance criteria

- [ ] `POST /api/local/webhooks/messenger` accepts the defined valid fake event in the local/test environment.
- [ ] A first valid delivery returns HTTP `201` with `duplicate: false`.
- [ ] The database contains exactly one row with the matching external event ID, complete raw payload, server-generated received timestamp, and status `RECEIVED`.
- [ ] A repeated valid delivery with the same external event ID returns HTTP `200` with `duplicate: true`.
- [ ] Duplicate delivery does not insert or update another row, including when concurrent requests race.
- [ ] Database uniqueness protects `external_event_id` independently of application-level checks.
- [ ] Missing, blank, oversized, malformed, or structurally invalid required input returns HTTP `400` and persists nothing.
- [ ] The fake endpoint is not exposed as an unrestricted production endpoint.
- [ ] A new Flyway migration applies successfully without modifying prior migrations.
- [ ] Required controller and persistence tests pass.
- [ ] Local PostgreSQL verification confirms migration, first insert, and duplicate behavior.
- [ ] No secrets, sensitive payload logging, unrelated changes, or out-of-scope features are included.
- [ ] Task record is updated concisely before the approved commit.
- [ ] No ADR is created because this task follows the roadmap's existing PostgreSQL/Flyway and idempotent webhook decisions.
- [ ] No changelog update is made because this is not yet a released user-visible capability.

## 7. Expected files or modules

Exact package names must follow existing backend conventions discovered during the plan-only inspection. Likely affected areas:

```text
backend/src/main/java/.../webhook/...
backend/src/main/java/.../webhook/api/...
backend/src/main/java/.../webhook/application/...
backend/src/main/java/.../webhook/persistence/...
backend/src/main/resources/db/migration/V2__create_inbound_webhook_event.sql
backend/src/test/java/.../webhook/...
docs/tasks/TASK-0002-persist-fake-messenger-events.md
```

Possible existing shared configuration or error-handling files may be adjusted only when necessary. Codex must explain any material deviation from these areas before editing.

## 8. Technical constraints

- Preserve the modular-monolith architecture.
- Keep HTTP transport handling separate from persistence/business behavior; the controller must not contain duplicate-resolution or database logic directly.
- Use Java 21, Spring Boot, PostgreSQL, Flyway, and existing project dependencies.
- Do not add a vendor-specific Meta client or couple the internal persistence model to the full Meta payload schema.
- Validate all external input at the controller boundary.
- Treat `externalEventId` as an opaque string; do not parse business meaning from it.
- Use a server-controlled clock for `received_at`; do not trust the event's `occurredAt` as receipt time.
- Preserve the original raw payload for audit/debugging.
- Enforce idempotency with a database unique constraint and transaction-safe duplicate resolution.
- Do not overwrite the original event when a duplicate external ID arrives with different payload content.
- Use an explicit processing status representation; the only status required in this task is `RECEIVED`.
- Do not log complete raw customer payloads at normal log levels.
- Never log secrets, database credentials, or stack traces in client responses.
- Do not add Redis, RabbitMQ, Kafka, asynchronous processing, or new infrastructure.
- Do not modify an already-applied Flyway migration.
- Avoid new dependencies unless existing Spring/Jackson/JPA/JDBC facilities cannot satisfy the task; escalate before adding a material dependency.

## 9. Required tests

### Unit or focused application tests

- First receipt produces a created result with status `RECEIVED` and `duplicate: false`.
- Duplicate receipt resolves to the existing record with `duplicate: true`.
- Duplicate receipt with changed payload does not overwrite the original record.
- If duplicate resolution has task-local service logic, cover the database-constraint conflict path rather than only a pre-check path.

### Persistence/repository tests

- A valid inbound event persists all required metadata and a lossless raw JSON payload.
- `external_event_id` uniqueness prevents a second row.
- Initial processing status is `RECEIVED`.
- `received_at` is generated by the backend/database and is populated.

Use the repository's existing database-test approach. Do not introduce Testcontainers solely for this task unless approved after plan review.

### Controller tests

- Valid first request returns HTTP `201` and the defined response body.
- Valid duplicate request returns HTTP `200` and `duplicate: true`.
- Missing each required top-level field returns HTTP `400`.
- Blank required strings return HTTP `400`.
- Invalid timestamp returns HTTP `400`.
- Missing `message` or blank `message.text` returns HTTP `400`.
- Oversized bounded fields return HTTP `400`.
- Malformed JSON returns HTTP `400`.
- Invalid requests do not invoke successful persistence.

### Regression and failure cases

- Existing health endpoint behavior remains unchanged.
- Existing baseline tests still pass.
- Concurrent or simulated racing duplicate inserts result in exactly one stored row and successful idempotent acknowledgement, not HTTP `500`.
- Client errors do not include SQL errors or stack traces.

## 10. Verification commands

Run from repository root unless noted.

### Automated verification

```powershell
cd backend
.\mvnw.cmd clean verify
```

### Docker Compose and migration verification

```powershell
cd ..
docker compose -f infra/docker-compose.yml config
docker compose -f infra/docker-compose.yml up -d
docker compose -f infra/docker-compose.yml ps
```

Start the backend using the repository's documented local command and environment configuration.

### Runtime behavior verification

Submit a valid event:

```powershell
$body = @'
{
  "externalEventId": "mid.local-0001",
  "senderId": "customer-123",
  "recipientId": "page-456",
  "occurredAt": "2026-07-30T16:00:00Z",
  "message": {
    "text": "Bao gia loc dau ma 123"
  }
}
'@

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/local/webhooks/messenger `
  -ContentType 'application/json' `
  -Body $body
```

Submit the same request again and confirm:

- first response is HTTP `201` with `duplicate: false`;
- second response is HTTP `200` with `duplicate: true`;
- the database contains exactly one row for `mid.local-0001`;
- the original raw payload, receipt timestamp, and status are unchanged.

Submit at least one invalid request and confirm HTTP `400` with no inserted row.

Use a PostgreSQL query appropriate to the final table name selected during implementation, for example:

```sql
SELECT external_event_id, raw_payload, received_at, processing_status
FROM inbound_webhook_event
WHERE external_event_id = 'mid.local-0001';
```

### Final inspection

```powershell
git status
git diff --check
git diff --stat
git diff
```

Stop the backend and local containers after verification unless the user explicitly asks to keep them running.

## 11. Stop and escalation conditions

Stop and report when:

- TASK-0001 is not merged and no stacked-branch strategy is approved;
- the existing repository structure materially conflicts with the expected modular boundary;
- enabling a local-only endpoint safely requires an undefined production-security decision;
- the database test approach cannot test the required PostgreSQL uniqueness or JSON behavior without adding infrastructure outside scope;
- duplicate behavior cannot be made race-safe without a material architecture or dependency change;
- a destructive migration or modification of an applied migration appears necessary;
- a required business rule in the request, response, or idempotency contract is ambiguous;
- two focused attempts fail without new evidence;
- implementation requires live Meta credentials, external permissions, or an out-of-scope integration;
- the working tree contains unexplained changes belonging to another task.

## 12. Commit

Suggested message:

```text
feat(webhook): persist fake Messenger events idempotently
```

Codex may commit only after explicit user approval.

## 13. Work-in-progress handoff

Not applicable.

## 14. Completion record

- Result: Implemented a local/test-only fake Messenger webhook endpoint that validates a bounded event, persists the submitted JSON value as PostgreSQL `jsonb`, and acknowledges duplicate external event IDs without overwriting the original record.
- Principal files or modules changed: `webhook` API, application, and PostgreSQL persistence modules; `V2__create_inbound_webhook_event.sql`; webhook controller/application/persistence tests; baseline application-context test.
- Tests and verification results: `mvn clean verify` passed with 16 tests, 0 failures, and 0 errors. PostgreSQL runtime verification confirmed Flyway V2 success; first valid delivery returned 201 and stored one row; changed duplicate delivery returned 200 and preserved the original payload, `received_at`, and status; missing `message.text` and malformed JSON returned 400 with no inserted rows; the database unique constraint rejected a direct duplicate insert; six concurrent requests produced one 201, five 200 responses, zero 500 responses, and one immutable row; the production profile returned 404 for the local webhook endpoint; `/health` returned `UP`.
- Commit: `Not committed yet`
- Known limitations: The named local PostgreSQL volume was preserved after runtime verification and retains local verification rows. No live Meta integration is included.
- Follow-up task IDs, without implementing them: Candidate follow-up for conversation/message mapping; assign an ID only when drafted.
- ADR updated: `No`
- Changelog updated: `No`

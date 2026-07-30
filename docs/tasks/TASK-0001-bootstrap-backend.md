---
id: TASK-0001
title: Bootstrap backend and local database
status: DONE
priority: P0
branch: chore/TASK-0001-bootstrap-backend
created: 2026-07-29
updated: 2026-07-30
owner: developer
codex:
  implementation_model: gpt-5.6-terra
  reasoning: medium
  reviewer_model: gpt-5.6-sol
  escalation_trigger: "Build remains broken after two focused fixes or configuration requires an architectural change"
  may_commit: false
---

# TASK-0001 — Bootstrap backend and local database

## Business outcome

Create a reproducible local foundation so future tasks can add business features without repeatedly changing project setup.

## Context to read

- `AGENTS.md`
- `docs/PROJECT_CONTEXT.md`
- `docs/WORKFLOW.md`

## In scope

- Create a Java 21 Spring Boot backend using Maven Wrapper.
- Include Spring Web, Validation, Data JPA, PostgreSQL driver, Flyway, and test dependencies.
- Add a minimal health endpoint or Spring Actuator health endpoint.
- Add PostgreSQL to `infra/docker-compose.yml` with safe local defaults.
- Add `.env.example` containing variable names only and safe placeholder values.
- Add one baseline Flyway migration.
- Add tests proving the application context and health behavior work.
- Document exact Windows PowerShell commands to start infrastructure and run tests.

## Out of scope

- Facebook/Meta integration.
- KiotViet integration.
- Authentication and user accounts.
- Product, customer, conversation, or CRM domain models.
- Frontend/dashboard.
- Cloud deployment and CI/CD.

## Acceptance criteria

- [x] `java -version` compatible code targets Java 21.
- [x] `backend/mvnw.cmd clean verify` passes on Windows when prerequisites are available.
- [x] PostgreSQL starts through Docker Compose.
- [x] The application can connect to PostgreSQL using environment-based configuration.
- [x] Flyway applies the baseline migration on an empty database.
- [x] Health endpoint returns success when the application is running.
- [x] No real secret is committed.
- [x] No unrelated feature is implemented.

## Expected files/modules

```text
backend/pom.xml
backend/mvnw
backend/mvnw.cmd
backend/.mvn/
backend/src/main/java/...
backend/src/main/resources/application.yml
backend/src/main/resources/db/migration/V1__baseline.sql
backend/src/test/java/...
infra/docker-compose.yml
.env.example
README.md
```

## Technical constraints

- Use a single backend application.
- Use configuration placeholders/environment variables for database credentials.
- Prefer a dedicated local database and user rather than PostgreSQL superuser defaults.
- Do not add Redis, message queues, Kubernetes, frontend packages, or LLM SDKs.

## Required tests

- Application context test.
- Health endpoint test.
- Flyway/database integration test if it can be made reliable with Testcontainers; otherwise document it as the immediate next isolated task rather than faking success.

## Verification commands

```powershell
docker compose -f infra/docker-compose.yml up -d
cd backend
.\mvnw.cmd clean verify
cd ..
docker compose -f infra/docker-compose.yml ps
git diff --check
git status
```

## Suggested Codex start prompt

```text
Read AGENTS.md, docs/PROJECT_CONTEXT.md, docs/WORKFLOW.md, and docs/tasks/TASK-0001-bootstrap-backend.md only. Do not edit yet. Produce a bounded implementation plan, expected files, verification commands, and risks. Do not implement any Facebook, KiotViet, CRM, AI, authentication, frontend, CI/CD, or deployment feature.
```

## Commit

```text
chore(backend): bootstrap Spring Boot and local PostgreSQL
```

Codex may commit only after explicit user approval.

## Completion record

- Result: Bootstrapped the Java 21 Spring Boot backend, local PostgreSQL configuration, Flyway baseline migration, and health behavior.
- Files changed: `backend/`, `infra/docker-compose.yml`, `.env.example`, and `README.md`.
- Tests: `backend\\.\\mvnw.cmd clean verify` passed with the application-context and health endpoint tests. Docker Compose reported PostgreSQL healthy. The application started with documented environment variables, Flyway recorded `V1__baseline.sql` as successful, and `GET /health` returned `{"status":"UP"}`.
- Commit: Not created; user approval is required.
- Known limitations: No Testcontainers integration test was added; the local Compose startup verifies the migration against PostgreSQL.
- Follow-up task IDs: Candidate `TASK-0002 — Persist fake Messenger webhook events`; the bounded task file has not been created yet.

## Work-in-progress handoff

Not applicable; TASK-0001 verification completed.

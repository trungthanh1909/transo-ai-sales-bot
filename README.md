# TRANSO AI Sales Bot

AI-assisted sales bot for Facebook/Messenger, backed by real inventory and price data from KiotViet.

## Current phase

Planning and repository bootstrap. The first MVP is an employee-assist workflow: receive a message, identify a product, query inventory, draft a response, and require employee approval before sending.

## Start here

1. Read `AGENTS.md` for repository-wide rules.
2. Read `docs/PROJECT_CONTEXT.md` for stable product context.
3. Open exactly one active task in `docs/tasks/`.
4. Follow `docs/WORKFLOW.md` until the task is tested and committed.

## Core stack

- Java 21
- Spring Boot
- Maven Wrapper
- PostgreSQL
- Flyway
- Docker Compose
- JUnit / Mockito
- Testcontainers when integration tests become stable

## Essential commands

```powershell
# Create local-only configuration, then start PostgreSQL.
Copy-Item .env.example .env
docker compose -f infra/docker-compose.yml up -d

# Build and test the backend.
Set-Location backend
.\mvnw.cmd clean verify
Set-Location ..

# Run the application against the local database.
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:POSTGRES_DB = "transo_sales_bot"
$env:POSTGRES_USER = "transo_local"
$env:POSTGRES_PASSWORD = "change-me-local-only"
Set-Location backend
.\mvnw.cmd spring-boot:run

# In another PowerShell session, verify health.
Invoke-RestMethod http://localhost:8080/health
```

The backend targets Java 21. Docker Compose reads `.env` automatically; keep that file local and never commit it.

Do not place secrets in this repository. Use `.env.example` only for variable names and safe sample values.

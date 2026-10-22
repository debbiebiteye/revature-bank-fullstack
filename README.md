# Revature Bank — Full-Stack Learning Project

A banking workflow demo built with Angular, Bootstrap, Spring Boot, Spring Security, JPA, and PostgreSQL. Created step by step as a Java and Angular refresher, with learner notes preserved in the source.

**Demo only: use fictional users and balances. This is not production banking software and does not move real money.**

## Features

- Registration with validation and salted PBKDF2 password hashing
- Session-based login and logout with CSRF protection and session ID rotation
- Checking and Savings accounts: one of each type per user
- Account lists restricted to the authenticated owner
- Deposits and withdrawals using Java BigDecimal
- Transactional account-row locks and insufficient-funds checks
- Angular reactive forms, route guard, loading states, and API error messages
- Responsive Bootstrap cards and custom CSS

## Repository layout

- `backend/`: Spring Boot REST API and learner comments
- `frontend/`: Angular application
- `database/schema.sql`: empty database schema; no personal data or credentials
- `docs/architecture.md`: request flow and layer responsibilities
- `docs/manual-tests.md`: manual verification scenarios and automated-test limitations

## Prerequisites

- JDK 17 or newer supported by the pinned Spring Boot release
- PostgreSQL 17 (used during development)
- Node.js 20.20.0 (used during development; Angular 21-compatible)
- npm 11.6.0 for installation; Maven Wrapper is included
- Git

The project pins Angular 21 through package.json and package-lock.json. Targeted Vite and MCP SDK overrides were added after dependency advisories. Review overrides when upgrading Angular.

## 1. Create the database

Create a NEW database named `revature_bank_web` in pgAdmin, then open its Query Tool and run `database/schema.sql`. Alternatively:

```powershell
psql -h localhost -U postgres -c "CREATE DATABASE revature_bank_web;"
psql -h localhost -U postgres -d revature_bank_web -f database/schema.sql
```

Run the schema only against a fresh database. Hibernate is configured with `ddl-auto=none`; it will not create the tables.

## 2. Start the backend

In PowerShell:

```powershell
Set-Location backend
$env:DB_USER = 'postgres'
$env:DB_URL = 'jdbc:postgresql://localhost:5432/revature_bank_web'
$bankDbPassword = Read-Host 'PostgreSQL password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $bankDbPassword).Password
.\mvnw.cmd spring-boot:run
```

Keep the terminal running. The API uses port 8080.

If PostgreSQL is bound to IPv6 localhost, use `jdbc:postgresql://[::1]:5432/revature_bank_web` for DB_URL. Match your local PostgreSQL host and port.

For IntelliJ, open `backend/pom.xml` as a Maven project and set DB_PASSWORD in the application's run configuration. Do not commit that run configuration.

After stopping the server, clear the password from that PowerShell session:

```powershell
Remove-Item Env:DB_PASSWORD
Remove-Variable bankDbPassword
```

## 3. Start the frontend

In a second terminal from the repository root:

```powershell
Set-Location frontend
npx.cmd --yes npm@11.6.0 ci
npm.cmd start
```

Open http://localhost:4200. The start script includes the development proxy: relative `/api/**` calls are forwarded to Spring Boot on port 8080. No manual token copying is needed in the Angular UI.

Register a fictional user, sign in, open an account, and try a deposit and withdrawal. New accounts start at zero.

## API overview

| Method | Endpoint | Purpose |
|---|---|---|
| GET | /api/auth/csrf | Get a session-bound CSRF token |
| POST | /api/users | Register |
| POST | /api/auth/login | Authenticate and establish a session |
| GET | /api/auth/me | Read the authenticated user's details |
| POST | /api/auth/logout | End the session (204) |
| GET | /api/accounts | List the current user's accounts |
| POST | /api/accounts | Open Checking or Savings |
| POST | /api/accounts/{id}/deposits | Deposit a positive amount |
| POST | /api/accounts/{id}/withdrawals | Withdraw within the balance |

All POST requests require the CSRF header, including registration and login. Refresh the CSRF token after login/logout. Accounts require an authenticated session. Passwords and hashes are excluded from response DTOs.

## Build and verification

```powershell
# From backend; packaging only, not a test-suite claim:
.\mvnw.cmd -DskipTests package

# From frontend:
npm.cmd run build
npm.cmd audit
```

The original Angular working project built successfully and reported zero audit findings during preparation. Its initial bundle exceeded the 500 kB warning budget by roughly 34 kB; this is an outstanding optimization, not a compilation failure.

See docs/manual-tests.md for workflows checked interactively. Generated test files are retained as scaffolding. They have not been updated into comprehensive automated coverage; commands that skip tests must not be read as test success. The generated backend context test requires the database and environment configuration.

## Learning notes and limitations

The application uses server-side sessions, not JWT or OAuth. The Angular guard improves navigation; backend authorization protects the data. See docs/architecture.md for a complete deposit walkthrough.

Future work includes automated integration/concurrency tests, transaction history, schema migrations, more specific API errors, accessible field-error associations, lazy-loaded routes and bundle optimization, and deployment configuration with HTTPS and secure cookies. No transaction ledger, idempotency keys, rate limiting, password recovery, or production financial controls are implemented.

The Angular development proxy is not a production deployment configuration. Do not expose the development servers or use real financial data.

This repository was developed with AI-assisted explanations and implementation guidance as a learning project. Learner comments are deliberately preserved.


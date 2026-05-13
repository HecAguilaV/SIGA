# Spec: infra-hardening

**Change**: infra-hardening — gateway routing, Flyway unification, JWT secret hardening.

## Block 1 — Gateway Routes

### R1.1: All routes MUST include StripPrefix=1 filter
Every gateway route in `application.yml` except the agent route MUST have `StripPrefix=1` in its filters list.

#### Scenario: Auth route reaches controller
- GIVEN a gateway route `Path=/api/auth/**` with `StripPrefix=1`
- WHEN a request hits `POST /api/auth/login`
- THEN the gateway forwards to `lb://auth-service/api/v1/auth/login`
- AND the auth controller responds 200 with JWT

#### Scenario: Agent route preserves prefix
- GIVEN the agent route with NO `StripPrefix` filter
- WHEN a request hits `/api/agent/query`
- THEN the gateway forwards to `lb://agent-service/api/agent/query`
- AND the agent responds correctly (no prefix stripped)

### R1.2: Eureka auto-routes MUST NOT interfere
Spring Cloud Gateway `discovery.locator` MUST be disabled or configured to not create duplicate routes for service IDs.

#### Scenario: No duplicate route conflict
- GIVEN Eureka discovery locator enabled
- WHEN the gateway receives a request matching an explicit route
- THEN the explicit route takes precedence
- AND no 404 from auto-route overriding StripPrefix

### R1.3: Gateway routes MUST NOT change external URLs
Consumers calling existing gateway paths MUST continue to use the same URLs (path rewriting happens at the gateway, not exposed externally).

#### Scenario: External URL unchanged after fix
- GIVEN an existing client calling `POST /api/auth/login` via gateway
- WHEN the gateway applies StripPrefix=1 routing
- THEN the client-visible URL remains `POST /api/auth/login`
- AND the internal target changes to `/api/v1/auth/login`

---

## Block 2 — Flyway Unification

### R2.1: Every V1 migration MUST own its schema
Each V1 `*.sql` migration MUST begin with `CREATE SCHEMA IF NOT EXISTS <service>;` followed by `SET search_path TO <service>;`.

#### Scenario: Fresh DB applies migrations without init-db.sh
- GIVEN a clean PostgreSQL RDS with no application schemas
- WHEN Flyway runs all V1 migrations
- THEN each service schema is created by its own migration
- AND all tables are created under the correct schema

#### Scenario: Re-run on existing DB is idempotent
- GIVEN a DB where schemas already exist
- WHEN Flyway re-runs V1 migrations (checksum unchanged)
- THEN `CREATE SCHEMA IF NOT EXISTS` succeeds (no-op)
- AND existing tables are not modified

### R2.2: All V1 tables MUST use schema.table prefix
Every `CREATE TABLE` in every V1 migration MUST reference the schema explicitly (e.g., `auth.users`, `sales.payment_methods`).

#### Scenario: Table creation with explicit schema
- GIVEN a V1 migration for service `sales`
- WHEN the migration executes
- THEN all `CREATE TABLE` statements use `sales.<table_name>`
- AND no unqualified table names remain

### R2.3: auth V1, V2, V3 MUST consistently use `auth.` prefix
All auth service migrations across versions MUST reference the `auth` schema.

#### Scenario: Cross-version schema consistency
- GIVEN auth V1 migration creating `auth.users`
- AND auth V2 migration referencing `auth.roles`
- AND auth V3 migration altering `auth.users`
- WHEN all migrations run sequentially
- THEN all tables and references resolve to `auth.` schema
- AND no ambiguity between versions

### R2.4: init-db.sh MUST NOT be DDL source of truth
DDL statements MUST be removed from `scripts/db-init/init-db.sh`. Only user/schema grants and non-DDL setup SHALL remain. Seed SQL in `backup_sql/` is preserved for reference only.

#### Scenario: Clean RDS deployable without init-db.sh DDL
- GIVEN a fresh RDS instance
- WHEN deploy runs `init-db.sh` (grants only) + Flyway migrations
- THEN all application tables exist under correct schemas
- AND `init-db.sh` creates no tables itself

#### Scenario: backup_sql preserved for seed data
- GIVEN `backup_sql/*.sql` files containing seed/INSERT statements
- WHEN Flyway manages all DDL
- THEN `backup_sql` files remain untouched
- AND no migration depends on `backup_sql` for schema creation

### R2.5: Services MUST be deployable on clean RDS
Full deployment pipeline MUST succeed on a fresh PostgreSQL RDS without `init-db.sh` DDL.

#### Scenario: Full pipeline on fresh RDS
- GIVEN a clean RDS with no SIGA schemas, tables, or data
- WHEN the full deployment pipeline runs (init-db.sh grants + Flyway migrations + seed data)
- THEN all services start successfully
- AND all health checks pass (200 OK)

---

## Block 3 — JWT Hardening

### R3.1: jwt.secret MUST have NO default fallback
The `jwt.secret` property MUST NOT have a default value in `application.yml`. The `@Value("\${jwt.secret}")` in `JwtService.kt` MUST NOT provide a fallback.

#### Scenario: Missing env var fails fast
- GIVEN `JWT_SECRET` environment variable is NOT set
- WHEN the auth service starts
- THEN startup MUST fail with `IllegalArgumentException` (or equivalent)
- AND the service MUST NOT become available

#### Scenario: Existing valid JWT still verifies
- GIVEN a valid JWT generated with the previous secret
- WHEN the service starts with `JWT_SECRET` set to the same value
- THEN token verification succeeds (no behavioral change)
- AND existing sessions are not invalidated

### R3.2: Startup MUST validate JWT_SECRET is secure
A `@PostConstruct` method in `JwtService.kt` MUST check the resolved `jwt.secret` value and log a FATAL-level error if the secret is empty or matches a known-insecure placeholder.

#### Scenario: Empty secret detected at startup
- GIVEN `JWT_SECRET` set to empty string
- WHEN `@PostConstruct` validation runs
- THEN a FATAL log message is emitted
- AND the application SHOULD fail to start or enter a degraded state

#### Scenario: Known placeholder rejected
- GIVEN `JWT_SECRET` set to `"super-secret-key"` or `"default-secret-key"`
- WHEN `@PostConstruct` validation runs
- THEN a FATAL log message is emitted identifying the placeholder
- AND the application MUST fail to start

#### Scenario: Secure secret passes validation
- GIVEN `JWT_SECRET` set to a 256+ bit random string
- WHEN `@PostConstruct` validation runs
- THEN no FATAL log is emitted
- AND startup proceeds normally

### R3.3: `.env.example` MUST include JWT_SECRET
The file `.env.example` at the project root MUST contain `JWT_SECRET=changeme` (or equivalent placeholder) so developers know to configure it.

#### Scenario: Developer onboarding
- GIVEN a developer cloning the repo for the first time
- WHEN they copy `.env.example` to `.env`
- THEN `JWT_SECRET` is present and documented
- AND the developer knows they MUST set a real secret

---

## Acceptance Criteria

| Block | Criteria | Verification |
|-------|----------|-------------|
| Gateway | `curl -X POST /api/auth/login` returns 200 (not 404) | Manual curl/E2E test |
| Gateway | `curl /api/agent/query` returns expected agent response | Manual curl/E2E test |
| Gateway | No duplicate routes from Eureka locator | Gateway startup logs |
| Flyway | Fresh DB: all schemas & tables created by Flyway only | Flyway migration history table |
| Flyway | init-db.sh creates no tables (grants only) | Review init-db.sh diff |
| Flyway | auth V1/V2/V3 all use `auth.` schema | SQL file review |
| JWT | Auth service fails startup without `JWT_SECRET` | docker-compose logs |
| JWT | No `super-secret-key` or `default-secret-key` in codebase | grep across all services |
| JWT | `.env.example` contains `JWT_SECRET` entry | File review |

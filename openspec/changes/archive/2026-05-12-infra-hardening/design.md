# Design: infra-hardening

**Change**: infra-hardening — gateway routing, Flyway unification, JWT secret hardening.
**Spec**: `openspec/changes/infra-hardening/spec.md`
**Status**: Final

---

## Technical Approach

Three independent blocks, each addressing a production deployment blocker:

1. **Gateway Routes** — Fix `StripPrefix` misunderstanding by using `RewritePath` to bridge `/api/<svc>` → `/api/v1/<svc>` for all Java services. No rewrite for the Python agent. Handle `CustomerController` exception by aligning its `@RequestMapping` to the `/api/v1/` convention.
2. **Flyway Unification** — Add `CREATE SCHEMA IF NOT EXISTS` + `SET search_path` to every V1, prefix all tables with `<schema>.`, and strip DDL from `init-db.sh`. Handle existing-dev-DB checksum mismatch via migration-repair.
3. **JWT Hardening** — Remove default secrets from config and code, add `@PostConstruct` validation in `JwtService.kt`, and ensure `.env.example` documents the requirement.

Blocks are independent — can be implemented in any order (no shared file edits).

---

## Architecture Decisions

### Decision: RewritePath over StripPrefix for Gateway

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `StripPrefix=1` | Strips `/api` but forwards bare `/auth/login` — misses `/v1/` | ❌ Rejected |
| `RewritePath` per route | Maps `/api/auth/(.*)` → `/api/v1/auth/$1` — exact control | ✅ Chosen |
| Global filter with regex | All routes affected — agent (no `/v1/`) needs exception | ❌ Rejected |

**Rationale**: Per-route `RewritePath` gives exact control. Agent route gets no filter. Internal `/api/comercial/` paths (legacy alias) also rewrite to `/api/v1/billing/`.

### Decision: Fix CustomerController instead of adding a special route

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Move to `/api/v1/auth/customers` | Breaks external URL unless gateway rewrites — and it will, using the same auth RewritePath | ✅ Chosen |
| Add separate route without StripPrefix | More routes, more config surface | ❌ Rejected |

**Rationale**: Changing the controller `@RequestMapping` from `/api/auth/customers` to `/api/v1/auth/customers` makes it consistent with all other controllers. The gateway RewritePath converts external `/api/auth/customers` → internal `/api/v1/auth/customers`, so external consumers see no change (R1.3 satisfied).

### Decision: Modify V1 in-place + flyway repair for existing dev DBs

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Modify V1 in-place | Existing dev DB gets checksum mismatch — run `flyway repair` | ✅ Chosen for dev |
| New Vx+1 migrations that add prefixes | Safe for prod — but we're pre-production, adds complexity | ❌ Rejected |
| Drop + recreate | Destructive, only for dev | ❌ Rejected |

**Rationale**: SIGA is in development, not production. In-place V1 changes are the simplest path. The design documents the `flyway repair` procedure for existing dev databases. For clean RDS (R2.5), V1 runs fresh with no history.

### Decision: Disable Eureka discovery locator

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Keep enabled | Auto-routes from Eureka could bypass RewritePath → 404s | ❌ Rejected |
| Disable | All routes explicit, no surprises — we already define every service route | ✅ Chosen |

**Rationale**: All service routes are already explicitly defined. Auto-routes from Eureka (e.g., `lb://siga-auth` → `/siga-auth/**`) create duplicate paths that DON'T have RewritePath, causing requests hitting the auto-route to reach the service without the `/v1/` prefix → 404. Safer to disable the locator.

---

## Data Flow

```
External Client
  │
  │  POST /api/auth/login
  ▼
Gateway ──[RewritePath]──► /api/v1/auth/login ──► lb://siga-auth (AuthController OK)
  │
  │  POST /api/auth/customers
  ▼
Gateway ──[RewritePath]──► /api/v1/auth/customers ──► lb://siga-auth (CustomerController OK ✓)
  │
  │  POST /api/agent/chat
  ▼
Gateway ──[no RewritePath]──► /api/agent/chat ──► lb://siga-agent (FastAPI OK)
```

---

## File Changes

### Block 1 — Gateway Routes

| File | Action | Description |
|------|--------|-------------|
| `services/gateway/src/main/resources/application.yml` | Modify | Add `RewritePath` filters to all routes except agent; disable `discovery.locator.enabled` |
| `services/auth/src/main/kotlin/com/siga/auth/controller/CustomerController.kt` | Modify | Change `@RequestMapping("/api/auth/customers")` → `@RequestMapping("/api/v1/auth/customers")` |

### Block 2 — Flyway Unification

| File | Action | Description |
|------|--------|-------------|
| `services/auth/src/main/resources/db/migration/V1__auth_init.sql` | Modify | Add `CREATE SCHEMA IF NOT EXISTS auth;` + `SET search_path TO auth;` at top; prefix all `CREATE TABLE` with `auth.` |
| `services/sales/src/main/resources/db/migration/V1__sales_init.sql` | Modify | Add `CREATE SCHEMA IF NOT EXISTS sales;` + `SET search_path TO sales;`; prefix all tables with `sales.` |
| `services/inventory/src/main/resources/db/migration/V1__inventory_init.sql` | Modify | Add `CREATE SCHEMA IF NOT EXISTS inventory;` + `SET search_path TO inventory;`; prefix all tables with `inventory.` |
| `services/agent/src/main/resources/db/migration/V1__agent_init.sql` | Modify | Add `CREATE SCHEMA IF NOT EXISTS agent;` + `SET search_path TO agent;`; prefix all tables with `agent.` |
| `scripts/db-init/init-db.sh` | Modify | Remove the SQL file execution lines (DDL); keep only schema creation, user creation, search_path setting |

**Note**: Billing V1–V3 already use `billing.` prefix — no changes needed. Auth V2 and V3 already target `auth.` schema — no changes needed beyond V1 consistency.

### Block 3 — JWT Hardening

| File | Action | Description |
|------|--------|-------------|
| `services/auth/src/main/resources/application.yml` | Modify | Change `jwt.secret: ${JWT_SECRET:-super-secret-...}` → `jwt.secret: ${JWT_SECRET}` (no default) |
| `services/auth/src/main/kotlin/com/siga/auth/security/JwtService.kt` | Modify | Remove `:default-secret-key-...` fallback; add `@PostConstruct validateSecret()` that rejects empty/placeholder values |
| `.env.example` | Modify | Update `JWT_SECRET=change_me_to_a_long_random_secret` → `JWT_SECRET=changeme` (simpler, explicit placeholder) |

---

## Gateway Route Config (Target)

```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: false    # was: true — disabled to prevent auto-routes without RewritePath
      routes:
        # Auth
        - id: siga-auth
          uri: lb://siga-auth
          predicates:
            - Path=/api/auth/**
          filters:
            - RewritePath=/api/auth/(?<segment>.*), /api/v1/auth/$\{segment}

        # Inventory
        - id: siga-inventory
          uri: lb://siga-inventory
          predicates:
            - Path=/api/products/**
            - Path=/api/stores/**
            - Path=/api/inventory/**
          filters:
            - RewritePath=/api/(?<service>products|stores|inventory)/(?<segment>.*), /api/v1/$\{service}/$\{segment}

        # Sales
        - id: siga-sales
          uri: lb://siga-sales
          predicates:
            - Path=/api/sales/**
            - Path=/api/cash-shifts/**
          filters:
            - RewritePath=/api/(?<segment>.*), /api/v1/$\{segment}

        # Billing
        - id: siga-billing
          uri: lb://siga-billing
          predicates:
            - Path=/api/billing/**
            - Path=/api/comercial/**
          filters:
            - RewritePath=/api/billing/(?<segment>.*), /api/v1/billing/$\{segment}
            - RewritePath=/api/comercial/(?<segment>.*), /api/v1/billing/$\{segment}

        # Agent (no /v1/ — no RewritePath)
        - id: siga-agent
          uri: lb://siga-agent
          predicates:
            - Path=/api/agent/**
```

---

## JwtService.kt (Target — key changes)

```kotlin
@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    @PostConstruct
    fun validateSecret() {
        require(secret.isNotBlank()) { "JWT_SECRET must not be empty" }
        val knownInsecure = listOf("super-secret-key", "default-secret-key", "changeme")
        val match = knownInsecure.firstOrNull { secret.startsWith(it) }
        require(match == null) { "JWT_SECRET uses known insecure placeholder: '$match'. Generate a 256+ bit random secret." }
    }
    // ... rest unchanged
}
```

---

## Database Migration Strategy

### Clean RDS (fresh DB)
- No Flyway history exists. Modified V1 files run fresh.
- `CREATE SCHEMA IF NOT EXISTS` creates schemas; `SET search_path` applies them.
- Idempotent by design (all `CREATE TABLE IF NOT EXISTS` preserved).

### Existing Dev Database
- V1 checksums have changed. Flyway will reject with checksum mismatch.
- **Fix**: After applying V1 changes and before restarting services:
  ```bash
  # For each service database:
  docker-compose exec <service> flyway repair
  # Or connect to DB and delete/replace the V1 checksum:
  UPDATE flyway_schema_history SET checksum = <new_checksum> WHERE version = '1';
  ```
- **Recommended**: Reset dev databases entirely (no production data):
  ```bash
  docker-compose down -v && docker-compose up -d
  ```

---

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | JwtService startup validation | Unit test: mock `@Value("")` → expect `IllegalArgumentException` |
| Integration | Gateway routes resolve correctly | Start gateway + one service; curl endpoints verify RewritePath works |
| Integration | Agent route unchanged | curl `/api/agent/chat` → verify no prefix stripping |
| Integration | Flyway on fresh DB | Spin up clean PostgreSQL container; run Flyway validate — no errors |
| Manual | Flyway on existing DB | Re-run migrations after V1 changes + repair — verify schema_history updated |
| E2E | login → JWT flow | Full auth flow: login returns token, verify endpoint accepts it |
| Security | Missing JWT_SECRET | Start auth service without env var — verify startup fails |

---

## Migration / Rollout

Blocks are independent → can be implemented and deployed separately or together.

**Recommended order (dependencies)**:
1. Block 3 (JWT) — code-only, no DB migration, lowest risk
2. Block 1 (Gateway) — config-only, no DB migration
3. Block 2 (Flyway) — DB migration, requires DB reset or repair

**Rollback per block**:
- **Gateway**: Revert `application.yml` and `CustomerController.kt` → restore `discovery.locator.enabled: true`
- **Flyway**: Restore original V1 files → `flyway repair` to fix checksums back → restore `init-db.sh`
- **JWT**: Revert `application.yml`, `JwtService.kt`, `.env.example` → secret fallback restored

---

## Open Questions

- [ ] `Path=/api/comercial/**` in gateway routes — does this legacy path still have consumers? Confirm rewrite target is correct (`/api/v1/billing/...`).
- [ ] Are there any other services (not listed as explicit routes) that depend on Eureka discovery locator auto-routing? If yes, consider keeping locator enabled and adding explicit route definition that takes precedence instead.

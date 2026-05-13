# Tasks: infra-hardening

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~143 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single commit series — 3 commits, one per block |
| Delivery strategy | single-pr |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Commit | Notes |
|------|------|--------|-------|
| 1 | Gateway Routes — RewritePath + CustomerController | `fix(gateway): add RewritePath filters to all service routes` | No DB, config + controller change |
| 2 | Flyway Unification — V1 schema ownership | `fix(flyway): unify schema management — V1 migrations own DDL` | DB migration, requires dev DB reset |
| 3 | JWT Hardening — remove defaults, add validation | `fix(auth): harden JWT secret — remove defaults, add startup validation` | Code-only, no DB migration |

## Phase 1: Gateway Routes

- [x] 1.1 Disable `discovery.locator.enabled` in `services/gateway/src/main/resources/application.yml`
- [x] 1.2 Add `RewritePath` filter to auth route: `/api/auth/(.*)` → `/api/v1/auth/$1`
- [x] 1.3 Add `RewritePath` filter to inventory routes (products, stores, inventory)
- [x] 1.4 Add `RewritePath` filter to sales routes (sales, cash-shifts)
- [x] 1.5 Add `RewritePath` filters to billing routes including `/api/comercial/` → `/api/v1/billing/`
- [x] 1.6 Confirm agent route has NO RewritePath filter (no `/v1/` needed)
- [x] 1.7 Change `CustomerController.kt`: `@RequestMapping("/api/auth/customers")` → `"/api/v1/auth/customers"`

## Phase 2: Flyway Unification

- [x] 2.1 Modify `services/auth/.../V1__auth_init.sql` — add `CREATE SCHEMA IF NOT EXISTS auth;` + `SET search_path TO auth;`; prefix all `CREATE TABLE` with `auth.`
- [x] 2.2 Modify `services/sales/.../V1__sales_init.sql` — same pattern with `sales` schema
- [x] 2.3 Modify `services/inventory/.../V1__inventory_init.sql` — same pattern with `inventory` schema
- [x] 2.4 Modify `services/agent/.../V1__agent_init.sql` — same pattern with `agent` schema
- [x] 2.5 Verify auth `V2__` and `V3__` already use `auth.` prefix (no changes needed)
- [x] 2.6 Remove DDL execution lines from `scripts/db-init/init-db.sh`; keep schema + user creation only
- [x] 2.7 Add `flyway repair` procedure comment to init-db.sh or docs for existing dev DBs

## Phase 3: JWT Hardening

- [x] 3.1 In `services/auth/src/main/resources/application.yml`: change `jwt.secret: ${JWT_SECRET:-super-secret-key...}` → `jwt.secret: ${JWT_SECRET}` (no default)
- [x] 3.2 In `services/auth/.../security/JwtService.kt`: remove `:default-secret-key` fallback from `@Value`; add `@PostConstruct validateSecret()` rejecting blank/placeholder values
- [x] 3.3 Update `.env.example`: add `JWT_SECRET=changeme` entry

## Dependency Graph

```
Phase 1 (Gateway) ──── independent ────┐
Phase 2 (Flyway)  ──── independent ────┤  → Any order, no shared files
Phase 3 (JWT)     ──── independent ────┘
```

All three blocks touch **disjoint files** — zero file overlap. Order is arbitrary. Recommended implementation: 3 → 1 → 2 (JWT is smallest risk, Flyway requires dev DB reset).

## Verification Steps

| Block | Verification |
|-------|-------------|
| **Gateway** | Start gateway + any service; `curl POST /api/auth/login` → 200 (not 404); `curl /api/agent/chat` → agent responds (no prefix stripped); gateway logs show no duplicate Eureka routes |
| **Flyway** | Clean PostgreSQL: run `flyway migrate` → all schemas created; `init-db.sh` review: no DDL; auth V2/V3 grep confirms `auth.` prefix; existing dev: `flyway repair` then migrate |
| **JWT** | Start auth without `JWT_SECRET` env var → startup fails with error; `JWT_SECRET=changeme` → startup fails (placeholder rejected); `JWT_SECRET=<random256>` → starts fine; no hardcoded secret defaults in codebase (`grep -r "super-secret-key\|default-secret-key" services/`)

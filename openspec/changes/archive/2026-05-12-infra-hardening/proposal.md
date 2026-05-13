# Proposal: infra-hardening

## Intent

Fix 3 infrastructure defects discovered during exploration: broken gateway routing (all `/api/v1/` controllers unreachable), Flyway dual source of truth (init-db.sh creates schemas that migrations should own), and insecure JWT secret defaults (double fallback with hardcoded values). These block production deployment and create security risk.

## Scope

### In Scope
1. **Gateway routes** — add `StripPrefix=1` or rewrite every route to `/api/v1/` target
2. **Flyway unification** — all V1 migrations use explicit `schema.table`, add `CREATE SCHEMA IF NOT EXISTS`, remove DDL from init-db.sh
3. **JWT hardening** — remove YAML default, remove @Value fallback, add startup validation that `JWT_SECRET` is set

### Out of Scope
- API versioning scheme (keep `/api/v1/`)
- Removing backup_sql files (keep for reference)
- Fixing all insecure defaults across every service
- Python agent service
- Engineering harness / testing gaps

## Capabilities

**New Capabilities**: None — pure infra/config change, no new spec-level behavior.

**Modified Capabilities**: None — no requirements change at the spec level.

## Approach

### Issue 1 — Gateway Routes (3 approaches)

| # | Approach | Tradeoffs |
|---|----------|-----------|
| A | **Add `StripPrefix=1`** to all Spring Cloud Gateway route filters | ✅ Minimal config change. ⚠️ Agent (no `/v1/`) needs exception. |
| B | **Rewrite gateway routes** to `/api/v1/**` (e.g. `/api/auth/**` → `/api/v1/auth/**`) | ✅ Explicit, no strip magic. ❌ All route literals change — bigger diff. |
| C | **Hybrid**: StripPrefix on v1 routes + explicit route for agent | ⚠️ Same as A with explicit agent handler. |

**Recommendation**: Approach A — least diff, one filter addition per route. Add `--spring` to agent route to skip strip.

### Issue 2 — Flyway (3 approaches)

| # | Approach | Tradeoffs |
|---|----------|-----------|
| A | **Prefix all V1 migrations** with `CREATE SCHEMA IF NOT EXISTS <service>; SET search_path TO <service>;` | ✅ Corrects root cause. ❌ Requires modifying existing V1 files (git history changes). |
| B | **Add baseline-on-migrate** + keep V1 as-is, prefix only V2+ | ❌ Doesn't fix V1 — same risk on fresh RDS. |
| C | **New V1.1 migration** per service that re-aliases schemas | ❌ Complex, order-dependent. Mask rather than fix. |

**Recommendation**: Approach A — modify V1 files is correct. Remove schema DDL from `init-db.sh`. Keep `backup_sql/` as reference only.

### Issue 3 — JWT Hardening (2 approaches)

| # | Approach | Tradeoffs |
|---|----------|-----------|
| A | **application.yml**: delete `jwt.secret` default. **JwtService.kt**: delete `@Value` fallback. Add `@PostConstruct` validation. | ✅ No defaults, fails fast. ❌ Breaks local dev without `.env`. |
| B | **application.yml**: keep but warn. **JwtService.kt**: delete fallback, add validation. | ❌ YAML default still compilable — false sense of security. |

**Recommendation**: Approach A — security over convenience. Document `.env` setup in service README.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/gateway/src/main/resources/application.yml` | Modified | Add `StripPrefix=1` filters per route |
| `services/auth/src/main/resources/db/migration/V1__*.sql` | Modified | Add `CREATE SCHEMA` + schema prefix |
| `services/sales/src/main/resources/db/migration/V1__*.sql` | Modified | Same |
| `services/inventory/src/main/resources/db/migration/V1__*.sql` | Modified | Same |
| `services/billing/src/main/resources/db/migration/V1__*.sql` | Modified | Already has schema — verify consistency |
| `services/auth/src/main/resources/application.yml` | Modified | Remove `jwt.secret` default |
| `services/auth/src/main/kotlin/.../JwtService.kt` | Modified | Remove `@Value` fallback, add `@PostConstruct` |
| `scripts/db-init/init-db.sh` | Modified | Remove DDL, keep user/schema grants |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Gateway change breaks working agent route | Low | Verify agent route has no strip prefix |
| Flyway V1 migration re-run on existing DB | Med | Test on dev DB first; V1 migrations are `CREATE IF NOT EXISTS` |
| Local dev breaks without JWT env var | Med | Update `.env.example`; document in dev README |
| init-db.sh removal breaks CI pipeline | Med | Review CI workflows before removing DDL |

## Rollback Plan

1. **Gateway**: revert `application.yml` route filters
2. **Flyway**: revert modified V1 files; restore DDL in `init-db.sh`
3. **JWT**: restore defaults in `application.yml` and `JwtService.kt`
4. Execute per-block atomic commits: revert block-by-block, not wholesale

## Dependencies

- Docker Compose environment for local testing
- Dev PostgreSQL DB to test Flyway baseline behavior

## Commit Breakdown

3 strategic commits in order, all to `migracion-microservicios`, pushed only after final review:

1. **`fix(gateway): add StripPrefix=1 to all service routes`**
2. **`fix(flyway): unify schema management — V1 migrations own DDL`**
3. **`fix(auth): harden JWT secret — remove defaults, add startup validation`**

## Success Criteria

- [ ] **Gateway**: `curl /api/v1/auth/login` reaches auth controller (not 404)
- [ ] **Gateway**: agent route still works (no strip applied)
- [ ] **Flyway**: fresh DB applies all migrations without init-db.sh DDL
- [ ] **Flyway**: `init-db.sh` no longer creates application tables
- [ ] **JWT**: auth service fails startup if `JWT_SECRET` is unset
- [ ] **JWT**: no hardcoded `super-secret-key` or `default-secret-key` in codebase

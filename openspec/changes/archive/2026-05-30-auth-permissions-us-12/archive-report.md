# Archive Report: auth-permissions-us-12

**Change**: auth-permissions-us-12 (US 1.2 — Permisos Granulares para Empleados Polifuncionales)
**Archived**: 2026-05-30
**Artifact Store Mode**: hybrid (OpenSpec + Engram)
**Verdict**: PASS WITH WARNINGS

---

## Artifact Inventory

| Artifact | Path | Status |
|----------|------|--------|
| Proposal | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/proposal.md` | ✅ Archived |
| Design | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/design.md` | ✅ Archived |
| Tasks | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/tasks.md` | ✅ Archived (14/14 tasks ✅) |
| Verify Report | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/verify-report.md` | ✅ Archived |
| Spec (granular-permissions) | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/specs/granular-permissions/spec.md` | ✅ Archived |
| Spec (customer-auth) | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/specs/customer-auth/spec.md` | ✅ Archived |
| Archive Report | `openspec/changes/archive/2026-05-30-auth-permissions-us-12/archive-report.md` | ✅ This file |

---

## Specs Synced

| Domain | Action | Details |
|--------|--------|---------|
| `granular-permissions` | Created | New domain spec — no main spec existed; delta spec copied as-is (R1–R6, 8 permission codes, 6 requirements, 1 deferred) |
| `customer-auth` | Updated | Merged ADDED (R9, R10) and MODIFIED (R4.1, R5) requirements: EMPLOYEE role in hierarchy table, EMPLOYEE in R3 login rol values, R4.1 expanded for EMPLOYEE acceptance, R5 expanded with catalogue CRUD + verify endpoint |

### Merge Details — customer-auth

| Req | Type | Change |
|-----|------|--------|
| Roles & Hierarchy | Modified | Added EMPLOYEE ("Polifuncional") row |
| R3 Login — User success | Modified | Added `EMPLOYEE` to valid rol values |
| R4.1 Create User | Modified | Added EMPLOYEE role acceptance; added Create EMPLOYEE scenario; clarified no default role permissions for EMPLOYEE |
| R5 Permission Management | Modified | Replaced endpoint listing with full table (GET/POST/PUT/DELETE catalogue + assign/revoke/verify user); added Create permission code, Verify user has permission, Cross-tenant assign rejected scenarios |
| R9 EMPLOYEE Role | Added | New standalone requirement for domain model and entity enum acceptance |
| R10 Permission Codes in Auth Response | Added | New requirement for permission codes in login response payload |

---

## Implementation Summary

- **Tasks**: 14/14 completed and marked [x]
- **Tests**: 162/162 passed (0 failures)
- **New files**: V4 migration, UserPermissionRepository, UserPermissionRepositoryPort, UserPermissionMapper, UserPermissionJpaAdapter, ManageUserPermissionUseCase, PermissionController, 4 test files (44 new tests)
- **Modified files**: Enums.kt (entity), UserRole.kt (domain), UserMapper.kt, SecurityConfig.kt, LoginResult.kt, LoginUseCase.kt, LoginUseCaseTest.kt

### Warnings from Verification

1. **TDD Cycle Evidence table missing** (CRITICAL — documentation oversight, not implementation defect)
2. **R2 Multi-permission narrative** (⚠️ PARTIAL — covered piecewise but not as single end-to-end test)
3. **R5 Role-default merging** (⚠️ PARTIAL — deferred to future US; implementation covers only user-specific granular permissions)

---

## Source of Truth Updated

The following main specs now reflect the new behavior:
- `openspec/specs/granular-permissions/spec.md`
- `openspec/specs/customer-auth/spec.md`

## Engram Observations

| Artifact | Observation ID |
|----------|----------------|
| Proposal | #748 — `sdd/auth-permissions-us-12/proposal` |
| Design | #750 — `sdd/auth-permissions-us-12/design` |
| Tasks | #751 — `sdd/auth-permissions-us-12/tasks` |
| Verify Report | #756 — `sdd/auth-permissions-us-12/verify-report` |
| Archive Report | #769 — `sdd/auth-permissions-us-12/archive-report` (this observation) |
| Spec | #749 — `sdd/auth-permissions-us-12/spec` |

---

## SDD Cycle Complete

The change has been fully planned (propose → spec → design → tasks), implemented (apply), verified (verify), and archived. Ready for the next change.

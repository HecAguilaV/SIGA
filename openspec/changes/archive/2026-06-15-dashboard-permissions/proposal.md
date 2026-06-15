# Proposal: Dashboard Granular Permissions Integration

## Intent

Implement strict permission-based access control (PBAC) in the Dashboard UI. Currently, the frontend relies on a single `rol` string, which is brittle and couples the UI to backend role definitions. Moving to granular permissions allows for flexible access management (e.g., polyfunctional employees) and aligns with the backend's granular permission system.

## Scope

### In Scope
- Refactor `UserSession` type in `packages/shared/auth.ts` and `apps/dashboard/src/lib/types/auth.ts` to replace `rol` with `permissions: string[]`.
- Update `apps/dashboard/src/lib/server/auth.server.ts` to extract the `permissions` claim from the JWT.
- Replace `ROLE_GUARDS` with `PERMISSION_GUARDS` in `apps/dashboard/src/hooks.server.ts`.
- Update `apps/dashboard/src/lib/components/layout/Sidebar.svelte` to toggle visibility based on permissions instead of roles.
- Support bilingual commit messages (Spanish/English) in the `migracion-microservicios` branch.

### Out of Scope
- Backend implementation of permission assignment (covered by `granular-permissions` spec).
- Dynamic permission management UI (deferred to future work).
- Legacy role support (once permissions are implemented, roles will be ignored by the frontend).

## Capabilities

### Modified Capabilities
- `granular-permissions`: Extend to cover frontend consumption of permissions for route guarding and UI visibility.
- `customer-auth`: Update session building to include permissions extracted from JWT claims.

## Approach

**Strict Permission-Based**: The frontend becomes agnostic to role names (`ADMINISTRATOR`, `CASHIER`). Access is determined solely by the presence of a specific permission string (e.g., `INVENTORY_READ`, `REPORTS_VIEW`) in the `UserSession.permissions` array.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `packages/shared/auth.ts` | Modified | Update `UserSession` interface. |
| `apps/dashboard/src/lib/types/auth.ts` | Modified | Update `UserSession` interface. |
| `apps/dashboard/src/lib/server/auth.server.ts` | Modified | Update `buildUserSession` to extract permissions from JWT. |
| `apps/dashboard/src/hooks.server.ts` | Modified | Replace `ROLE_GUARDS` with `PERMISSION_GUARDS`. |
| `apps/dashboard/src/lib/components/layout/Sidebar.svelte` | Modified | Update navigation visibility logic. |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Missing Permissions in JWT | Medium | Ensure backend update matches frontend expectations before merging. |
| User Lockout | Low | Verify that migration preserves essential access during the transition. |
| Role-Permission Mismatch | Low | Use a clear mapping during the transition phase if necessary. |

## Rollback Plan

Revert all changes in the affected files to restore `rol`-based logic. This is safe as long as the backend still provides the `rol` claim in the JWT.

## Dependencies

- Backend must provide `permissions` claim in the JWT (as specified in `granular-permissions` spec).

## Success Criteria

- [ ] `UserSession` correctly populated with `permissions` from JWT.
- [ ] Sidebar items only visible to users with corresponding permissions.
- [ ] Protected routes (e.g., `/users`) correctly blocked (403) for users without the required permission.
- [ ] No regressions in authentication flow.

## OWASP Analysis (A01: Broken Access Control)

Moving from RBAC to PBAC reduces the risk of insecure direct object references and functional-level access control failures by enforcing the principle of least privilege at a more granular level. The frontend enforcement complements backend checks.

## Proposal question round

1. **Permission Naming Convention**: Should we use the exact codes from `granular-permissions` (e.g., `INVENTORY_READ`) or a more namespaced approach for the frontend (e.g., `dashboard:inventory:view`)?
2. **Default Permissions**: Should the frontend assume a set of "base" permissions if none are provided, or strictly default to zero access?
3. **Role Compatibility**: During transition, should the frontend support *both* roles and permissions, or is a hard switch to permissions preferred? (Current assumption: Hard switch).
4. **JWT Size**: Extracting many granular permissions into the JWT payload increases its size. Are we confident the current `siga_token` cookie can handle the potential growth?

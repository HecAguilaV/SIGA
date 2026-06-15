# Tasks: Dashboard Granular Permissions Integration

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~150 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR (strategic commits) |
| Delivery strategy | exception-ok |
| Chain strategy | stacked-to-main |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: stacked-to-main
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Foundation & Auth Logic | PR 1 | Types, utilities, and JWT extraction |
| 2 | Guarding & UI Updates | PR 1 | Hooks, layout, Sidebar, and tests |

## Phase 1: Foundation (Types & Utilities)

- [x] 1.1 Update `packages/shared/auth.ts`: Add `permissions: string[]` to `UserSession` interface.
- [x] 1.2 Update `apps/dashboard/src/lib/types/auth.ts`: Sync `UserSession` interface with the shared package.
- [x] 1.3 Create `apps/dashboard/src/lib/auth/permissions.ts`: Implement `hasPermission` utility and define `PERMISSION_GUARDS` mapping.

## Phase 2: Authentication logic (JWT extraction)

- [x] 2.1 Update `apps/dashboard/src/lib/server/auth.server.ts`: Modify `buildUserSession` to extract and populate `permissions` from JWT claims.

## Phase 3: Route Guarding (Server-side)

- [x] 3.1 Refactor `apps/dashboard/src/hooks.server.ts`: Replace `ROLE_GUARDS` logic with `PERMISSION_GUARDS` check.
- [x] 3.2 Update `apps/dashboard/src/routes/(dashboard)/+layout.server.ts`: Update layout-level permission validation.

## Phase 4: UI & Store (Reactivity)

- [x] 4.1 Update `apps/dashboard/src/lib/stores/auth.svelte.ts`: Add derived `userPermissions` and `canAccess` state to the auth store.
- [x] 4.2 Refactor `apps/dashboard/src/lib/components/layout/Sidebar.svelte`: Update navigation link visibility to use permission checks instead of roles.

## Phase 5: Testing & Verification

- [x] 5.1 Unit Test `permissions.ts`: Verify `hasPermission` logic, including the wildcard `*` for full access.
- [x] 5.2 Integration Test `hooks.server.ts`: Verify 403 vs 200 responses based on mocked user permissions.
- [x] 5.3 E2E/Manual: Confirm sidebar items correctly toggle based on simulated permissions.

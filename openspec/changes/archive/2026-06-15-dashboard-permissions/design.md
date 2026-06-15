# Design: Dashboard Granular Permissions Integration

## Technical Approach

The implementation shifts from Role-Based Access Control (RBAC) to Permission-Based Access Control (PBAC). The frontend will no longer check for role strings like `ADMINISTRATOR` or `CASHIER`. Instead, it will verify the presence of specific permission codes (e.g., `INVENTORY_READ`, `SALES_CREATE`) in the user session.

This approach involves:
1. Updating the `UserSession` type to include a `permissions` array.
2. Enhancing the JWT decoding logic to extract permissions.
3. Centralizing route protection and UI visibility logic using permission checks.
4. Refactoring existing role-guarded routes to use permission-guarded logic.

## Architecture Decisions

### Decision: Centralized Permission Utility

**Choice**: Create `apps/dashboard/src/lib/auth/permissions.ts` as the single source of truth for permission logic.
**Alternatives considered**: Localizing checks in `hooks.server.ts` and individual components.
**Rationale**: Centralization ensures consistency between server-side route guards, client-side UI visibility, and manual checks in `load` functions. It reduces duplication and makes it easier to audit access rules.

### Decision: Role Deprecation in Logic

**Choice**: Roles will still be stored in `UserSession` for display purposes (e.g., in `UserProfileMenu`) but will be strictly ignored for authorization logic.
**Alternatives considered**: Complete removal of `rol` field.
**Rationale**: Keeping the role name provides better UX for users who are accustomed to their titles, while PBAC ensures technical flexibility and security.

### Decision: Defense-in-Depth Guarding

**Choice**: Enforce permissions at three levels: `hooks.server.ts` (route prefix), `+layout.server.ts` (layout level), and `+page.server.ts` (action/load level).
**Alternatives considered**: Only at `hooks.server.ts`.
**Rationale**: Multi-level guarding prevents accidental exposure if a hook is bypassed or misconfigured. It also allows for more granular checks (e.g., `READ` vs `WRITE`) within the same route tree.

## Data Flow

1. **JWT Extraction**: `apps/dashboard/src/lib/server/auth.server.ts` decodes the `permissions` claim from the JWT.
2. **Session Population**: `UserSession` is populated with the permissions array.
3. **Route Guarding**: `hooks.server.ts` intercepts requests and compares the route against `PERMISSION_GUARDS`.
4. **UI Filtering**: `Sidebar.svelte` filters navigation items based on the user's effective permissions.
5. **Reactive Stores**: `auth.svelte.ts` provides a derived `userPermissions` store for component-level reactivity.

```
JWT (permissions: ["INVENTORY_READ"]) ──→ buildUserSession() ──→ UserSession
                                                                    │
    ┌───────────────────────────────────────────────────────────────┤
    ▼                                                               ▼
hooks.server.ts (Route Check)                          auth.svelte.ts (Store)
    │                                                               │
    └───────→ 403 Forbidden (if lacks)                       Sidebar.svelte (UI)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `packages/shared/auth.ts` | Modify | Add `permissions: string[]` to `UserSession`. |
| `apps/dashboard/src/lib/types/auth.ts` | Modify | Sync with `shared/auth.ts`. |
| `apps/dashboard/src/lib/auth/permissions.ts` | Create | New utility for permission checks and `PERMISSION_GUARDS` map. |
| `apps/dashboard/src/lib/server/auth.server.ts` | Modify | Update `buildUserSession` to extract permissions from JWT claims. |
| `apps/dashboard/src/hooks.server.ts` | Modify | Replace `ROLE_GUARDS` with `PERMISSION_GUARDS`. |
| `apps/dashboard/src/lib/stores/auth.svelte.ts` | Modify | Add `userPermissions` derived store. |
| `apps/dashboard/src/lib/components/layout/Sidebar.svelte` | Modify | Update navigation items to use `permissions` instead of `roles`. |
| `apps/dashboard/src/routes/(dashboard)/+layout.server.ts` | Modify | Replace `ROLE_GUARDS` with `PERMISSION_GUARDS`. |

## Interfaces / Contracts

### UserSession Update
```typescript
export interface UserSession {
	id: string;
	email: string;
	name: string;
	principalType: PrincipalType;
	rol?: string; // Kept for display purposes
	permissions: string[]; // New: mandatory for authorization
	tenantId?: string;
	avatar?: string;
}
```

### Permission Guard Mapping
```typescript
export const PERMISSION_GUARDS: Record<string, string[]> = {
	'/products': ['INVENTORY_READ'],
	'/stores': ['KIOSK_ADMIN'],
	'/categories': ['INVENTORY_READ'],
	'/users': ['USERS_MANAGE'], // Assumed code for user management
	'/analytics': ['REPORTS_VIEW'],
	'/pos': ['SALES_CREATE']
};
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | `hasPermission` helper | Test with various user permission sets (empty, partial, full). |
| Integration | `hooks.server.ts` | Mock JWT with different permissions and verify 200 vs 403 on guarded routes. |
| E2E | Sidebar Visibility | Log in as users with different permissions and verify sidebar item visibility. |

## Migration / Rollout

No data migration required as the change relies on JWT claims provided by the backend. The rollout is "atomic" on the frontend; once deployed, the UI will strictly use permissions.

## Open Questions

- [ ] Confirm exact code for user management (currently using `USERS_MANAGE`).
- [ ] Verify if `KIOSK_ADMIN` is the correct permission for the `/stores` route.

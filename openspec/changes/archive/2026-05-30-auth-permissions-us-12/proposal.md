# Proposal: US 1.2 — Permisos Granulares para Empleados Polifuncionales

## Intent

US-1.2 requires granular permission assignment: Elizabeth (owner/tenant) assigns specific permissions per employee (Héctor → KIOSK_ADMIN, Yesenia → sales, Luis → DELIVERY_VIEW). The full hexagonal stack for UserPermission exists as DB schema + entities + domain models, but **the pipeline stops there** — no port, adapter, use case, or controller exposes this functionality. Without this, employees cannot be assigned granular permissions, every user is all-or-nothing.

## Scope

### In Scope
- Permission codes seed data (Flyway V4): INVENTORY_READ, INVENTORY_WRITE, INVENTORY_DELETE, KIOSK_ADMIN, DELIVERY_VIEW, REPORTS_VIEW, SALES_CREATE, SALES_READ
- Full hexagonal stack for UserPermission: `UserPermissionRepository` → `UserPermissionMapper` → `UserPermissionRepositoryPort` → `UserPermissionJpaAdapter` → `ManageUserPermissionUseCase` → `PermissionController`
- `PermissionController` REST API: CRUD permissions, assign/revoke permissions to users, verify if user has permission
- `EMPLOYEE` role added to `UserRole` enum (entity + domain)
- Strict TDD: unit tests (use case, Mockito) + integration tests (adapter, controller via BaseIntegrationTest)

### Out of Scope
- Agent permission inheritance (US-4.x — deferred)
- Role-based permission assignment via `RolePermission` (direct-to-user only)
- Permission middleware in other services to check JWT claims (deferred)
- Frontend (API-only change)

## Capabilities

### New Capabilities
- `granular-permissions`: CRUD for permission definitions + user-permission assignment + verification

### Modified Capabilities
- `customer-auth`: Extend user model with `EMPLOYEE` role; auth service includes permission codes in tenant-scoped responses

## Approach

Follow existing hexagonal patterns (same structure as `UserStore` → `UserStoreRepositoryPort` → `UserStoreJpaAdapter`):

1. **Flyway V4** — seed permission codes (`services/auth/src/main/resources/db/migration/V4__seed_permissions.sql`)
2. **Repository** — `UserPermissionRepository` (Spring Data JPA, composite key via `UserPermissionId`)
3. **Mapper** — `UserPermissionMapper` (entity ↔ domain)
4. **Port** — `UserPermissionRepositoryPort` (findByUserId, findByPermissionId, save, delete, exists)
5. **Adapter** — `UserPermissionJpaAdapter` (`@Component`)
6. **Use Case** — `ManageUserPermissionUseCase` (`@Service`, validates tenant ownership before assign)
7. **Controller** — `PermissionController` (`/api/v1/auth/permissions`) with CRUD + assign + verify endpoints
8. **Role** — Add `EMPLOYEE` to `entity/Enums.kt` and `domain/model/UserRole.kt`
9. **Security** — Add `/api/v1/auth/permissions/**` as authenticated in `SecurityConfig`

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/auth/src/main/resources/db/migration/V4__seed_permissions.sql` | **New** | Permission codes seed data |
| `services/auth/src/main/kotlin/.../domain/port/UserPermissionRepositoryPort.kt` | **New** | Hexagonal port interface |
| `services/auth/src/main/kotlin/.../infrastructure/mapper/UserPermissionMapper.kt` | **New** | Entity ↔ domain mapper |
| `services/auth/src/main/kotlin/.../infrastructure/adapter/UserPermissionJpaAdapter.kt` | **New** | JPA adapter implementing port |
| `services/auth/src/main/kotlin/.../application/usecase/ManageUserPermissionUseCase.kt` | **New** | Business logic use case |
| `services/auth/src/main/kotlin/.../controller/PermissionController.kt` | **New** | REST endpoints |
| `services/auth/src/main/kotlin/.../entity/Enums.kt` | **Modified** | Add `EMPLOYEE` role |
| `services/auth/src/main/kotlin/.../domain/model/UserRole.kt` | **Modified** | Add `EMPLOYEE` role |
| `services/auth/src/main/kotlin/.../security/SecurityConfig.kt` | **Modified** | Add permissions route to authenticated |
| `services/auth/src/test/.../application/usecase/ManageUserPermissionUseCaseTest.kt` | **New** | Unit tests (Mockito) |
| `services/auth/src/test/.../infrastructure/adapter/UserPermissionJpaAdapterTest.kt` | **New** | Integration tests (H2) |
| `services/auth/src/test/.../PermissionControllerIntegrationTest.kt` | **New** | Controller tests (MockMvc) |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Missing `findByPermissionId` in entity | Low | Verify `UserPermissionRepository` query methods match entity composite key pattern (`findById_UserId`, `findById_PermissionId`) |
| Tenant spoofing via assign endpoint | Med | `manageUserPermissionUseCase` validates assigner belongs to same tenant as target user |
| Permission code collisions | Low | V4 seed uses `ON CONFLICT DO NOTHING` or unique constraint violation handling |

## Rollback Plan

1. Revert V4 migration: `DROP TABLE IF EXISTS auth.permissions CASCADE` + re-run V3 → V4 rollback script
2. Remove new files via `git revert` of the feature commit
3. Remove `EMPLOYEE` from enums (entity + domain)
4. Revert `SecurityConfig` route additions

## Dependencies

- V1 migration (permissions DB schema) must be applied — already present
- BaseIntegrationTest infrastructure — already present

## Success Criteria

- [ ] `ManageUserPermissionUseCase` unit tests pass (assign, revoke, exists, reject spoofing)
- [ ] `UserPermissionJpaAdapter` integration tests pass (CRUD via H2)
- [ ] `PermissionController` integration tests pass (assign, verify, list via MockMvc)
- [ ] `EMPLOYEE` role accepted in user creation and permission assignment
- [ ] Seed permissions present after Flyway V4 runs
- [ ] JaCoCo coverage ≥ 80% for new code

# Design: US 1.2 — Granular Permissions for Multifunctional Employees

## Technical Approach

Complete the hexagonal stack for UserPermission by mirroring the existing **UserStore pattern**: Repository → Mapper → Port → Adapter → UseCase → Controller. Add V4 Flyway seed with 8 permission codes. Extend login response with effective permissions. Add `EMPLOYEE` enum value to both entity and domain `UserRole`.

## Architecture Decisions

### Decision 1: PermissionController Ownership

| Option | Tradeoff | Decision |
|--------|----------|----------|
| **New PermissionController** for all endpoints | Clean separation; matches existing controller-per-domain pattern | **Chosen** |
| Add endpoints to UserController | Fewer files; couples permission logic with user CRUD | Rejected — violates SRP |

`PermissionController` handles catalogue CRUD at `/api/v1/auth/permissions` and user-permission assignment/verify at `/api/v1/auth/users/{id}/permissions`.

### Decision 2: Tenant Scoping in UseCase

| Option | Tradeoff | Decision |
|--------|----------|----------|
| **UseCase validates tenant** from JWT | Consistent with UserController pattern; prevents spoofing at business layer | **Chosen** |
| Controller-only validation | Leaks tenant concern; inconsistent with existing architecture | Rejected |

`ManageUserPermissionUseCase` reads `tenantId` from JWT claims via the same `getCustomerIdFromSecurityContext()` helper pattern used in `UserController`, looks up target user's `customerId`, and throws `IllegalArgumentException` (→ 403) on mismatch.

### Decision 3: Login Response includes Permissions

Extend `LoginResult` with `permissions: List<String>`. `LoginUseCase.authenticateUser()` fetches effective permissions via `UserPermissionRepositoryPort.findByUserId()` after successful auth. Empty list for users with no granular permissions — backward-compatible.

### Decision 4: UserPermissionRepository Query Pattern

Follow `UserStoreRepository` exactly: `JpaRepository<UserPermission, UserPermissionId>` with `findById_UserId()` and `findById_PermissionId()` derived query methods. The `UserPermissionId` composite key already exists in the entity.

## Data Flow

```
Assign:   Client ──POST /api/v1/auth/users/{id}/permissions──→
          PermissionController → ManageUserPermissionUseCase
          (validates tenantId vs target user's customerId)
          → UserPermissionRepositoryPort
          → UserPermissionJpaAdapter → UserPermissionRepository
          → auth.user_permissions (DB)

Verify:   GET .../verify?code=INVENTORY_READ → PermissionController
          → UseCase.verify(userId, code, tenantId)
          → UserPermissionRepositoryPort.findByUserId(userId)
          → check if code matches user's assigned permissions
          → { hasPermission: true/false }

Login:    POST /api/v1/auth/login → LoginUseCase
          (auth success) → UserPermissionRepositoryPort.findByUserId(user.id)
          → LoginResult.permissions = [assigned codes]
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `.../db/migration/V4__seed_permissions.sql` | Create | Seed 8 permission codes |
| `.../repository/UserPermissionRepository.kt` | Create | Spring Data JPA (`UserPermissionId` composite key) |
| `.../domain/port/UserPermissionRepositoryPort.kt` | Create | Hexagonal port interface |
| `.../infrastructure/mapper/UserPermissionMapper.kt` | Create | Entity ↔ domain (mirror `UserStoreMapper`) |
| `.../infrastructure/adapter/UserPermissionJpaAdapter.kt` | Create | JPA adapter (`@Component`, mirror `UserStoreJpaAdapter`) |
| `.../application/usecase/ManageUserPermissionUseCase.kt` | Create | Business logic + tenant validation |
| `.../controller/PermissionController.kt` | Create | REST endpoints |
| `.../entity/Enums.kt` | Modify | Add `EMPLOYEE` to entity `UserRole` |
| `.../domain/model/UserRole.kt` | Modify | Add `EMPLOYEE` to domain `UserRole` |
| `.../infrastructure/mapper/UserMapper.kt` | Modify | Add `EMPLOYEE → EMPLOYEE` mapping |
| `.../application/usecase/LoginResult.kt` | Modify | Add `permissions: List<String>` field |
| `.../application/usecase/LoginUseCase.kt` | Modify | Inject `UserPermissionRepositoryPort`, fetch permissions |
| `.../ManageUserPermissionUseCaseTest.kt` | Create | Unit tests (Mockito) |
| `.../adapter/UserPermissionJpaAdapterTest.kt` | Create | Integration tests (H2, FK user setup) |
| `.../PermissionControllerIntegrationTest.kt` | Create | Controller tests (MockMvc + JWT claims) |

## Interfaces

```kotlin
// Port — services/auth/.../domain/port/UserPermissionRepositoryPort.kt
interface UserPermissionRepositoryPort {
    fun findByUserId(userId: UUID): List<UserPermission>
    fun findByPermissionId(permissionId: UUID): List<UserPermission>
    fun save(userPermission: UserPermission): UserPermission
    fun deleteByUserIdAndPermissionId(userId: UUID, permissionId: UUID)
    fun existsByUserIdAndPermissionId(userId: UUID, permissionId: UUID): Boolean
}

// REST — PermissionController
POST   /api/v1/auth/users/{userId}/permissions           → 200 + body
DELETE /api/v1/auth/users/{userId}/permissions/{permId}  → 204
GET    /api/v1/auth/users/{userId}/permissions            → 200 + list
GET    /api/v1/auth/users/{userId}/permissions/verify?code=X → { hasPermission: boolean }

// LoginResult (modified)
data class LoginResult(
    ...existing fields...,
    val permissions: List<String> = emptyList()
)
```

## Threat Model

| Threat | Mitigation |
|--------|------------|
| Cross-tenant permission assignment | UseCase checks target user's `customerId` vs JWT `tenantId` (same pattern as `UserController`) |
| Permission code injection | V4 seed uses `UNIQUE` constraint on `code`; CRUD validates non-blank code |
| Unauthorized catalogue access | All endpoints covered by `anyRequest().authenticated()` in SecurityConfig |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | UseCase: assign, revoke, verify, cross-tenant rejection | Mockito on both `UserPermissionRepositoryPort` and `UserRepositoryPort` |
| Integration | Adapter: CRUD via H2 with FK user | `@SpringBootTest` + `UserJpaAdapter` for user setup |
| Integration | Controller: assign, verify, list, cross-tenant | `BaseIntegrationTest` (MockMvc) + JWT claim setup per `UserControllerIntegrationTest` pattern |
| Unit | LoginUseCase includes permissions | Mockito on `UserPermissionRepositoryPort` |

## Migration / Rollout

No migration required. V4 is purely additive seed data. Rollback via `git revert` of feature commit. `EMPLOYEE` value is additive — existing data unaffected.

## Permission Seed Values

| Code | Category |
|------|----------|
| INVENTORY_READ | INVENTORY |
| INVENTORY_WRITE | INVENTORY |
| INVENTORY_DELETE | INVENTORY |
| KIOSK_ADMIN | KIOSK |
| DELIVERY_VIEW | DELIVERY |
| REPORTS_VIEW | REPORTS |
| SALES_CREATE | SALES |
| SALES_READ | SALES |

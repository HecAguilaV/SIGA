# Tasks: US 1.2 — Permisos Granulares para Empleados Polifuncionales

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~720 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (Foundation, ~250) → PR 2 (UseCase, ~210) → PR 3 (Controller+Login, ~260) |
| Delivery strategy | ask-on-risk |
| Chain strategy | stacked-to-main |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Foundation: EMPLOYEE role + V4 seed + persistence stack + adapter tests | PR 1 | base=`migracion-microservicios`; standalone |
| 2 | Core: `ManageUserPermissionUseCase` + unit tests | PR 2 | base=`main` after PR 1 |
| 3 | REST + Login: `PermissionController` + integration tests + Login response | PR 3 | base=`main` after PR 2 |

## Phase 1: Foundation — Role Enum + V4 Seed + Persistence Stack

- [x] 1.1 Add `EMPLOYEE` to `entity/Enums.kt`, `domain/model/UserRole.kt`, and `infrastructure/mapper/UserMapper.kt` mapping
- [x] 1.2 Create `db/migration/V4__seed_permissions.sql` with 8 permission codes
- [x] 1.3 Create `repository/UserPermissionRepository.kt` (Spring Data JPA, `UserPermissionId` composite key)
- [x] 1.4 Create `domain/port/UserPermissionRepositoryPort.kt` (hexagonal port interface)
- [x] 1.5 Create `infrastructure/mapper/UserPermissionMapper.kt` (entity ↔ domain, mirror `UserStoreMapper`)
- [x] 1.6 Create `infrastructure/adapter/UserPermissionJpaAdapter.kt` (implements port, mirror `UserStoreJpaAdapter`)
- [x] 1.7 Create `UserPermissionJpaAdapterTest.kt` — integration: CRUD via H2 (FK user setup, mirror `UserStoreJpaAdapterTest`)

## Phase 2: Core — UseCase + Unit Tests

- [x] 2.1 Create `application/usecase/ManageUserPermissionUseCase.kt` (assign, revoke, verify, exists; tenant ownership validation via JWT `tenantId` vs target user's `customerId`)
- [x] 2.2 Create `ManageUserPermissionUseCaseTest.kt` — unit: assign, revoke, verify, cross-tenant rejection (Mockito on `UserPermissionRepositoryPort` + `UserRepositoryPort`)

## Phase 3: REST — Controller + Integration Tests

- [x] 3.1 Create `controller/PermissionController.kt` — CRUD at `/api/v1/auth/permissions`, assign/revoke/verify at `/api/v1/auth/users/{id}/permissions`
- [x] 3.2 Create `PermissionControllerIntegrationTest.kt` — integration: assign, verify, list, cross-tenant rejection (MockMvc via `BaseIntegrationTest`, JWT claim setup per `UserControllerIntegrationTest`)

## Phase 4: Integration — Login Response Enhancement

- [x] 4.1 Add `permissions: List<String> = emptyList()` to `LoginResult.kt`
- [x] 4.2 Inject `UserPermissionRepositoryPort` + `PermissionRepositoryPort` in `LoginUseCase`, fetch permission codes via `findByUserId()` + `findById()` after successful user auth
- [x] 4.3 Update `LoginUseCaseTest.kt` — assert permissions field in response for User login path (Customer returns emptyList)

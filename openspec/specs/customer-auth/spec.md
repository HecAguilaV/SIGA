# Customer Authentication Specification

## Purpose

Define auth flows, tenant-scoped user management, and granular permission model for SIGA. Covers registration (Customer + company), email verification, dual-principal login (Customer or User), role-based access (Owner/Admin/Employee), granular permissions with overrides, and plan-based limits.

## Domain Model — Roles & Hierarchy

### Hierarchy

| Rol | Quién es | Privilegios | Cantidad |
|-----|----------|-------------|----------|
| **Dueño (Owner)** | Customer que registró la empresa | Control TOTAL. Crea admin/empleados, asigna permisos. | 1 por empresa (el Customer) |
| **Admin** | Empleado con role=ADMINISTRATOR | Según permisos asignados. Puede recibir TODOS (Super Admin 2) si el dueño decide. | Máximo 1 por empresa |
| **Cajero** | Empleado con role=CASHIER | Permisos default de cajero + overrides que asigne el dueño/admin | Ilimitado según plan |
| **Inventario** | Empleado con role=OPERATOR | Permisos default de operador + overrides que asigne el dueño/admin | Ilimitado según plan |
| **Polifuncional** | Empleado con role=EMPLOYEE | Sin permisos default. Todos los permisos vía UserPermission | Ilimitado según plan |

### Principios

1. **Dueño ≠ User**: El Dueño es un `Customer`, no pasa por RBAC. Crea la empresa y tiene control absoluto.
2. **Admin es un User**: El Admin es un empleado como cualquier otro, pero con role=ADMINISTRATOR. El dueño le asigna los permisos que considere.
3. **Super Admin 2**: Cualquier empleado PUEDE llegar a tener todos los permisos si el dueño (o quien tenga permisos suficientes) se los asigna vía `UserPermission`. No es un rol especial — es la suma de todos los permisos granulares.
4. **Permisos granulares**: Existen `Permission` (catálogo), `RolePermission` (defaults por rol), y `UserPermission` (overrides por usuario). El dueño/admin puede asignar/revocar cualquier permiso a cualquier empleado.
5. **Límite por plan**: La cantidad de empleados activos está limitada por `Customer.planId` → servicio billing.

### Permisos — Funcionamiento

```
Permission (catálogo global)
  ├── RolePermission (permisos DEFAULT que tiene cada rol al crearse)
  └── UserPermission (permisos EXTRA asignados a un usuario específico)

Efectivo total = RolePermission ∪ UserPermission
```

El dueño (Customer) NO necesita permisos — tiene control inherente sobre su tenant.

## Security Requirements

| Control | Standard |
|---------|----------|
| Passwords | BCrypt hash |
| JWT | HMAC256, 24h expiry, claims: `tenantId`, `principalType`, `rol`, `sub` |
| Verify tokens | 24h expiry |
| Tenant isolation | All User operations filtered by JWT `customerId` |
| Backward compat | Existing public endpoints remain accessible |
| Permission check | Middleware/service checks effective permissions on sensitive operations |

## Requirements

### R1: Customer Registration

`POST /api/v1/auth/register`. Creates Customer (`isActive=false`), sends verification email, returns `201 Pending`.

#### Scenario: Successful registration

- GIVEN a valid request (email, password, name, companyName)
- WHEN POST /api/v1/auth/register
- THEN 201 + `{ status: "pending" }`
- AND Customer created with BCrypt password hash, `isActive=false`
- AND verification email sent

#### Scenario: Duplicate email

- GIVEN existing Customer with email "a@b.com"
- WHEN POST /api/v1/auth/register with email "a@b.com"
- THEN 409 Conflict

#### Scenario: Missing required fields

- GIVEN request without email/password/name/companyName
- WHEN POST /api/v1/auth/register
- THEN 400 Bad Request

### R2: Email Verification

`GET /api/v1/auth/verify?token=<token>`. Activates Customer (`isActive=true`).

#### Scenario: Successful verification

- GIVEN a pending Customer with valid verification token
- WHEN GET /api/v1/auth/verify?token=valid-token
- THEN 200 + Customer `isActive=true`, token invalidated

#### Scenario: Expired token

- GIVEN a verification token older than 24h
- WHEN GET /api/v1/auth/verify?token=expired-token
- THEN 410 Gone

#### Scenario: Invalid token

- GIVEN a non-existent verification token
- WHEN GET /api/v1/auth/verify?token=invalid-token
- THEN 404 Not Found

### R3: Login

`POST /api/v1/auth/login`. Authenticates Customer (tried first) or User. Returns JWT with `principalType`.

#### Scenario: Customer login success

- GIVEN an active Customer with valid credentials
- WHEN POST /api/v1/auth/login
- THEN 200 + JWT with `tenantId=customer.id`, `principalType=customer`, `rol=customer`

#### Scenario: User login success

- GIVEN an active User (customerId=1) with valid credentials
- WHEN POST /api/v1/auth/login
- THEN 200 + JWT with `tenantId=1`, `principalType=user`, `rol=ADMINISTRATOR|CASHIER|OPERATOR|EMPLOYEE`

#### Scenario: Inactive Customer rejected

- GIVEN a Customer with `isActive=false`
- WHEN POST /api/v1/auth/login with valid credentials
- THEN 403 Forbidden

#### Scenario: Inactive User rejected

- GIVEN a User with `isActive=false`
- WHEN POST /api/v1/auth/login with valid credentials
- THEN 403 Forbidden

#### Scenario: Wrong credentials

- GIVEN any registered principal
- WHEN POST /api/v1/auth/login with wrong password
- THEN 401 Unauthorized (generic, no principal disclosure)

#### Scenario: Neither Customer nor User matches

- GIVEN no principal exists with the given email
- WHEN POST /api/v1/auth/login
- THEN 401 Unauthorized

### R4: User Management (Tenant Scoped)

All User CRUD is scoped to the authenticated Customer's tenant. Only the Customer (dueño) or users with sufficient permissions can manage users.

#### R4.1: Create User

`POST /api/v1/auth/users` (Auth required: Customer or user with `user:create` permission). The system MUST accept `EMPLOYEE` as a valid role. EMPLOYEE users MUST NOT receive default role permissions — only granular UserPermission assignments.

- GIVEN authenticated Customer (`tenantId=1`)
- WHEN POST /api/v1/auth/users with `{ email, password, firstName, role: EMPLOYEE }`
- THEN 201 + User created with `customerId=1`, role=EMPLOYEE, no default role permissions

- GIVEN an authenticated Customer (`tenantId=1`)
- WHEN POST /api/v1/auth/users with `{ email, password, firstName, role: CASHIER }`
- THEN 201 + User created with `customerId=1`, BCrypt password, `isActive=true`
- AND role default permissions are assigned via RolePermission

- GIVEN a Customer who already has 1 admin (role=ADMINISTRATOR)
- WHEN POST /api/v1/auth/users with `{ role: ADMINISTRATOR }`
- THEN 409 Conflict — max 1 admin per tenant

- GIVEN an unauthenticated request
- WHEN POST /api/v1/auth/users
- THEN 401 Unauthorized

#### R4.2: List Users

`GET /api/v1/auth/users` (Auth required). Returns only users for the authenticated tenant.

- GIVEN an authenticated Customer (`tenantId=1`) with 3 users
- WHEN GET /api/v1/auth/users
- THEN 200 + only users with `customerId=1`

- GIVEN an authenticated Customer (`tenantId=2`) with no users
- WHEN GET /api/v1/auth/users
- THEN 200 + empty list

#### R4.3: Activate/Deactivate User

`PATCH /api/v1/auth/users/{id}/status` (Auth required: Customer or user with `user:manage` permission).

- GIVEN an authenticated Customer with target user under their tenant
- WHEN PATCH with `{ isActive: false }`
- THEN 200 + user deactivated (cannot login)

- GIVEN a JWT with `principalType=user` and NO `user:manage` permission
- WHEN PATCH /api/v1/auth/users/{id}/status
- THEN 403 Forbidden

#### R4.4: Delete User

`DELETE /api/v1/auth/users/{id}` (Auth required: Customer only).

- GIVEN an authenticated Customer with target user under their tenant
- WHEN DELETE /api/v1/auth/users/{id}
- THEN 204 No Content

- GIVEN a JWT with `principalType=user`
- WHEN DELETE /api/v1/auth/users/{id}
- THEN 403 Forbidden — solo el dueño elimina usuarios

#### R4.5: Password Change

`PUT /api/v1/auth/users/{id}/password` (Auth required: same user, or Customer).

- GIVEN an authenticated User changing their own password
- WHEN PUT with `{ currentPassword, newPassword }`
- THEN 200 + password updated (BCrypt)

- GIVEN an authenticated Customer changing any user's password in their tenant
- WHEN PUT /api/v1/auth/users/{id}/password with `{ newPassword }`
- THEN 200 + password updated

### R5: Permission Management

All permission endpoints require authentication. Customer (dueño) or users with `permission:manage` MAY manage the catalogue and user assignments.

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/auth/permissions | List catalogue |
| POST | /api/v1/auth/permissions | Create permission code |
| PUT | /api/v1/auth/permissions/{id} | Update permission code |
| DELETE | /api/v1/auth/permissions/{id} | Remove permission code |
| GET | /api/v1/auth/users/{id}/permissions | List user permissions |
| POST | /api/v1/auth/users/{id}/permissions | Assign permission(s) |
| DELETE | /api/v1/auth/users/{id}/permissions/{permId} | Revoke permission |
| GET | /api/v1/auth/users/{id}/permissions/verify?code=X | Verify permission |

#### Scenario: List permissions catalogue

- GIVEN available permissions in the catalogue
- WHEN GET /api/v1/auth/permissions
- THEN 200 + list of all `Permission` entries

#### Scenario: Create permission code

- GIVEN authenticated user with `permission:manage`
- WHEN POST /api/v1/auth/permissions with `{ code, description }`
- THEN 201 + permission created

#### Scenario: Assign permission to user

- GIVEN an authenticated Customer with a target user under their tenant
- WHEN POST /api/v1/auth/users/{id}/permissions with `{ permissionId }`
- THEN 200 + UserPermission created

#### Scenario: Revoke permission from user

- GIVEN an existing UserPermission for user X and permission Y
- WHEN DELETE /api/v1/auth/users/{id}/permissions/{permId}
- THEN 204 No Content

#### Scenario: Verify user has permission

- GIVEN user with INVENTORY_READ
- WHEN GET /api/v1/auth/users/{id}/permissions/verify?code=INVENTORY_READ
- THEN 200 + `{ hasPermission: true }`

#### Scenario: Cross-tenant assign rejected

- GIVEN assigner tenant A, target tenant B
- WHEN POST assign
- THEN 403 Forbidden

### R6: Verification Token Expiry

Verification tokens MUST expire 24 hours after creation.

#### Scenario: Token expiry enforcement

- GIVEN a verification token created 25 hours ago
- WHEN GET /api/v1/auth/verify?token=old-token
- THEN 410 Gone

### R7: Backward Compatibility

Existing unprotected endpoints MUST remain functional after adding SecurityConfig.

#### Scenario: Public endpoint accessible without auth

- GIVEN a publicly accessible endpoint (e.g., health check)
- WHEN an unauthenticated GET request is made
- THEN 200 OK — no auth required

### R8: Plan-Based Limits

The number of active users per tenant MUST NOT exceed the limit defined by `Customer.planId`.

#### Scenario: User creation blocked by plan limit

- GIVEN a Customer with planId=1 (max 3 users) and already 3 active users
- WHEN POST /api/v1/auth/users
- THEN 402 Payment Required — plan limit reached

*Note: Full plan enforcement requires billing service. Initial implementation: allow unlimited users (limit check deferred).*

### R9: EMPLOYEE Role

The system MUST accept `EMPLOYEE` as a valid UserRole in the domain model and entity enum. EMPLOYEE users MUST NOT receive default RolePermission assignments — all permissions come via UserPermission.

#### Scenario: Create EMPLOYEE user

- GIVEN authenticated Customer (dueño)
- WHEN POST /api/v1/auth/users with `{ role: EMPLOYEE }`
- THEN 201 + User created with role=EMPLOYEE, no default permissions

### R10: Permission Codes in Auth Response

The system SHOULD include the user's effective permission codes in the login response payload.

#### Scenario: Login returns permissions

- GIVEN authenticated User with INVENTORY_READ
- WHEN POST /api/v1/auth/login
- THEN 200 + response includes `{ permissions: ["INVENTORY_READ"] }`

# Delta for customer-auth

## ADDED Requirements

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

## MODIFIED Requirements

### R4.1: Create User

`POST /api/v1/auth/users` (Auth required: Customer or user with `user:create` permission). The system MUST accept `EMPLOYEE` as a valid role. EMPLOYEE users MUST NOT receive default role permissions — only granular UserPermission assignments.

(Previously: Only ADMINISTRATOR, CASHIER, OPERATOR roles supported. All roles received RolePermission defaults.)

#### Scenario: Create EMPLOYEE (new)

- GIVEN authenticated Customer (`tenantId=1`)
- WHEN POST /api/v1/auth/users with `{ email, password, firstName, role: EMPLOYEE }`
- THEN 201 + User created with `customerId=1`, role=EMPLOYEE, no default role permissions

#### Scenario: Create CASHIER (unchanged)

- GIVEN authenticated Customer (`tenantId=1`)
- WHEN POST /api/v1/auth/users with `{ email, password, firstName, role: CASHIER }`
- THEN 201 + User created, role default permissions assigned via RolePermission

#### Scenario: Max 1 admin (unchanged)

- GIVEN Customer with existing ADMINISTRATOR
- WHEN POST with role=ADMINISTRATOR
- THEN 409 Conflict

#### Scenario: Unauthenticated (unchanged)

- GIVEN no auth header
- WHEN POST /api/v1/auth/users
- THEN 401 Unauthorized

### R5: Permission Management

All permission endpoints require authentication. Customer (dueño) or users with `permission:manage` MAY manage the catalogue and user assignments. New endpoints added for catalogue CRUD and permission verification.

(Previously: Only list, assign, and revoke were defined. No catalogue CRUD or verify endpoint.)

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/v1/auth/permissions | List catalogue (unchanged) |
| POST | /api/v1/auth/permissions | Create permission code |
| PUT | /api/v1/auth/permissions/{id} | Update permission code |
| DELETE | /api/v1/auth/permissions/{id} | Remove permission code |
| GET | /api/v1/auth/users/{id}/permissions | List effective (unchanged) |
| POST | /api/v1/auth/users/{id}/permissions | Assign permission(s) (unchanged) |
| DELETE | /api/v1/auth/users/{id}/permissions/{permId} | Revoke (unchanged) |
| GET | /api/v1/auth/users/{id}/permissions/verify?code=X | Verify permission |

#### Scenario: List catalogue (unchanged)

- GIVEN available permissions
- WHEN GET /api/v1/auth/permissions
- THEN 200 + all Permission entries

#### Scenario: Create permission code

- GIVEN authenticated user with `permission:manage`
- WHEN POST /api/v1/auth/permissions with `{ code, description }`
- THEN 201 + permission created

#### Scenario: Assign permission (unchanged)

- GIVEN authenticated Customer, target user under same tenant
- WHEN POST /api/v1/auth/users/{id}/permissions
- THEN 200 + UserPermission created

#### Scenario: Revoke permission (unchanged)

- GIVEN existing UserPermission
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

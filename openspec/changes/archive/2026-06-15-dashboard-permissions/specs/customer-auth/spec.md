# Delta for customer-auth

## MODIFIED Requirements

### Requirement: R3: Login

`POST /api/v1/auth/login`. Authenticates Customer (tried first) or User. Returns JWT with `principalType` and granular permissions.
(Previously: JWT only contained `tenantId`, `principalType`, `rol`, and `sub`)

#### Scenario: Customer login success

- GIVEN an active Customer with valid credentials
- WHEN POST /api/v1/auth/login
- THEN 200 + JWT with `tenantId=customer.id`, `principalType=customer`, `rol=customer`
- AND JWT contains `permissions: ["*"]` (Customer has all permissions)

#### Scenario: User login success

- GIVEN an active User (customerId=1) with valid credentials
- AND user has effective permissions `["INVENTORY_READ", "SALES_CREATE"]`
- WHEN POST /api/v1/auth/login
- THEN 200 + JWT with `tenantId=1`, `principalType=user`, `rol=ADMINISTRATOR|CASHIER|OPERATOR|EMPLOYEE`
- AND JWT contains `permissions: ["INVENTORY_READ", "SALES_CREATE"]`

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

### Requirement: R10: Permission Codes in Auth Response

The system MUST include the user's effective permission codes and a structured `UserSession` in the login response payload.
(Previously: SHOULD include permissions, was less structured)

#### Scenario: Login returns permissions and session

- GIVEN authenticated User with INVENTORY_READ
- WHEN POST /api/v1/auth/login
- THEN 200 + response includes a `user` object with `id`, `email`, `rol`, and `permissions`
- AND `permissions` contains `["INVENTORY_READ"]`

#### Scenario: UserSession construction

- GIVEN a valid JWT with permissions
- WHEN the frontend builds the `UserSession` object
- THEN the `UserSession.permissions` array MUST match the JWT `permissions` claim
- AND the `rol` field is retained for backward compatibility but SHOULD NOT be used for access logic

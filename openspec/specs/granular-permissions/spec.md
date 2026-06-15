# Granular Permissions Specification

## Purpose

Permission code catalogue, user-permission assignment, and verification for SIGA. Tenant owners assign fine-grained permissions per employee beyond role defaults.

## Requirements

### R1: Permission Catalogue

The system MUST seed a catalogue of permission codes via Flyway V4 and enforce uniqueness.

| Code | Description |
|------|-------------|
| INVENTORY_READ | View inventory |
| INVENTORY_WRITE | Add/edit products |
| INVENTORY_DELETE | Delete products |
| KIOSK_ADMIN | Manage kiosks |
| DELIVERY_VIEW | View delivery routes |
| REPORTS_VIEW | View reports |
| SALES_CREATE | Create sales |
| SALES_READ | View sales |

#### Scenario: Seed on migration

- GIVEN a fresh database
- WHEN Flyway V4 runs
- THEN permissions table contains all 8 codes above

#### Scenario: Duplicate code rejected

- GIVEN existing code "INVENTORY_READ"
- WHEN inserting duplicate
- THEN unique constraint violation

### R2: Assign Permissions to User

The system MUST allow tenant-scoped permission assignment. Assigner MUST belong to the same tenant as the target user.

#### Scenario: Single permission assignment

- GIVEN authenticated user with `permission:manage` access
- AND target user under same tenant
- WHEN assigning INVENTORY_READ
- THEN 200 + UserPermission created

#### Scenario: Multi-permission (polyfunctional employee)

- GIVEN Héctor is User with role EMPLOYEE under Elizabeth's tenant
- WHEN Elizabeth assigns INVENTORY_READ, INVENTORY_WRITE, KIOSK_ADMIN
- THEN Héctor can view stock, add products, manage kiosks
- AND cannot delete products (lacks INVENTORY_DELETE)

#### Scenario: Cross-tenant rejected

- GIVEN assigner from tenant A, target from tenant B
- WHEN POST assign
- THEN 403 Forbidden

### R3: Revoke Permission

The system MUST allow revoking a user's permission.

#### Scenario: Revoke existing

- GIVEN existing UserPermission for user X, permission Y
- WHEN DELETE revoke
- THEN 204 No Content, permission removed

### R4: Verify Permission

The system MUST check whether a user has a specific permission (effective = role defaults ∪ user assignments).

#### Scenario: User has permission

- GIVEN user with INVENTORY_READ assigned
- WHEN verify INVENTORY_READ
- THEN `{ hasPermission: true }`

#### Scenario: User lacks permission (minimum for delivery)

- GIVEN Luis (EMPLOYEE) with INVENTORY_READ, DELIVERY_VIEW
- WHEN verify INVENTORY_WRITE
- THEN `{ hasPermission: false }`

### R5: List Effective Permissions

The system MUST return all effective permissions for a user.

#### Scenario: List with role defaults + overrides

- GIVEN CASHIER (default SALES_CREATE) + user-specific INVENTORY_READ
- WHEN list permissions
- THEN response includes SALES_CREATE and INVENTORY_READ

### R6: Minimum Permission Recipients

The system MUST allow creating users with zero default permissions (role=EMPLOYEE). All permissions are granted via UserPermission.

#### Scenario: Employee with no defaults

- GIVEN Elizabeth creates Luis with role EMPLOYEE
- WHEN Luis logs in before any assignment
- THEN Luis has zero effective permissions

### R7: Frontend Navigation Visibility

The Dashboard Sidebar MUST toggle visibility of links based on the presence of required permissions in the `UserSession`.

#### Scenario: Sidebar link visible

- GIVEN a UserSession with permission `INVENTORY_READ`
- WHEN the Sidebar renders
- THEN the "Inventario" link MUST be visible

#### Scenario: Sidebar link hidden

- GIVEN a UserSession WITHOUT permission `REPORTS_VIEW`
- WHEN the Sidebar renders
- THEN the "Reportes" link MUST NOT be visible

#### Scenario: Customer (Dueño) sees everything

- GIVEN a UserSession with permission `"*"` (Customer)
- WHEN the Sidebar renders
- THEN all navigation links MUST be visible

### R8: Frontend Route Access Control

The Dashboard MUST enforce server-side and client-side route guards based on granular permissions.

#### Scenario: Route access granted

- GIVEN a user navigating to `/users`
- AND the `UserSession` contains `user:manage`
- WHEN the route guard executes
- THEN the request MUST be allowed to proceed

#### Scenario: Route access denied (Forbidden)

- GIVEN a user navigating to `/inventory`
- AND the `UserSession` lacks `INVENTORY_READ`
- WHEN the route guard executes
- THEN the system MUST return a 403 Forbidden status
- OR redirect to a "Not Authorized" page

#### Scenario: Route guard replaces Role check

- GIVEN an existing route guarded by `role=ADMINISTRATOR`
- WHEN the system is updated to PBAC
- THEN the route guard MUST check for the corresponding permission (e.g., `user:manage`) instead of the role string

## Deferred Requirements

### Agent Permission Inheritance (→ US-4.x)

Agent inherits the requesting user's effective permissions. No implementation in this change.

#### Scenario: Agent inherits user permissions

- GIVEN Héctor has INVENTORY_WRITE
- WHEN agent acts "add 50 napkins to kiosk North" on Héctor's behalf
- THEN agent executes because Héctor has the permission
- AND agent logs action as Héctor's delegate

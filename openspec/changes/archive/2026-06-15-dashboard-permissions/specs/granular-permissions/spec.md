# Delta for granular-permissions

## ADDED Requirements

### Requirement: R7: Frontend Navigation Visibility

The Dashboard Sidebar MUST toggle visibility of links based on the presence of required permissions in the `UserSession`.
(Note: This requirement extends the domain to frontend consumption).

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

### Requirement: R8: Frontend Route Access Control

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

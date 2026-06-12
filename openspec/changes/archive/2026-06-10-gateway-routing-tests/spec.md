# Delta Spec: Gateway Routing Integration Tests

## Goal
Establish a robust integration testing framework for the `siga-gateway` service using `WebTestClient` and `WireMock`. The primary focus is validating that all public API requests are correctly routed to their respective downstream microservices with appropriate path rewrites.

## Requirements

### 1. Test Environment Setup
- Use `@SpringBootTest` with a random port.
- Configure `WireMock` to act as the downstream microservice for all routed requests.
- Mock Eureka service discovery to resolve `lb://` URIs to the local WireMock instance.

### 2. Route Validation Matrix
The following table defines the expected routing and rewrite behavior that MUST be verified:

| Source Path | Target Service | Expected Rewritten Path | Method |
|-------------|----------------|--------------------------|--------|
| `/api/agent/status` | `siga-agent` | `/api/agent/status` | GET |
| `/api/auth/login` | `siga-auth` | `/api/v1/auth/login` | POST |
| `/api/products/123` | `siga-inventory` | `/api/v1/products/123` | GET |
| `/api/stores/S01` | `siga-inventory` | `/api/v1/stores/S01` | GET |
| `/api/inventory/stock`| `siga-inventory` | `/api/v1/inventory/stock`| GET |
| `/api/sales/order` | `siga-sales` | `/api/v1/sales/order` | POST |
| `/api/cash-shifts/open`| `siga-sales` | `/api/v1/cash-shifts/open`| POST |
| `/api/billing/invoice` | `siga-billing` | `/api/v1/billing/invoice` | GET |
| `/api/comercial/data` | `siga-billing` | `/api/v1/billing/data` | GET |

### 3. Verification Criteria
- **Status Code Match**: Gateway must return the same HTTP status code provided by the WireMock stub (e.g., 200 OK, 201 Created, 404 Not Found).
- **Body Integrity**: Gateway must transparently forward the response body from the downstream service.
- **Header Forwarding**: Basic headers (Content-Type, etc.) should be preserved.
- **Standardization**: Ensure `siga-agent` uses `lb://siga-agent` in `GatewayApplication.kt` (currently using hardcoded IP).

## Test Scenarios

### Scenario 1: AI Agent Routing
- **Request**: `GET /api/agent/ping`
- **Stub**: `lb://siga-agent` returns 200 OK with body `{"status": "pong"}` at path `/api/agent/ping`.
- **Assertion**: `WebTestClient` receives 200 OK and body `{"status": "pong"}`.

### Scenario 2: Auth Rewrite
- **Request**: `POST /api/auth/login`
- **Stub**: `lb://siga-auth` expects `POST /api/v1/auth/login`. Returns 200 OK.
- **Assertion**: Gateway successfully routes and rewrites; `WebTestClient` receives 200 OK.

### Scenario 3: Inventory Multi-Path Rewrite
- **Request**: `GET /api/products/P001`
- **Stub**: `lb://siga-inventory` expects `GET /api/v1/products/P001`. Returns 200 OK.
- **Assertion**: Gateway successfully routes and rewrites.
- **Request**: `GET /api/stores/ST01`
- **Stub**: `lb://siga-inventory` expects `GET /api/v1/stores/ST01`.
- **Assertion**: Gateway successfully routes and rewrites.

### Scenario 4: Sales and Cash-Shifts
- **Request**: `GET /api/sales/history`
- **Stub**: `lb://siga-sales` expects `GET /api/v1/sales/history`.
- **Assertion**: Correct rewrite and routing.
- **Request**: `POST /api/cash-shifts/close`
- **Stub**: `lb://siga-sales` expects `POST /api/v1/cash-shifts/close`.
- **Assertion**: Correct rewrite and routing.

### Scenario 5: Billing and Comercial (Cross-Rewrite)
- **Request**: `GET /api/billing/reports`
- **Stub**: `lb://siga-billing` expects `GET /api/v1/billing/reports`.
- **Assertion**: Correct rewrite and routing.
- **Request**: `GET /api/comercial/info`
- **Stub**: `lb://siga-billing` expects `GET /api/v1/billing/info`.
- **Assertion**: `/api/comercial/**` is correctly mapped to `/api/v1/billing/**` in `siga-billing` service.

## Non-Goals
- Testing security/JWT validation (unless specifically added to gateway filters later).
- Testing microservice internal logic.
- Testing load balancing algorithms.

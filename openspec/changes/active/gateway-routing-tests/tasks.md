# Tasks: Gateway Routing Integration Tests

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 150 - 200 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | single-pr |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | Infrastructure, Standardization, and Integration Tests | PR 1 | Base branch: migracion-microservicios |

## Phase 1: Foundation & Standardization

- [x] 1.1 Add WireMock and Spring Cloud Contract dependencies to `services/gateway/build.gradle.kts`.
- [x] 1.2 Update `siga-agent` route in `services/gateway/src/main/kotlin/com/siga/gateway/GatewayApplication.kt` to use `lb://siga-agent`.
- [x] 1.3 Verify that `application.yml` is consistent with the Kotlin bean configuration.

## Phase 2: Test Infrastructure Setup

- [x] 2.1 Create `services/gateway/src/test/kotlin/com/siga/gateway/RoutingIntegrationTests.kt`.
- [x] 2.2 Configure `@SpringBootTest` with random port and `@AutoConfigureWireMock`.
- [x] 2.3 Implement `@TestConfiguration` to mock `ReactiveDiscoveryClient` for service resolution.

## Phase 3: Route Implementation Tests

- [x] 3.1 Implement Scenario 1: AI Agent Routing (direct path).
- [x] 3.2 Implement Scenario 2: Auth Rewrite (`/api/auth/**` -> `/api/v1/auth/**`).
- [x] 3.3 Implement Scenario 3: Inventory Multi-Path Rewrite (`/api/products/**`, `/api/stores/**`, `/api/inventory/**` -> `/api/v1/...`).
- [x] 3.4 Implement Scenario 4: Sales and Cash-Shifts (`/api/sales/**`, `/api/cash-shifts/**` -> `/api/v1/...`).
- [x] 3.5 Implement Scenario 5: Billing and Comercial Cross-Rewrite (`/api/billing/**`, `/api/comercial/**` -> `/api/v1/billing/...`).

## Phase 4: Verification & Coverage

- [x] 4.1 Run `./gradlew :services:gateway:test` to verify all scenarios.
- [x] 4.2 Verify response body integrity and header forwarding in at least one scenario.
- [x] 4.3 Check JaCoCo coverage report to ensure 85%+ coverage of routing logic.

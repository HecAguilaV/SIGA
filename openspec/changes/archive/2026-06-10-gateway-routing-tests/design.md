# Design: Gateway Routing Integration Tests

## Technical Approach

The goal is to implement a robust integration test suite for `siga-gateway` using `WebTestClient` and `WireMock`. The strategy involves:
1.  **Environment Setup**: Using `@SpringBootTest` with a random port to start the full application context.
2.  **Service Mocking**: Employing `@AutoConfigureWireMock` to simulate downstream microservices.
3.  **Discovery Mocking**: Providing a mock implementation of `DiscoveryClient` or `ReactiveDiscoveryClient` to intercept `lb://` URI resolution and point them to the WireMock server.
4.  **Route Verification**: Defining WireMock stubs for each expected downstream path and asserting that requests to the Gateway are correctly forwarded and rewritten.

This maps directly to the proposal's requirement of achieving high coverage and validating all 5 core routes.

## Architecture Decisions

### Decision: Test Tooling Choice

**Choice**: Use `WebTestClient` + `WireMock` + `Spring Cloud Contract Stub Runner`.
**Alternatives considered**: `TestRestTemplate`, manual WireMock management.
**Rationale**: `WebTestClient` is designed for reactive WebFlux applications. `Stub Runner` provides an easy way to mock service discovery without needing a real Eureka instance.

### Decision: Discovery Client Mocking

**Choice**: Register a `@TestConfiguration` that provides a mock `ReactiveDiscoveryClient`.
**Alternatives considered**: Using `Spring Cloud Contract Stub Runner` Eureka integration.
**Rationale**: A manual mock provides more granular control over exactly which service IDs are "discovered" and where they point during tests, reducing complexity compared to configuring a full stub runner for simple routing tests.

### Decision: Standardizing siga-agent route

**Choice**: Change `GatewayApplication.kt` to use `lb://siga-agent`.
**Alternatives considered**: Keeping hardcoded IP, using a property for the IP.
**Rationale**: Consistency with other routes and alignment with best practices for service discovery in microservices architectures.

## Data Flow

The data flow during a test execution is as follows:

    WebTestClient ───(HTTP Request)───→ siga-gateway (SUT)
                                            │
                                            ├─(Route Lookup)
                                            ├─(Path Rewrite Filter)
                                            └─(Load Balancer / Mock Discovery)
                                                    │
    WireMock Server ←──(Forwarded Request)──────────┘
          │
          └─(Match Stub)
          └─(Return Mock Response) ──────────→ siga-gateway
                                                    │
    WebTestClient ←──────(Final Response)───────────┘

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `services/gateway/build.gradle.kts` | Modify | Add `spring-cloud-contract-wiremock` and `spring-cloud-starter-contract-stub-runner` dependencies. |
| `services/gateway/src/main/kotlin/com/siga/gateway/GatewayApplication.kt` | Modify | Update `siga-agent` route to use `lb://siga-agent`. |
| `services/gateway/src/test/kotlin/com/siga/gateway/RoutingIntegrationTests.kt` | Create | New integration test suite covering all routes. |

## Interfaces / Contracts

No new public API interfaces. The change affects internal routing configuration.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Integration | Route Matching | Verify Gateway matches paths like `/api/auth/**` to correct service IDs. |
| Integration | Path Rewriting | Verify Gateway transforms `/api/auth/login` to `/api/v1/auth/login`. |
| Integration | Multi-path handling | Verify multiple predicates for `siga-inventory` and `siga-sales`. |

## Threat Model

| Threat | Impact | Mitigation |
|--------|--------|------------|
| Route Misconfiguration | Exposure of unintended internal endpoints. | Explicit tests for path rewrites ensure prefix stripping/adding is correct. |
| Service Discovery hijacking | Requests routed to malicious services. | Tests verify that `lb://` resolution logic is correctly integrated with the internal load balancer. |

## Migration / Rollout

No data migration required. This is a technical alignment and testing enhancement.

## Open Questions

- [ ] Should we also verify Header propagation (e.g. `X-Forwarded-For`) in these tests?
- [ ] Is there any custom filter logic (auth/logging) that should be tested alongside routing?

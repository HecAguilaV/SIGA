# SDD Proposal: Gateway Routing Integration Tests

## Executive Summary
This proposal aims to implement a comprehensive integration test suite for the `siga-gateway` service. Currently, the gateway lacks routing validation, and there is a discrepancy between the Kotlin bean configuration and the YAML configuration regarding the `siga-agent` route. We will implement tests using `WebTestClient` and `WireMock` to ensure all 5 core routes are correctly rewritten and forwarded to their respective destinations.

## Goals
- Achieve **85%+ code coverage** of `GatewayApplication.kt` logic.
- Validate **Path Predicates** and **RewritePath Filters** for all 5 routes:
    - `siga-agent`
    - `siga-auth`
    - `siga-inventory`
    - `siga-sales`
    - `siga-billing`
- Standardize the `siga-agent` route to use service discovery (`lb://siga-agent`) instead of a hardcoded IP.
- Ensure tests are resilient by mocking service discovery (Eureka) using WireMock or Spring Cloud Contract Stub Runner.

## Scope
- **Affected Files:**
    - `services/gateway/build.gradle.kts`: Add WireMock and Spring Cloud Contract dependencies.
    - `services/gateway/src/main/kotlin/com/siga/gateway/GatewayApplication.kt`: Standardize `siga-agent` route.
    - `services/gateway/src/main/resources/application.yml`: Sync with Kotlin bean configuration.
    - `services/gateway/src/test/kotlin/com/siga/gateway/RoutingIntegrationTests.kt`: New test suite.
- **Out of Scope:**
    - Testing actual microservice logic (only routing and forwarding).
    - Performance testing.

## Technical Approach

### 1. Dependency Updates
Add the following to `services/gateway/build.gradle.kts`:
- `testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")`
- `testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock")`

### 2. Configuration Standardization
Update `GatewayApplication.kt` to change `.uri("http://192.168.1.10:8000")` to `.uri("lb://siga-agent")` for the `siga-agent` route, ensuring consistency with `application.yml` and proper service discovery usage.

### 3. Test Implementation Strategy
- Use `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` with `@AutoConfigureWireMock(port = 0)`.
- Mock Eureka registration/discovery to return `localhost:${wiremock.port}` for all service IDs.
- For each route:
    - Define a WireMock stub for the target path.
    - Use `WebTestClient` to send a request to the Gateway.
    - Assert the response matches the stub.
    - Verify that the path was rewritten correctly using WireMock's `verify` or by checking the stubbed request.

### 4. Handling Reactive Gateway
Since Spring Cloud Gateway is built on WebFlux, all tests will use the non-blocking `WebTestClient`.

## Verification Plan
- **Automated Tests:** Run `./gradlew :services:gateway:test` to verify all routing scenarios.
- **Coverage Report:** Generate JaCoCo reports to confirm 85%+ coverage of the route locator bean.

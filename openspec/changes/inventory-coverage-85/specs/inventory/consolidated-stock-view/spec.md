# Delta for Consolidated Stock View

## ADDED Requirements

### Requirement: Infrastructure Test Stability

The system MUST ensure that all integration tests using Testcontainers initialize reliably across all supported environments (Local Docker, CI), specifically avoiding `IllegalStateException` during Docker strategy discovery.

#### Scenario: Testcontainers initialization success

- GIVEN a standard Docker environment (Local or CI)
- WHEN the `ConsolidatedStockCacheTest` (or any Testcontainers-based test) starts
- THEN the Docker client strategy MUST be discovered successfully
- AND the required containers MUST start and reach a READY state within 30 seconds.

### Requirement: Verifiable Test Coverage

The system MUST maintain a minimum of 85% instruction and branch coverage for the `siga-inventory` service, verifiable via JaCoCo reports.

#### Scenario: Coverage threshold enforcement

- GIVEN the `siga-inventory` build pipeline
- WHEN the `check` or `test` task is executed
- THEN the JaCoCo report MUST be generated
- AND the build MUST fail if coverage is below 85%.

## Security Requirements

### Requirement: Secure Test Infrastructure

The system MUST ensure that Testcontainers and other testing infrastructure do not expose sensitive data or leave insecure configurations in the host environment.

#### Scenario: Secret scanning in test configurations

- GIVEN a test configuration file (e.g., `application-test.yml`)
- WHEN the `gitleaks` or `trivy` scan is executed
- THEN no hardcoded secrets or sensitive API keys MUST be detected.

#### Scenario: Container lifecycle management

- GIVEN a test run using Testcontainers
- WHEN the test suite completes (successfully or failed)
- THEN all started containers MUST be stopped and removed automatically (Ryuk sidecar MUST be active).

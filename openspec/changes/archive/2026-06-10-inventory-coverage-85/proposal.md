# Proposal: Inventory Coverage 85%+ & Test Stabilization

## Intent
The primary goal is to stabilize the integration testing suite for the `siga-inventory` service. While nominal coverage is currently 88%, the suite is unstable due to Testcontainers initialization failures (`IllegalStateException` at `DockerClientProviderStrategy.java:274`). Fixing these infrastructure issues will ensure that the 85% coverage goal is reliable and verifiable.

## Scope

### In Scope
- Resolve `IllegalStateException` during Testcontainers initialization in `ConsolidatedStockCacheTest`.
- Stabilize all inventory integration tests.
- Maintain and audit 85%+ JaCoCo coverage for the inventory service.
- Standardize Docker environment configuration for local testing.

### Out of Scope
- Refactoring core business logic.
- Performance optimization of the stock cache mechanism.
- Testing of non-inventory services.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `inventory`: Stabilization of integration tests and verification of existing requirements.

## Approach
Identify the root cause of the Testcontainers failure (likely permissions or Docker socket access in the current environment). Standardize the `BaseIntegrationTest` (if any) or shared testing configuration to ensure a consistent Docker strategy. Update JaCoCo configuration if necessary to exclude non-testable boilerplate.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/inventory/src/test/kotlin` | Modified | Stabilize existing tests and infrastructure. |
| `services/inventory/build.gradle.kts` | Modified | JaCoCo/Test configuration updates. |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Docker environment incompatibility | Medium | Use conditional test execution or better discovery strategies. |
| Coverage drop during stabilization | Low | Monitor JaCoCo reports closely after each fix. |

## Rollback Plan
Revert changes to `build.gradle.kts` and test files using Git.

## Dependencies
- Local Docker Engine (already confirmed running).

## Success Criteria
- [ ] `ConsolidatedStockCacheTest` passes consistently.
- [ ] The full inventory test suite (237+ tests) passes.
- [ ] JaCoCo coverage report confirms >85% for the inventory service.

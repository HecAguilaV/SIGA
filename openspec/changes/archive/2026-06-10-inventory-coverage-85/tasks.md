# Tasks: Inventory Coverage 85%+ & Test Stabilization

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 50 - 100 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | single PR |
| Delivery strategy | single-pr |
| Chain strategy | pending |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

## Phase 1: Infrastructure & Environment Fixes

- [x] 1.1 Update `services/inventory/build.gradle.kts` with `TESTCONTAINERS_RYUK_DISABLED=true` environment variable for tests.
- [x] 1.2 Add `docker` host detection properties to `build.gradle.kts` if needed.
- [x] 1.3 Refactor `services/inventory/src/test/kotlin/com/siga/inventory/BaseIntegrationTest.kt` to include a singleton Redis container.

## Phase 2: Test Stabilization

- [x] 2.1 Modify `services/inventory/src/test/kotlin/com/siga/inventory/integration/ConsolidatedStockCacheTest.kt` to inherit from `BaseIntegrationTest`.
- [x] 2.2 Verify `ConsolidatedStockCacheTest` passes individually.
- [x] 2.3 Run full test suite for `inventory` service.

## Phase 3: Coverage Verification

- [x] 3.1 Execute `./gradlew :services:inventory:jacocoTestReport`.
- [x] 3.2 Audit the JaCoCo report to ensure 85%+ coverage.
- [x] 3.3 Add missing tests for any critical areas identified during the audit (if coverage drops below 85%).

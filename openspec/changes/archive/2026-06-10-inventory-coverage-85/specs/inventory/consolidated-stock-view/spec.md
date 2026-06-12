# Delta for Inventory / Consolidated Stock View

## ADDED Requirements

### Requirement: Test Infrastructure Stability
The integration testing infrastructure MUST be compatible with the local Docker Engine environment. Testcontainers-based tests SHALL initialize correctly without `IllegalStateException`.

#### Scenario: Testcontainers Initialization
- GIVEN a local Docker Engine running
- WHEN running `ConsolidatedStockCacheTest`
- THEN the Redis container MUST start successfully
- AND the test suite MUST be able to connect to the dynamic Redis port.

### Requirement: Minimum Coverage Enforcement
The `siga-inventory` service MUST maintain a minimum of 85% total line coverage as reported by JaCoCo.

#### Scenario: Coverage Verification
- GIVEN all implementation changes are complete
- WHEN running `./gradlew :services:inventory:jacocoTestReport`
- THEN the total coverage percentage MUST be >= 85%.

## MODIFIED Requirements

### Requirement: Consolidated Stock Caching
(Modified to ensure cache hit/miss behavior is verified at integration level)
(Previously: The system SHOULD cache consolidated stock views to improve performance.)

#### Scenario: Cache Hit Consistency
- GIVEN a previous successful request for consolidated stock
- WHEN the same request (same storeId, page, size) is repeated within the TTL window
- THEN the result MUST be returned from cache
- AND the underlying database ports MUST NOT be called.

#### Scenario: Cache Miss on Parameter Change
- GIVEN a previous successful request for consolidated stock
- WHEN a new request is made with a different `storeId` or pagination parameters
- THEN the result MUST NOT be returned from cache
- AND the underlying database ports MUST be called to fetch fresh data.

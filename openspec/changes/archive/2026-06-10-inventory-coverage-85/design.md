# Design: Inventory Coverage 85%+ & Test Stabilization

## Technical Approach
The overall strategy focuses on stabilizing the Testcontainers-based integration tests and ensuring JaCoCo accurately reports coverage above 85%. We will address the `IllegalStateException` by standardizing the Docker environment configuration and improving the `BaseIntegrationTest` lifecycle.

## Architecture Decisions

### Decision: Testcontainers Initialization Strategy
**Choice**: Use a shared `BaseIntegrationTest` with a static singleton Redis container instead of `@Testcontainers` / `@Container` on each class.
**Alternatives considered**: Keep current `@Container` per class (unstable in multi-module builds); Disable Ryuk (last resort).
**Rationale**: Singleton containers are faster and more reliable in multi-test runs, reducing initialization overhead and potential socket conflicts.

### Decision: Environment Variable Injection
**Choice**: Set `TESTCONTAINERS_RYUK_DISABLED=true` and `DOCKER_HOST` defaults in `build.gradle.kts` test task.
**Alternatives considered**: Manual export by user (fragile); Shell script wrapper.
**Rationale**: Integrating environment defaults directly into Gradle ensures that `./gradlew test` works out-of-the-box regardless of the shell configuration.

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `services/inventory/src/test/kotlin/com/siga/inventory/BaseIntegrationTest.kt` | Modify | Add singleton Redis container and dynamic property source. |
| `services/inventory/src/test/kotlin/com/siga/inventory/integration/ConsolidatedStockCacheTest.kt` | Modify | Inherit from `BaseIntegrationTest` and remove redundant container setup. |
| `services/inventory/build.gradle.kts` | Modify | Configure test environment variables and ensure JaCoCo exclusion rules are optimized. |

## Interfaces / Contracts
No new business interfaces. The contract for testing is defined by the `BaseIntegrationTest` abstraction.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Integration | Redis Caching | Use `BaseIntegrationTest` with real Redis container. |
| Integration | Database Mappings | Use H2 (current) or Testcontainers Postgres (if needed for PGVector). |
| Verification | Coverage | Run JaCoCo report after all tests pass. |

## Migration / Rollout
No production migration required. This change only affects the test suite.

## Open Questions
- [ ] Do we need Testcontainers for Postgres as well to test PGVector specific queries? (H2 might not support them).
- [ ] Should we enforce a coverage check at the build level (failure if < 85%)?

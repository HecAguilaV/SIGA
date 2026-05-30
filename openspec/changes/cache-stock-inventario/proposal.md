# Proposal: Cache-Stock-Inventario

## Intent

Add Redis caching to `ConsolidatedStockUseCase` to meet the <300ms response time (spec requirement #4) and stop loading ALL stock entries into memory on every request. Currently `stockPort.findAll()` pulls every row, then groups/filters/paginates in-memory — does not scale beyond a few stores.

## Scope

### In Scope
- Add `spring-boot-starter-data-redis` dependency to `services/inventory/build.gradle.kts`
- Create `CacheConfig` with `@Configuration @EnableCaching`
- Annotate `ConsolidatedStockUseCase.execute()` with `@Cacheable(value = ["consolidatedStock"], key = "#storeId?.toString() ?: 'all' + ':' + #page + ':' + #size")`
- Add Redis connection + 60s TTL to `application.yml`
- Add `siga-redis` container to `docker-compose.yml` (single node, port 6379)
- Write integration test verifying cache hit/miss behavior
- Add `REDIS_PORT` env var to `.env.example` for local dev

### Out of Scope
- Event-based cache invalidation (TTL-only is sufficient for this use case)
- Caching other use cases or query endpoints
- Redis Cluster, Sentinel, Auth, or persistence
- Changes to `StockRepositoryPort` or its JPA adapter
- Redis config on already-deployed environments (ops concern)

## Capabilities

### New Capabilities
None — infrastructure change, no new spec-level behavior.

### Modified Capabilities
None — Requirement #4 (<300ms) already exists in `consolidated-stock-view/spec.md`. Caching is an implementation detail.

## Approach

Add Spring's declarative `@Cacheable` on the use case method. Redis client provided by `spring-boot-starter-data-redis` (Lettuce). 60s TTL via `spring.cache.redis.time-to-live=60s`. Enable caching with `@EnableCaching` on a dedicated `CacheConfig` class. Single-node Redis in docker-compose. Existing MockK-based tests are unaffected because `@Cacheable` only activates within a Spring ApplicationContext.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `services/inventory/build.gradle.kts` | Modified | Add `spring-boot-starter-data-redis` |
| `services/inventory/.../config/CacheConfig.kt` | **New** | `@Configuration @EnableCaching` |
| `services/inventory/.../ConsolidatedStockUseCase.kt` | Modified | `@Cacheable` on `execute()` |
| `services/inventory/.../application.yml` | Modified | Redis host/port + `spring.cache.redis.time-to-live=60s` |
| `docker-compose.yml` | Modified | New `siga-redis` service |
| `.env.example` | Modified | `REDIS_PORT=6379` |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Stale data served after stock mutation | Low | 60s TTL; consolidated view is read-heavy, not real-time critical |
| Redis unavailable causes 500s | Low | Spring Cache throws `CacheException` — add `application.yml` fallback config or `@Primary` no-op cache manager for graceful degradation |
| Existing tests break | Low | All 30+ existing tests use MockK or `@WebMvcTest` without full context — `@Cacheable` annotation is ignored without Spring AOP |

## Rollback Plan

Revert a single commit: remove `spring-boot-starter-data-redis`, delete `CacheConfig.kt`, remove `@Cacheable`, revert `application.yml`, revert `docker-compose.yml`, revert `.env.example`.

## Dependencies

- Docker Desktop / Compose for local Redis
- `spring-boot-starter-data-redis` (managed by Spring Boot BOM 4.0.6)

## Success Criteria

- [ ] Same request within 60s returns cached response without hitting `stockPort.findAll()` (verified via integration test with Redis)
- [ ] Request with different storeId/page/size produces a cache miss (different key)
- [ ] All 30+ existing tests pass
- [ ] `docker compose up` starts Redis, inventory connects successfully

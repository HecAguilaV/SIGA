# Tasks: Cache-Stock-Inventario

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~110–130 |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single commit (well under 400 lines) |
| Delivery strategy | exception-ok |
| Chain strategy | size-exception |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: Low

## Phase 1: Infrastructure

- [x] 1.1 Add `siga-redis` service to `docker-compose.yml` — `redis:7-alpine`, port 6379, `siga-net`, after `siga-kafka` in LAYER 0
- [x] 1.2 Add `implementation("org.springframework.boot:spring-boot-starter-data-redis")` to `services/inventory/build.gradle.kts` dependencies block
- [x] 1.3 Add `spring.cache.redis.time-to-live=60s` + `spring.data.redis.host=${REDIS_HOST:-localhost}` + `spring.data.redis.port=${REDIS_PORT:-6379}` to `services/inventory/src/main/resources/application.yml`
- [x] 1.4 Add `spring.data.redis.port=6379` to `services/inventory/src/test/resources/application-test.yml` (Testcontainers overrides via `@DynamicPropertySource`)

## Phase 2: Core Implementation

- [x] 2.1 Create `services/inventory/src/main/kotlin/com/siga/inventory/config/CacheConfig.kt` — `@Configuration @EnableCaching class CacheConfig` (empty body)
- [x] 2.2 Add `import org.springframework.cache.annotation.Cacheable` and `@Cacheable(cacheNames = ["consolidatedStock"], key = "(#storeId?.toString() ?: 'all') + ':' + #page + ':' + #size")` on `ConsolidatedStockUseCase.execute()`

## Phase 3: Testing

- [x] 3.1 Create `services/inventory/src/test/kotlin/com/siga/inventory/integration/ConsolidatedStockCacheTest.kt` — JUnit 5 `@SpringBootTest` with Redis Testcontainer via `@DynamicPropertySource`; test cache hit (same request twice, verify second call doesn't hit DB), cache miss (different storeId/page/size → fresh call), and TTL expiration (set `spring.cache.redis.time-to-live=1s`, wait, verify fresh call)

## Phase 4: Polish

- [x] 4.1 Add `REDIS_PORT=6379` to `.env.example` under `# Local Infrastructure Defaults` section

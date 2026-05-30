# Design: Cache-Stock-Inventario

## Technical Approach

Add declarative Redis caching to `ConsolidatedStockUseCase.execute()` via Spring's `@Cacheable` annotation. Spring's cache abstraction (AOP proxy) intercepts calls, checks Redis via `RedisCacheManager` (backed by Lettuce), returns cached data on hit or delegates to `execute()` on miss and stores the result. 60s TTL keeps stale data bounded. No changes to the domain layer or port contracts.

## Architecture Decisions

### Decision: `@Cacheable` annotation over manual `RedisTemplate`

| Option | Tradeoff | Decision |
|--------|----------|----------|
| `@Cacheable` | Declarative, 0 lines of cache logic, pluggable backend, testable via `@CacheSpy` | ✅ **Adopted** |
| `RedisTemplate` | Full control, explicit get/set, but couples use case to Redis API | ❌ Rejected |
| Caffeine local cache | Faster, no network hop, but per-instance inconsistency and memory pressure | ❌ Rejected |

**Rationale**: `@Cacheable` requires changing only 1 line in the use case + a config class. Spring manages the cache manager lifecycle, serialization, and TTL. The cache backend can be swapped later (e.g., Caffeine for unit tests) without touching business code.

### Decision: 60s TTL, no event-based invalidation

| Option | Tradeoff | Decision |
|--------|----------|----------|
| TTL-only (60s) | Simple, eventual consistency ≤60s | ✅ **Adopted** |
| Event-based invalidation | Immediate consistency, but requires Kafka consumer in inventory for stock-mutation events + `@CacheEvict` | ❌ Deferred |

**Rationale**: The consolidated stock view is read-heavy and not real-time critical (dashboard/reporting). 60s of staleness is acceptable per spec requirement #4 (<300ms response time). Invalidation events can be added later on a `@CacheEvict` without changing the cache key contract.

### Decision: Graceful degradation via Spring default behavior

**Choice**: Accept Spring's default `CacheException` propagation when Redis is down. No `CacheErrorHandler` or fallback cache for now.

**Alternatives considered**: Composite `CacheManager` with Caffeine fallback; custom `CacheErrorHandler` that logs and returns `null`.

**Rationale**: Adding a fallback cache manager introduces complexity (two cache layers, eviction semantics) for a low-likelihood scenario. The proposal flags this risk, and a `CacheErrorHandler` can be added as a follow-up if Redis reliability becomes an issue in staging/production.

## Data Flow

```
Client ──GET /api/v1/consolidated-stock?storeId=X&page=0&size=20──→ Controller
                                                                      │
                                                                      ▼
                                                            ConsolidatedStockUseCase
                                                                    │
                                                          ┌── @Cacheable ──┐
                                                          │               │
                                                     Redis GET       Cache MISS
                                                       (key)            │
                                                          │               ▼
                                                      Cache HIT    stockPort.findAll()
                                                          │         productPort.findById()
                                                          │         build response
                                                          │               │
                                                          │         Redis PUT (60s TTL)
                                                          │               │
                                                          └───── RETURN ──┘
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `services/inventory/build.gradle.kts` | Modify | Add `implementation("org.springframework.boot:spring-boot-starter-data-redis")` (version managed by BOM) |
| `services/inventory/src/main/kotlin/com/siga/inventory/config/CacheConfig.kt` | **Create** | `@Configuration @EnableCaching` class |
| `services/inventory/src/main/kotlin/com/siga/inventory/application/usecase/ConsolidatedStockUseCase.kt` | Modify | Add `@Cacheable(cacheNames = ["consolidatedStock"], key = "#storeId?.toString() ?: 'all' + ':' + #page + ':' + #size")` on `execute()` |
| `services/inventory/src/main/resources/application.yml` | Modify | Add `spring.cache.redis.time-to-live=60s` and `spring.data.redis.host/port` using env vars |
| `services/inventory/src/test/resources/application-test.yml` | Modify | Add Redis config pointing to localhost:6379 (Testcontainers will override with dynamic port) |
| `services/inventory/src/test/kotlin/com/siga/inventory/integration/ConsolidatedStockCacheTest.kt` | **Create** | Integration test verifying cache hit/miss behavior with Testcontainers |
| `docker-compose.yml` | Modify | Add `siga-redis` service (image: redis:7-alpine, port 6379, network siga-net) |
| `.env.example` | Modify | Add `REDIS_PORT=6379` in "Local Infrastructure Defaults" section |

## Interfaces / Contracts

**Cache key contract** (all components MUST agree on this format):

```
consolidatedStock::<storeId_or_"all">:<page>:<size>
```

Examples:
- `consolidatedStock::all:0:20` — all stores, page 0, size 20
- `consolidatedStock::a1b2c3d4:0:50` — store a1b2c3d4, page 0, size 50

**CacheConfig** (new):

```kotlin
@Configuration
@EnableCaching
class CacheConfig
```

Empty body — `@EnableCaching` activates Spring's post-processor that scans for `@Cacheable` annotations. `RedisCacheConfiguration` is auto-configured by `spring-boot-starter-data-redis` and customised via `application.yml`.

**execute() annotation change**:

```kotlin
@Cacheable(cacheNames = ["consolidatedStock"], key = "#storeId?.toString() ?: 'all' + ':' + #page + ':' + #size")
fun execute(storeId: UUID?, page: Int, size: Int): ConsolidatedStockResponse
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Existing tests (unchanged) | Pure MockK/Kotest — `@Cacheable` is inert without Spring AOP proxy. All 30+ existing tests pass without modification. |
| Integration | Cache hit returns cached data without DB call | `ConsolidatedStockCacheTest`: `@SpringBootTest` + `@ActiveProfiles("test")` + Redis Testcontainer. Execute same request twice; second call returns cached `ConsolidatedStockResponse` without hitting `stockPort.findAll()` (verify via `@SpyBean` on `StockRepositoryPort` or by comparing response time). |
| Integration | Cache miss with different key | Vary `storeId`, `page`, or `size` — each produces a different cache key and triggers `execute()`. |
| Integration | TTL expiration | Set short TTL (`spring.cache.redis.time-to-live=1s`), wait, verify fresh call to `execute()`. |

**Integration test structure** — follows existing pattern at `ConsolidatedStockQueryTest`: JUnit 5 with `@SpringBootTest`, uses `@DynamicPropertySource` to inject Testcontainer Redis port.

## Migration / Rollout

No migration required. Redis is additive infrastructure. The first deployment without Redis will log connection warnings but the service continues to function (Spring's `RedisCacheManager` fails at first cache access if Redis is unreachable). Apply the docker-compose change first so Redis is available when the inventory service restarts.

## Open Questions

- [ ] Should we add a `CacheErrorHandler` bean in this change or defer it as documented? Current design defers.
- [ ] Verify that Testcontainers dependency is available in the test classpath — `spring-boot-starter-test` includes it transitively via `org.testcontainers:testcontainers` in Boot 4.x, but we may need `org.testcontainers:testcontainers:1.20.x` explicitly for the Redis module.

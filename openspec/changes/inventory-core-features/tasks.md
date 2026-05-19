# Tasks: Inventory Core Features

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~1300–1500 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1: Foundation → PR 2: Ports & Adapters → PR 3: Business Logic → PR 4: Controllers + Cleanup |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: pending
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | DB migration + domain models + entities + mappers | PR 1 | Base = `migracion-microservicios`; tests include mapper/enum mapping |
| 2 | New ports + adapters + repository queries | PR 2 | Base = PR 1; tests via `@DataJpaTest` with Testcontainers |
| 3 | `SkuGenerator` + all 6 use cases | PR 3 | Base = PR 2; unit tests with MockK per use case |
| 4 | Controller endpoints + seed bcrypt cleanup | PR 4 | Base = PR 3; controller integration tests + seed SQL |
| 5 | (Optional) End-to-end smoke tests | — | If time permits after merge |

## Phase 1: Foundation — DB, Domain, Entities, Mappers

- [x] 1.1 Create `V2__inventory_core_features.sql` with SKU column, `sku_sequences` table, `last_movement_at`, `correlation_id`, indexes, unaccent index
- [x] 1.2 Add `RECONCILIATION, TRANSFER` to `domain/model/Movement.kt` + `correlationId`, `destinationStoreId`
- [x] 1.3 Add `sku`, `unitType` to `domain/model/Product.kt`
- [x] 1.4 Add `lastMovementAt` to `domain/model/Stock.kt`
- [x] 1.5 Add `RECONCILIATION` to `entity/Enums.kt` MovementType
- [x] 1.6 Add `sku`, `unitType` columns to `entity/Product.kt`
- [x] 1.7 Add `lastMovementAt` column to `entity/Stock.kt`
- [x] 1.8 Add `correlationId`, `destinationStoreId` to `entity/Movement.kt`
- [x] 1.9 Update `ProductMapper.kt` with `sku`, `unitType`
- [x] 1.10 Update `StockMapper.kt` with `lastMovementAt`
- [x] 1.11 Update `MovementMapper.kt` — map `RECONCILIATION`, `TRANSFER`, `correlationId`, `destinationStoreId`

## Phase 2: Ports, Repositories, Adapters

- [x] 2.1 Create `domain/port/SkuSequencePort.kt` — `nextSequence(tenantId, prefix): Int`
- [x] 2.2 Create `domain/port/AlertRepositoryPort.kt` — `save(alert)`, `findByStoreId(storeId)`
- [x] 2.3 Add `findByProductIds(ids, tenantId)` to `domain/port/StockRepositoryPort.kt`
- [x] 2.4 Add `search(query, page, size)` and `findByNameLike(name, tenantId)` to `domain/port/ProductRepositoryPort.kt`
- [x] 2.5 Add `findByFilters(storeId, type, from, to, pageable)` to `domain/port/MovementRepositoryPort.kt`
- [x] 2.6 Create `infrastructure/adapter/SkuSequenceJpaAdapter.kt` — JPA `sku_sequences` counter
- [x] 2.7 Create `infrastructure/adapter/AlertJpaAdapter.kt` — Alert persistence
- [x] 2.8 Update `infrastructure/adapter/StockJpaAdapter.kt` — implement `findByProductIds`
- [x] 2.9 Update `infrastructure/adapter/ProductJpaAdapter.kt` — implement `search`, `findByNameLike`
- [x] 2.10 Update `infrastructure/adapter/MovementJpaAdapter.kt` — implement `findByFilters`
- [x] 2.11 Add `@Query` search method (ILIKE + unaccent) to `repository/ProductRepository.kt`
- [x] 2.12 Add filtered query methods to `repository/MovementRepository.kt`

## Phase 3: Domain Service & Use Cases

- [x] 3.1 Create `domain/service/SkuGenerator.kt` — prefix extraction + sequence via port
- [x] 3.2 Create `application/usecase/ConsolidatedStockUseCase.kt` — paginated products → batch load stock → in-memory group
- [x] 3.3 Create `application/usecase/CreateProductUseCase.kt` — SKU gen, fuzzy duplicate check, save
- [x] 3.4 Create `application/usecase/SearchProductsUseCase.kt` — delegate to port, validate min 2 chars
- [x] 3.5 Create `application/usecase/ReconcileStockUseCase.kt` — discrepancy calc, stock adjust, >10% alert
- [x] 3.6 Create `application/usecase/TransferStockUseCase.kt` — `@Transactional` OUT+IN with correlation UUID
- [x] 3.7 Create `application/usecase/TransferMovementHistoryUseCase.kt` — filtered movement history

## Phase 4: Controllers & Wiring

- [ ] 4.1 Add `GET /consolidated`, `POST /reconciliations`, `POST /transfers`, `GET /movements` to `StockController.kt`
- [ ] 4.2 Replace direct JPA repo usage in `ProductController.kt` — use `CreateProductUseCase`, add `GET /search`, `GET /duplicate-check`

## Phase 5: Seed Cleanup

- [ ] 5.1 Replace hardcoded bcrypt hashes with env-var placeholder in `scripts/seed/01_seed_auth.sql`
- [ ] 5.2 Replace hardcoded bcrypt hashes with env-var placeholder in `scripts/seed/04_seed_billing.sql`
- [ ] 5.3 Replace hardcoded bcrypt hashes with env-var placeholder in `scripts/seed/demo_data.sql`

## Phase 6: Testing

- [ ] 6.1 Unit tests for `SkuGenerator` — prefix extraction, sequence logic (no Spring)
- [ ] 6.2 Unit test per Use Case (MockK) — ConsolidatedStock, CreateProduct, SearchProducts, ReconcileStock, TransferStock, TransferMovementHistory
- [ ] 6.3 Integration test — consolidated stock query via `@DataJpaTest`
- [ ] 6.4 Integration test — search ILIKE + unaccent via `@DataJpaTest`
- [ ] 6.5 Integration test — transfer `@Transactional` atomicity (rollback-only assertion)
- [ ] 6.6 Integration test — reconciliation >10% alert creation
- [ ] 6.7 Integration test — controller endpoints (MockMvc)

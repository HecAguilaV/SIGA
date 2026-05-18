# Design: Inventory Core Features

## Technical Approach

Five independent capabilities layered on the existing hexagonal architecture. Follow `ReserveStockUseCase` pattern: Controller → UseCase → Port → JpaAdapter → JpaRepository. Add `sku` and `unit_type` to Product. Extend Movement domain types to support RECONCILIATION and TRANSFER. All DB changes via Flyway V2. Controllers that currently inject JPA repos directly (`StockController`, `ProductController`) remain untouched; new endpoints use the hexagonal stack exclusively.

## Architecture Decisions

### 1. Consolidated Stock — Aggregation Query + In-Memory Group

| Option | Tradeoff | Decision |
|--------|----------|----------|
| DB View | Can't paginate across products efficiently | ❌ |
| Two-phase query (paginated products + batch load stock) | 2 queries max, handles pagination, <300ms at 10 stores | ✅ |

**Rationale**: First query fetches paginated products for tenant. Second loads all stock + store names for those product IDs. In-memory group builds the per-store breakdown. Avoids N+1, supports pagination, and `lastMovementAt` comes from `stock.updated_at` (updated on every mutation).

### 2. Auto-SKU — Domain Service + `sku_sequences` Table

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Repository method (sequence query) | Couples sequence logic to JPA | ❌ |
| `SkuGenerator` domain service | Pure logic, testable, owns prefix extraction + `NEXTVAL` via port | ✅ |

**Rationale**: SKU business rules (category prefix → uppercase first 3 chars, sequential per tenant) belong in the domain. The port interface `SkuSequencePort` exposes `nextSequence(tenantId, prefix): Int`; adapter calls `SELECT NEXTVAL(...)` or a dedicated `sku_sequences` table. The domain model and entity gain `sku: String` and `unitType: String`.

### 3. Search — ILIKE + `unaccent()` in Repository Layer

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Dedicated search service (Elasticsearch) | Overkill for ≤10K products | ❌ |
| `@Query` with native ILIKE + unaccent | Single query, parameterized, sub-500ms with BTREE index on `unaccent(name)` | ✅ |

**Rationale**: PostgreSQL `unaccent` + `ILIKE` with a functional index meets all requirements. Query is parameterized (no injection risk). Rank by `CASE WHEN name ILIKE 'prefix%' THEN 0 ELSE 1 END`.

### 4. Reconciliation — Synchronous in Endpoint

| Option | Tradeoff | Decision |
|--------|----------|----------|
| Async (Kafka/background) | Adds latency, complexity, no benefit at this volume | ❌ |
| Sync in POST endpoint | Immediate response, alert creation is cheap INSERT | ✅ |

**Rationale**: The spec shows a single POST with immediate response including `alertCreated`. Adding async would require a consumer, retry logic, and eventual-consistency window. Sync at this scale is simpler and correct.

### 5. Transfer — `@Transactional` on UseCase

| Option | Tradeoff | Decision |
|--------|----------|----------|
| SAGA with Kafka | Would require orchestration for a single-DB operation | ❌ |
| Single `@Transactional` | Atomic OUT + IN + movements in one DB transaction | ✅ |

**Rationale**: Source and destination stores are in the same PostgreSQL instance. A `@Transactional` (REQUIRED default) on `TransferStockUseCase` ensures both stock updates and both movement records commit or roll back as one. Correlation UUID links the pair.

## Data Flow

```
┌─ Consolidated Stock ─────────────────────────────────────┐
│ GET /api/v1/inventory/stock/consolidated                 │
│   → ConsolidatedStockUseCase                              │
│     → StockRepositoryPort.findByProductIds(ids, tenantId) │
│     → StockJpaAdapter → JOIN stock + stores               │
│     → aggregate DTO in-memory → paginated response       │
└──────────────────────────────────────────────────────────┘

┌─ Product Creation ───────────────────────────────────────┐
│ POST /api/v1/inventory/products                          │
│   → CreateProductUseCase                                  │
│     → [no SKU] SkuGenerator.nextSku(tenantId, catId)     │
│     → [duplicate?] ProductRepositoryPort.findByNameLike() │
│     → ProductRepositoryPort.save(product)                 │
└──────────────────────────────────────────────────────────┘

┌─ Stock Reconciliation ───────────────────────────────────┐
│ POST /api/v1/inventory/stock/reconciliations             │
│   → ReconcileStockUseCase                                 │
│     → StockRepositoryPort.findByProductIdAndStoreId()    │
│     → MovementRepositoryPort.save(RECONCILIATION)        │
│     → StockRepositoryPort.save(adjusted)                 │
│     → [>10% disc.] AlertRepositoryPort.create()          │
└──────────────────────────────────────────────────────────┘

┌─ Warehouse Transfer ─────────────────────────────────────┐
│ POST /api/v1/inventory/stock/transfers                   │
│   → TransferStockUseCase (@Transactional)                 │
│     → validate: stock >= quantity, stores differ          │
│     → StockRepositoryPort.save(origin, -qty)             │
│     → StockRepositoryPort.save(dest, +qty)               │
│     → MovementRepositoryPort.save(OUT + correlationId)   │
│     → MovementRepositoryPort.save(IN + correlationId)    │
└──────────────────────────────────────────────────────────┘

┌─ Inventory Search ───────────────────────────────────────┐
│ GET /api/v1/inventory/products/search?q={query}          │
│   → SearchProductsUseCase                                 │
│     → ProductRepositoryPort.search(query, page, size)    │
│     → ProductJpaAdapter → @Query native ILIKE+unaccent  │
└──────────────────────────────────────────────────────────┘
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `domain/model/Product.kt` | Modify | Add `sku: String?`, `unitType: String?` |
| `domain/model/Movement.kt` | Modify | Add `RECONCILIATION, TRANSFER` to MovementType enum; add `correlationId: UUID?`, `destinationStoreId: UUID?` |
| `domain/model/Stock.kt` | Modify | Add `lastMovementAt: Instant?` |
| `domain/port/StockRepositoryPort.kt` | Modify | Add `findByProductIds(ids, tenantId): List<StockWithStore>` |
| `domain/port/ProductRepositoryPort.kt` | Modify | Add `search(query, page, size)`, `findByNameLike(name, tenantId)` |
| `domain/port/MovementRepositoryPort.kt` | Modify | Add `findByFilters(storeId, type, from, to, pageable)` |
| `domain/port/SkuSequencePort.kt` | Create | Port for SKU sequence generation |
| `domain/port/AlertRepositoryPort.kt` | Create | Port for alert persistence |
| `domain/service/SkuGenerator.kt` | Create | Domain service: `nextSku(tenantId, categoryId): String` |
| `application/usecase/ConsolidatedStockUseCase.kt` | Create | Aggregates stock per product across stores |
| `application/usecase/CreateProductUseCase.kt` | Create | Handles creation, SKU gen, duplicate check |
| `application/usecase/SearchProductsUseCase.kt` | Create | Delegates to port search |
| `application/usecase/ReconcileStockUseCase.kt` | Create | Physical count, discrepancy calc, alert if >10% |
| `application/usecase/TransferStockUseCase.kt` | Create | Atomic OUT+IN with correlationId |
| `application/usecase/TransferMovementHistoryUseCase.kt` | Create | Queries movement history with filters |
| `controller/StockController.kt` | Modify | Add `GET /consolidated`, `POST /reconciliations`, `POST /transfers` |
| `controller/ProductController.kt` | Modify | Replace `createProduct` with use-case call; add `GET /search`, `GET /duplicate-check` |
| `infrastructure/adapter/StockJpaAdapter.kt` | Modify | Implement new port methods |
| `infrastructure/adapter/ProductJpaAdapter.kt` | Modify | Implement search + name-like queries |
| `infrastructure/adapter/MovementJpaAdapter.kt` | Modify | Implement filtered queries + save with correlation |
| `infrastructure/adapter/SkuSequenceJpaAdapter.kt` | Create | JPA-based sequence counter |
| `infrastructure/adapter/AlertJpaAdapter.kt` | Create | Alert persistence |
| `infrastructure/mapper/ProductMapper.kt` | Modify | Add sku, unitType mapping |
| `infrastructure/mapper/StockMapper.kt` | Modify | Add lastMovementAt |
| `infrastructure/mapper/MovementMapper.kt` | Modify | Map RECONCILIATION, TRANSFER types + correlationId |
| `entity/Product.kt` | Modify | Add `sku`, `unitType` columns |
| `entity/Stock.kt` | Modify | Add `lastMovementAt` |
| `entity/Movement.kt` | Modify | Add `correlationId`, `destinationStoreId` |
| `entity/Alerts.kt` | Modify | Add RECONCILIATION alert type (if needed) or reuse |
| `entity/Enums.kt` | Modify | Add `RECONCILIATION` to MovementType |
| `repository/ProductRepository.kt` | Modify | Add `@Query search` method |
| `repository/MovementRepository.kt` | Modify | Add query methods for filters |
| `db/migration/V2__inventory_core_features.sql` | Create | New columns, `sku_sequences` table, indexes |

## Interfaces / Contracts

```kotlin
// Ports
interface SkuSequencePort {
    fun nextSequence(tenantId: UUID, prefix: String): Int
}
interface AlertRepositoryPort {
    fun save(alert: Alert): Alert
    fun findByStoreId(storeId: UUID): List<Alert>
}

// Domain Services
class SkuGenerator(private val sequencePort: SkuSequencePort) {
    fun nextSku(tenantId: UUID, categoryId: UUID?): String
    // prefix = category name → uppercase first 3 chars, else "GEN"
    // format = "{prefix}-{sequence:03d}"
}

// DTOs
data class ConsolidatedStockResponse(
    val products: List<ConsolidatedProduct>,
    val page: Int, val size: Int, val totalElements: Long, val totalPages: Int
)
data class ConsolidatedProduct(
    val productId: UUID, val productName: String, val sku: String,
    val totalStock: Int, val stores: List<StoreStock>
)
data class StoreStock(
    val storeId: UUID, val storeName: String, val quantity: Int, val lastMovementAt: Instant?
)
```

## Database Changes (V2 Migration)

```sql
-- Products: add SKU and unit_type
ALTER TABLE inventory.products ADD COLUMN sku VARCHAR(20);
ALTER TABLE inventory.products ADD COLUMN unit_type VARCHAR(20) NOT NULL DEFAULT 'UNIDAD';
CREATE UNIQUE INDEX uq_products_sku_tenant ON inventory.products (sku, commercial_user_id);

-- Stock: add last_movement_at
ALTER TABLE inventory.stock ADD COLUMN last_movement_at TIMESTAMPTZ;

-- Movements: add correlation_id and destination_store_id for transfers
ALTER TABLE inventory.movements ADD COLUMN correlation_id UUID;
ALTER TABLE inventory.movements ADD COLUMN destination_store_id UUID;
CREATE INDEX idx_movements_correlation ON inventory.movements (correlation_id);
CREATE INDEX idx_movements_filters ON inventory.movements (store_id, type, created_at);

-- SKU sequence counter
CREATE TABLE inventory.sku_sequences (
    tenant_id UUID NOT NULL,
    prefix VARCHAR(10) NOT NULL,
    last_sequence INT NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, prefix)
);

-- Search index (functional index for unaccent + ILIKE)
CREATE INDEX idx_products_name_unaccent ON inventory.products (unaccent(name));

-- Extended movement types in domain
ALTER TABLE inventory.movements DROP CONSTRAINT IF EXISTS ck_movement_type;
```

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | `SkuGenerator` prefix/sequence logic, duplicate scoring, discrepancy calc | Plain JUnit 5, no Spring |
| Unit | All UseCases (mocked ports) | JUnit 5 + MockK/Mockito |
| Integration | Consolidated stock query, search ILIKE + unaccent | `@DataJpaTest` with PostgreSQL testcontainers |
| Integration | Transfer atomicity (`@Transactional` rollback) | `@SpringBootTest` + rollback-only assertion |
| Integration | Reconciliation alert threshold | `@SpringBootTest` verifying Alert row count |

## Migration / Rollout

No feature flags. V2 migration is additive only (new columns, new table) — backwards compatible. Controllers using JPA repos directly (`StockController`, `ProductController`) are NOT removed; new endpoints coexist on the same `@RequestMapping`. Domain model extensions (`sku`, `unitType`) have nullable defaults so existing data is unaffected.

## Open Questions

- [ ] `MovementType` domain enum expansion: current mapper throws for OUT and TRANSFER. Do we merge entity and domain enums into a single shared set, or keep them separate? Recommend shared enum to remove brittle mapping.
- [ ] SKU prefix strategy: extract from category name (first 3 uppercase chars) vs manual prefix per category via a new `categories.prefix` column. Current approach: derive from name.
- [ ] `lastMovementAt`: maintain as column on `stock` (updated on every mutation) vs derive via subquery. Recommend column for read performance; adds write overhead.

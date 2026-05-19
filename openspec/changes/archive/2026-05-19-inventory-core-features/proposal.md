# Proposal: inventory-core-features

## Intent

Business logic for US-2.1 to US-2.5 (consolidated stock, frictionless product creation, search, reconciliation, transfers) + strip bcrypt from seed SQL for GitGuardian.

## Scope

### In Scope
- Consolidated stock: total per product + per-store breakdown, filterable by store
- Auto-SKU (category + sequential) + fuzzy duplicate detection on creation
- Case/accent-insensitive search (ILIKE + unaccent), <500ms
- Physical count: discrepancy detection, motive (MERMA/ROBO/CADUCADO/ERROR_INGRESO/OTRO), alerts, audit log
- Transfer (pair OUT/IN) + movement history filtered by destination + date
- Clean bcrypt from `scripts/seed/01_seed_auth.sql`, `04_seed_billing.sql`, `demo_data.sql`
- Add `sku` column (Flyway V2), `TRANSFER`/`RECONCILIATION` to domain MovementType

### Out of Scope
- Frontend, Kafka/SAGA (US-3.x), auth (US-1.x), billing (US-5.x), agent IA (US-4.x)
- Refactoring existing non-hexagonal endpoints

## Capabilities

### New
- `consolidated-stock`: Multi-store stock with per-store breakdown
- `product-sku-autogen`: Auto-SKU + fuzzy duplicate detection
- `product-search`: Case/accent-insensitive partial search
- `stock-reconciliation`: Physical counting with discrepancy handling
- `stock-transfer`: Store-to-store transfers with traceability

### Modified
- None

## Approach

Follow `ReserveStockUseCase` pattern: domain → port → use case → adapter → controller. Transfer pairs OUT (origin) + IN (destination) via correlation UUID. Reconciliation logs discrepancy as `RECONCILIATION` movement, creates Alert on >10% delta. Seed bcrypt → env-var placeholder.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `domain/model/Movement.kt` | Modified | Add TRANSFER, RECONCILIATION types |
| `domain/model/Product.kt`, `entity/Product.kt` | Modified | Add sku field |
| `domain/port/*.kt` | Modified | Add search, findByStore |
| `application/usecase/` | **New** | 5 use cases |
| `controller/` | **New** | 3 controllers + search endpoint |
| `infrastructure/mapper/` | Modified | SKU, TRANSFER, RECONCILIATION support |
| `V2__inventory_features.sql` | **New** | sku column, enable unaccent |
| `scripts/seed/*.sql` | Modified | Replace bcrypt hashes |

## Risks

| Risk | Mitigation |
|------|------------|
| Transfer partial update | Transactional boundary + compensating alert |
| Fuzzy search precision | Tune threshold, log false positives |
| SKU tenant conflicts | Prefix sequential per tenant |
| Seed fix breaks dev | Document env-var fallback in README |

## Rollback

1. Revert Flyway V2, remove sku from entity/model
2. Delete new controllers + use cases
3. Restore original seed SQL from git
4. Redeploy `siga-inventory`

## Dependencies

- PostgreSQL `unaccent` extension enabled

## Success Criteria

- [ ] Consolidated endpoint totals correct across stores, <300ms
- [ ] Product creation auto-generates SKU + warns on fuzzy duplicates
- [ ] Search returns matches in <500ms for partial/accented input
- [ ] Physical count adjusts stock, logs motive, creates alert on >10% discrepancy
- [ ] Transfer moves stock atomically, recorded in movement history; seed SQL has zero bcrypt literals

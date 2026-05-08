# Sales Service — Test Suite

> **Language:** English | [**Español**](../../../es/services/sales/README.md)

## Overview

The Sales service test suite covers **unit** and **integration** tests for the hexagonal architecture migration. All tests use [Kotest](https://kotest.io/) (`DescribeSpec`) with [MockK](https://mockk.io/) for mocking and Spring MockMvc for HTTP layer validation.

**Total tests:** 83 unit + integration
**Framework:** JUnit 5 platform + Kotest 6.0.0
**Runner:** `./gradlew :services:sales:test`
**Coverage report:** `./gradlew :services:sales:jacocoTestReport` → `build/reports/jacoco/`

---

## Test Layers

### 1. Unit Tests — Use Cases (`application/usecase/`)

Pure business logic tests. Ports are mocked — no database, no Spring context.

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `CreateSaleUseCaseTest` | 2 | Sale creation orchestrates persistence + SAGA event emission. Verifies items get `saleId` assigned. |
| `ManageCustomerUseCaseTest` | 5 | Customer CRUD: create, find by ID, find by taxId. Found/missing scenarios. |
| `ManageCashShiftUseCaseTest` | 5 | Shift opening (OPEN status), closing (CLOSED + final amount), missing shift returns null. |

### 2. Unit Tests — Controllers (`controller/`)

Pure hexagonal: controllers depend on ports only. Tested with mocked ports, **no Spring context**.

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `SaleControllerTest` | 10 | All REST endpoints: GET all/by ID/by store/by user/by status, POST with `CreateSaleRequest` DTO, PUT. 404/400 scenarios. |
| `CashShiftControllerTest` | 8 | All REST endpoints: GET all/by ID/by store/by user, POST open shift, PUT update. 404 scenarios. |

### 3. Unit Tests — Mappers (`infrastructure/mapper/`)

Domain ↔ JPA Entity conversion. tests verify every field maps correctly, including the UUID-zero boundary for JPA ID generation.

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `SaleMapperTest` | 8 | `toDomain`, `toEntity`, roundtrip, UUID-zero → null, nullable fields |
| `SaleItemMapperTest` | 4 | Same pattern — full field mapping + UUID-zero |
| `CustomerMapperTest` | 7 | Same pattern — full field mapping + nullable + roundtrip |
| `CashShiftMapperTest` | 6 | Same pattern — note: `toEntity` does NOT map `openedAt`, `closedAt`, or `finalAmount` (set elsewhere) |
| `SaleDocumentMapperTest` | 7 | Same pattern — full mapping with nested enums (`DocumentType`, `DocumentStatus`) |
| `PaymentMethodMapperTest` | 5 | Same pattern — simple two-field entity |

### 4. Unit Tests — Events (`event/`)

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `SaleEventProducerTest` | 2 | Kafka producer publishes to correct topic with saleId as key |
| `StockEventConsumerTest` | 5 | SAGA step 3: STOCK_RESERVED → COMPLETED, STOCK_FAILED → CANCELLED, duplicate skip, not-found skip, already-completed skip |

### 5. Integration Tests — Kafka Embedded (requires no Docker)

Uses `@EmbeddedKafka` from `spring-kafka-test` to verify real Kafka serialization/deserialization without an external broker.

| Test Class | Tests | What It Covers |
|-----------|-------|----------------|
| `StockEventConsumerIntegrationTest` | TBD | Full roundtrip: produce StockEvent → consume via @KafkaListener → verify sale status updated in H2 |

**Not covered yet:**
- Feign client integration (requires Docker + running Inventory service)
- Full SAGA choreography end-to-end (requires Docker + Kafka + PostgreSQL + Inventory)
- Eureka/Gateway registration tests

---

## Running Tests

```bash
# All unit + integration tests
./gradlew :services:sales:test

# Only unit tests (faster, no Spring context)
./gradlew :services:sales:test --tests "*Test" --exclude-task compileTestKotlin

# Coverage report
./gradlew :services:sales:jacocoTestReport
# open build/reports/jacoco/html/index.html
```

---

## Bug History

| Bug | Found By | Fix |
|-----|----------|-----|
| `createdAt` lost in domain→entity conversion (SaleMapper, CustomerMapper, SaleDocumentMapper) | Roundtrip tests | Added `createdAt` parameter to `toEntity()` calls |
| Double `@RequestBody` in `SaleController.createSale()` | Code review | Created `CreateSaleRequest` DTO wrapper |

---

*See also: [Testing Strategy](../../README.md) | [Integration Harness](../../INTEGRATION_HARNESS.md)*

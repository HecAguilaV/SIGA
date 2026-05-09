# Sales Service (siga-sales)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Sales orchestrator, point-of-sale (POS) transactions, and tax documents.

## Tech Stack
- **Language**: Kotlin 2.2.0
- **Framework**: Spring Boot 4.0.6
- **Messaging**: Apache Kafka (SAGA Choreography)
- **DB**: PostgreSQL (Schema: `sales`)
- **Service Discovery**: Eureka Client

## SAGA — Event Flow

| Step | Event / Topic | Origin → Destination | Status |
|------|---------------|----------------------|--------|
| 1 | `sale-events` → Kafka | Sales → Inventory (reserve stock) | ✅ |
| 2 | `stock-events` → Kafka | Inventory → Sales (reservation result) | ✅ |
| 3 | `sale-completed` → Kafka | Sales → Billing (generate invoice) | ✅ |

**Produces**: `SaleCompletedEvent` to topic `sale-completed` after confirming stock reservation.
**Consumes**: `StockEvent` from topic `stock-events` (Inventory response).

## Architecture
- [x] **Hexagonal Architecture** — Domain/Application/Infrastructure/Controller layers
- [x] UUID v4 (Law 21.719)
- [x] SAGA Idempotency (Table: `processed_events`)
- [x] Feign Client → Inventory (sync fallback)

## Tests

| Type | Tests | Documentation |
|------|-------|---------------|
| Unit (use cases, controllers, mappers, events) | 83 | [EN](../../docs/tests/en/services/sales/README.md) · [ES](../../docs/tests/es/services/sales/README.md) |
| Kafka Embedded Integration | In development | Same reference |

```bash
# Run all tests
./gradlew :services:sales:test

# Coverage report
./gradlew :services:sales:jacocoTestReport
```

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/sales` | List all sales |
| GET | `/api/v1/sales/{id}` | Sale by ID |
| GET | `/api/v1/sales/store/{storeId}` | Sales by store |
| GET | `/api/v1/sales/user/{userId}` | Sales by user |
| GET | `/api/v1/sales/status/{status}` | Sales by status |
| POST | `/api/v1/sales` | Create sale (SAGA) |
| PUT | `/api/v1/sales/{id}` | Update sale |
| GET | `/api/v1/sales/cash-shifts` | List cash shifts |
| GET | `/api/v1/sales/cash-shifts/{id}` | Shift by ID |
| GET | `/api/v1/sales/cash-shifts/store/{storeId}` | Shifts by store |
| GET | `/api/v1/sales/cash-shifts/user/{userId}/open` | Open shift by user |
| POST | `/api/v1/sales/cash-shifts` | Open cash shift |
| PUT | `/api/v1/sales/cash-shifts/{id}` | Close/update shift |


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

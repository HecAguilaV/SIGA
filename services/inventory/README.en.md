# Inventory Service (siga-inventory)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Centralized stock, product, and warehouse movement (Kardex) management.

## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 3.4.3
- **Messaging**: Apache Kafka (SAGA Choreography)
- **DB**: PostgreSQL (Schema: `inventory`)

## APIs & Contracts

### Products
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/products` | List products |
| `POST` | `/api/v1/inventory/products` | Create product (auto-SKU if empty, duplicate detection) |
| `GET` | `/api/v1/inventory/products/search?q=X` | ILIKE + unaccent search (min 2 chars) |
| `GET` | `/api/v1/inventory/products/duplicate-check?name=X` | Check duplicates by name |

### Stock
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/stock/product/{id}` | Product stock by store |
| `GET` | `/api/v1/inventory/stock/store/{id}` | Store stock overview |
| `GET` | `/api/v1/inventory/stock/consolidated?productId=X` | **Multi-point consolidated stock** (total + breakdown) |
| `POST` | `/api/v1/inventory/stock/reconciliations` | **Physical count** with discrepancy detection and alerts |
| `POST` | `/api/v1/inventory/stock/transfers` | **Warehouse↔store transfer** (atomic via @Transactional) |

### Movements (Kardex)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/inventory/stock/movements` | Filtered history by store, type, dates |

## Interconnections
- **SAGA**: Listens to `sale-events` (Topic: `sale-events`) for stock reservation.
- **SAGA**: Publishes `stock-events` (Topic: `stock-events`) with reservation results.
- **Service Registry**: Registers with `siga-registry` (Eureka).

## Architecture
- [x] Hexagonal (Ports & Adapters)
- [x] Strict TDD (50+ tests — unit + integration)
- [x] SDD: `openspec/changes/archive/2026-05-19-inventory-core-features/`
- [x] UUID v4 (Law 21.719)
- [x] Idempotency (Table: `processed_events`)

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

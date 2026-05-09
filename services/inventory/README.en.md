# Inventory Service (siga-inventory)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Centralized stock, product, and warehouse movement (Kardex) management.

## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 4.0.6
- **Messaging**: Apache Kafka (SAGA Choreography)
- **DB**: PostgreSQL (Schema: `inventory`)

## APIs & Contracts
- **Products**: `GET /api/v1/inventory/products`
- **Stock**: `GET /api/v1/inventory/stock/{productId}`
- **Movements**: `POST /api/v1/inventory/movements`
- **Swagger**: `http://localhost:8082/swagger-ui.html`

## Interconnections
- **SAGA**: Listens to `sale-events` (Topic: `sale-events`) for stock reservation.
- **SAGA**: Publishes `stock-events` (Topic: `stock-events`) with reservation results.
- **Service Registry**: Registers with `siga-registry` (Eureka).

## Architecture
- [x] Hexagonal
- [x] UUID v4 (Law 21.719)
- [x] Idempotency (Table: `processed_events`)

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

# Inventory Service (siga-inventory)

Gestión centralizada de stock, productos y movimientos de almacén (Kardex).

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 3.2.x
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **BD**: PostgreSQL (Esquema: `inventory`)

## APIs & Contratos
- **Productos**: `GET /api/v1/inventory/products`
- **Stock**: `GET /api/v1/inventory/stock/{productId}`
- **Movimientos**: `POST /api/v1/inventory/movements`
- **Swagger**: `http://localhost:8082/swagger-ui.html`

## Interrelaciones
- **SAGA**: Escucha `sale-events` (Topic: `sale-events`) para reservar stock.
- **SAGA**: Publica `stock-events` (Topic: `stock-events`) con el resultado de la reserva.
- **Service Registry**: Se registra en `siga-registry` (Eureka).

## Arquitectura
- [x] Hexagonal
- [x] UUID v4 (Ley 21.719)
- [x] Idempotencia (Tabla: `processed_events`)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

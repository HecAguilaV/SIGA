# Sales Service (siga-sales)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Orquestador de ventas, transacciones de punto de venta (POS) y documentos tributarios.

## Stack Tecnológico
- **Lenguaje**: Kotlin 2.2.0
- **Framework**: Spring Boot 3.4.3
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **BD**: PostgreSQL (Esquema: `sales`)
- **Service Discovery**: Eureka Client

## SAGA — Flujo de Eventos

| Paso | Evento / Topic | Origen → Destino | Estado |
|------|----------------|------------------|--------|
| 1 | `sale-events` → Kafka | Sales → Inventory (reservar stock) | ✅ |
| 2 | `stock-events` → Kafka | Inventory → Sales (resultado reserva) | ✅ |
| 3 | `sale-completed` → Kafka | Sales → Billing (generar factura) | ✅ |

**Produce**: `SaleCompletedEvent` al topic `sale-completed` luego de confirmar stock reservado.
**Consume**: `StockEvent` del topic `stock-events` (respuesta de Inventory).

## Arquitectura
- [x] **Hexagonal Architecture** — Domain/Application/Infrastructure/Controller layers
- [x] UUID v4 (Ley 21.719)
- [x] Idempotencia SAGA (Tabla: `processed_events`)
- [x] Feign Client → Inventory (fallback síncrono)

## Tests

| Tipo | Tests | Documentación |
|------|-------|--------------|
| Unitarios (use cases, controllers, mappers, events) | 83 | [EN](../../docs/tests/en/services/sales/README.md) · [ES](../../docs/tests/es/services/sales/README.md) |
| Integración Kafka Embedded | En desarrollo | Misma referencia |

```bash
# Ejecutar todos los tests
./gradlew :services:sales:test

# Reporte de cobertura
./gradlew :services:sales:jacocoTestReport
```

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/sales` | Listar todas las ventas |
| GET | `/api/v1/sales/{id}` | Ventas por ID |
| GET | `/api/v1/sales/store/{storeId}` | Ventas por tienda |
| GET | `/api/v1/sales/user/{userId}` | Ventas por usuario |
| GET | `/api/v1/sales/status/{status}` | Ventas por estado |
| POST | `/api/v1/sales` | Crear venta (SAGA) |
| PUT | `/api/v1/sales/{id}` | Actualizar venta |
| GET | `/api/v1/sales/cash-shifts` | Listar turnos de caja |
| GET | `/api/v1/sales/cash-shifts/{id}` | Turno por ID |
| GET | `/api/v1/sales/cash-shifts/store/{storeId}` | Turnos por tienda |
| GET | `/api/v1/sales/cash-shifts/user/{userId}/open` | Turno abierto por usuario |
| POST | `/api/v1/sales/cash-shifts` | Abrir turno de caja |
| PUT | `/api/v1/sales/cash-shifts/{id}` | Cerrar/actualizar turno |


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

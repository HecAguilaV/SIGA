# Design: SAGA Sales-Inventory

## Technical Approach
Coreografía de eventos vía Spring Kafka. Sales produce eventos y consume respuestas. Inventory consume eventos y produce respuestas. Sin orquestador central.

## Architecture Decisions

| Decision | Choice | Alternative | Rationale |
|----------|--------|-------------|-----------|
| Patrón SAGA | Coreografía | Orquestación | Solo 2 servicios; coreografía es más simple y no requiere servicio extra |
| Broker | Apache Kafka (Confluent) | RabbitMQ | Ya integrado en docker-compose; soporta Big Data pipeline futuro |
| Serialización | JSON (Jackson) | Avro/Protobuf | Simplicidad; Avro se evaluará cuando haya schema registry |
| Idempotencia | Tabla `processed_events` | Cache Redis | No introduce nueva dependencia; PostgreSQL ya disponible |
| Fallback | Mantener Feign client | Eliminar Feign | Permite rollback sin código nuevo si Kafka falla |

## Data Flow

```
[Sales: Crear Venta]
       │
       ▼ produce
  ┌─────────────────┐
  │  sale-events     │  (Topic Kafka)
  └────────┬────────┘
           │ consume
           ▼
[Inventory: Reservar Stock]
       │
       ▼ produce
  ┌─────────────────┐
  │  stock-events    │  (Topic Kafka)
  └────────┬────────┘
           │ consume
           ▼
[Sales: Confirmar/Cancelar]
```

## Kafka Topics

| Topic | Producer | Consumer | Payload |
|-------|----------|----------|---------|
| `sale-events` | Sales | Inventory | SaleInitiated, SaleCancelled |
| `stock-events` | Inventory | Sales | StockReserved, StockFailed |
| `sale-events.DLT` | Kafka (auto) | — | Dead letters |

## Event Schema (JSON)

```kotlin
data class SaleEvent(
    val eventId: UUID,
    val eventType: String,      // SALE_INITIATED, SALE_CANCELLED
    val saleId: UUID,
    val tenantId: UUID,
    val userId: UUID,
    val items: List<SaleItemEvent>,
    val timestamp: Instant
)

data class SaleItemEvent(
    val productId: UUID,
    val quantity: Int
)

data class StockEvent(
    val eventId: UUID,
    val eventType: String,      // STOCK_RESERVED, STOCK_FAILED
    val saleId: UUID,
    val tenantId: UUID,
    val reason: String?,
    val timestamp: Instant
)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `services/sales/build.gradle.kts` | Modify | Añadir `spring-kafka` dependency |
| `services/inventory/build.gradle.kts` | Modify | Añadir `spring-kafka` dependency |
| `services/sales/src/.../config/KafkaConfig.kt` | Create | Producer y Consumer config |
| `services/sales/src/.../event/SaleEvent.kt` | Create | DTOs de eventos |
| `services/sales/src/.../event/StockEvent.kt` | Create | DTOs de eventos recibidos |
| `services/sales/src/.../event/SaleEventProducer.kt` | Create | Emite eventos al topic |
| `services/sales/src/.../event/StockEventConsumer.kt` | Create | Consume respuestas de Inventory |
| `services/inventory/src/.../config/KafkaConfig.kt` | Create | Producer y Consumer config |
| `services/inventory/src/.../event/SaleEvent.kt` | Create | DTO del evento recibido |
| `services/inventory/src/.../event/StockEvent.kt` | Create | DTO del evento emitido |
| `services/inventory/src/.../event/SaleEventConsumer.kt" | Create | Consume eventos de Sales |
| `services/inventory/src/.../event/StockEventProducer.kt` | Create | Emite respuestas |
| `services/sales/src/.../entity/ProcessedEvent.kt` | Create | Tabla de idempotencia |
| `services/inventory/src/.../entity/ProcessedEvent.kt` | Create | Tabla de idempotencia |
| `scripts/db-init/backup_sql/sales_v1_init.sql` | Modify | Añadir tabla processed_events |
| `scripts/db-init/backup_sql/inventory_v1_init.sql` | Modify | Añadir tabla processed_events |
| `services/sales/src/main/resources/application.yml` | Modify | Kafka bootstrap servers config |
| `services/inventory/src/main/resources/application.yml` | Modify | Kafka bootstrap servers config |

## Testing Strategy

| Layer | What | Approach |
|-------|------|----------|
| Unit | SaleEventProducer, StockEventConsumer | MockK + Kotest |
| Unit | SaleEventConsumer, StockEventProducer | MockK + Kotest |
| Integration | Full SAGA flow | EmbeddedKafka + H2 |
| Idempotency | Duplicate event handling | Unit test con evento duplicado |

## Migration
No migration required. Solo agregar tablas `processed_events` a los scripts de init.

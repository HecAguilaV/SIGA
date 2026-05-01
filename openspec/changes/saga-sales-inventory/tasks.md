# Tasks: SAGA Sales-Inventory

## Phase 1: Infrastructure (Kafka Dependencies)
- [ ] 1.1 Añadir `spring-kafka` a `services/sales/build.gradle.kts`
- [ ] 1.2 Añadir `spring-kafka` a `services/inventory/build.gradle.kts`
- [ ] 1.3 Configurar Kafka bootstrap servers en `application.yml` de Sales
- [ ] 1.4 Configurar Kafka bootstrap servers en `application.yml` de Inventory
- [ ] 1.5 Crear `KafkaConfig.kt` en Sales (Producer + Consumer beans)
- [ ] 1.6 Crear `KafkaConfig.kt` en Inventory (Producer + Consumer beans)

## Phase 2: Event Contracts (DTOs compartidos)
- [ ] 2.1 Crear `SaleEvent.kt` en Sales (`event/` package)
- [ ] 2.2 Crear `StockEvent.kt` en Sales (`event/` package)
- [ ] 2.3 Crear `SaleEvent.kt` en Inventory (`event/` package)
- [ ] 2.4 Crear `StockEvent.kt` en Inventory (`event/` package)

## Phase 3: Idempotency Tables
- [ ] 3.1 Añadir tabla `processed_events` a `sales_v1_init.sql`
- [ ] 3.2 Añadir tabla `processed_events` a `inventory_v1_init.sql`
- [ ] 3.3 Crear entidad `ProcessedEvent.kt` en Sales
- [ ] 3.4 Crear entidad `ProcessedEvent.kt` en Inventory
- [ ] 3.5 Crear repositorio `ProcessedEventRepository.kt` en ambos servicios

## Phase 4: Sales Producer (Emitir eventos)
- [ ] 4.1 Crear `SaleEventProducer.kt` en Sales
- [ ] 4.2 Modificar flujo de creación de venta para emitir `SaleInitiated` tras persistir con status `PENDING`
- [ ] 4.3 Test unitario: `given_valid_sale_when_created_then_emit_sale_initiated_event`

## Phase 5: Inventory Consumer + Producer (Escuchar y responder)
- [ ] 5.1 Crear `SaleEventConsumer.kt` en Inventory
- [ ] 5.2 Implementar lógica de reserva de stock al recibir `SaleInitiated`
- [ ] 5.3 Crear `StockEventProducer.kt` en Inventory
- [ ] 5.4 Emitir `StockReserved` si hay stock, `StockFailed` si no hay
- [ ] 5.5 Test unitario: `given_sale_initiated_when_stock_available_then_emit_stock_reserved`
- [ ] 5.6 Test unitario: `given_sale_initiated_when_stock_insufficient_then_emit_stock_failed`

## Phase 6: Sales Consumer (Confirmar/Compensar)
- [ ] 6.1 Crear `StockEventConsumer.kt` en Sales
- [ ] 6.2 Implementar confirmación de venta al recibir `StockReserved`
- [ ] 6.3 Implementar compensación (cancelación) al recibir `StockFailed`
- [ ] 6.4 Test unitario: `given_stock_reserved_when_sale_pending_then_confirm_sale`
- [ ] 6.5 Test unitario: `given_stock_failed_when_sale_pending_then_cancel_sale`

## Phase 7: Integration Tests
- [ ] 7.1 Test integración: Happy path completo (Sale → Stock Reserved → Confirmed)
- [ ] 7.2 Test integración: Compensación (Sale → Stock Failed → Cancelled)
- [ ] 7.3 Test idempotencia: Evento duplicado no genera doble descuento

## Phase 8: Verification
- [ ] 8.1 `./gradlew :services:sales:test`
- [ ] 8.2 `./gradlew :services:inventory:test`
- [ ] 8.3 Docker Compose up + verificación manual del flujo

# Spec: SAGA Sales-Inventory

## REQ-SAGA-001: Sale Initiation Event
**MUST** emitir un evento `SaleInitiated` al topic `sale-events` cuando se crea una venta.
### Scenario: Successful sale initiation
- **Given** un cajero registra una venta con items válidos
- **When** el servicio Sales persiste la venta con status `PENDING`
- **Then** se emite un evento `SaleInitiated` con saleId, tenantId, items (productId, quantity)

## REQ-SAGA-002: Stock Reservation
**MUST** reservar stock al recibir un evento `SaleInitiated`.
### Scenario: Stock available
- **Given** Inventory recibe un `SaleInitiated` con items disponibles
- **When** se valida que hay stock suficiente para cada item
- **Then** se descuenta el stock, se registra el movimiento y se emite `StockReserved`

### Scenario: Stock insufficient
- **Given** Inventory recibe un `SaleInitiated` con items sin stock
- **When** se detecta que un producto no tiene stock suficiente
- **Then** se emite `StockFailed` con el motivo y los items fallidos

## REQ-SAGA-003: Sale Confirmation
**MUST** confirmar o cancelar la venta según la respuesta de Inventory.
### Scenario: Stock reserved successfully
- **Given** Sales recibe un `StockReserved`
- **When** el saleId coincide con una venta en status `PENDING`
- **Then** la venta pasa a status `COMPLETED` y se genera el sale_document

### Scenario: Stock reservation failed (Compensación)
- **Given** Sales recibe un `StockFailed`
- **When** el saleId coincide con una venta en status `PENDING`
- **Then** la venta pasa a status `CANCELLED` (compensación)

## REQ-SAGA-004: Idempotency
**MUST** garantizar idempotencia en todos los consumidores Kafka.
- Cada evento lleva un `eventId` (UUID).
- Los consumidores verifican si el evento ya fue procesado antes de ejecutar.

## REQ-SAGA-005: Timeout and Dead Letter
**SHOULD** manejar eventos que no reciben respuesta en 30 segundos.
- Si `SaleInitiated` no recibe respuesta en 30s, la venta se marca como `TIMEOUT`.
- Los eventos fallidos van a un Dead Letter Topic (`sale-events.DLT`).

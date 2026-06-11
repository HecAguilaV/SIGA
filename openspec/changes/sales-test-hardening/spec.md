# Spec: Sales Test Hardening

## Scenario 1: Validación de Stock vía Feign (Happy Path)
**Given** que el microservicio de inventario está disponible a través de Feign
**And** que el producto con SKU "PROD-001" tiene stock suficiente
**When** el servicio de Sales solicita la validación de stock
**Then** debe recibir un valor booleano `true`
**And** la petición debe incluir el header `X-Tenant-Id` correcto

## Scenario 2: Validación de Stock vía Feign (Stock Insuficiente)
**Given** que el producto con SKU "PROD-002" NO tiene stock suficiente
**When** el servicio de Sales solicita la validación de stock
**Then** debe recibir un valor booleano `false`

## Scenario 3: Fallo en Comunicación con Inventario (Feign Error)
**Given** que el microservicio de inventario retorna un error 500
**When** el servicio de Sales solicita la validación de stock
**Then** el cliente de Feign debe manejar la excepción o retornar un fallback (si aplica)

## Scenario 4: Coreografía SAGA Completa
**Given** una venta en estado `PENDING`
**When** se recibe un evento `STOCK_RESERVED` de Kafka
**Then** el estado de la venta debe cambiar a `COMPLETED`
**And** se debe emitir un evento `SALE_COMPLETED` para el servicio de Billing

## Scenario 5: Actualización de Aprendizajes
**Given** que se han implementado tests de integración avanzados
**When** se actualiza `ACADEMIC/LEARNING.md`
**Then** debe incluirse la justificación técnica del uso de WireMock y Testcontainers en SIGA.

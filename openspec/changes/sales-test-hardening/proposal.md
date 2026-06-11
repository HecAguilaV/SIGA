# Proposal: Sales Test Hardening & LEARNING.md Update

## Goal
Elevar la confianza en el sistema de ventas mediante la implementación de tests de integración para dependencias externas y la actualización del registro de aprendizaje del proyecto.

## Scope
- Implementar `InventoryClientIntegrationTest` usando WireMock para simular el servicio de inventario.
- Implementar `SagaEndToEndTest` (o similar) para verificar el flujo Sales -> Inventory -> Sales vía Kafka.
- Actualizar `ACADEMIC/LEARNING.md` con una nueva sección sobre "Testing de Integración en Microservicios".
- Actualizar el estado de los tests en la documentación técnica.

## Out of Scope
- Tests de carga o performance.
- Implementación de la Reconciliación de Caja (se hará en un cambio posterior).

## Approach
Usaremos **WireMock** para los tests de Feign Client, lo que nos permite desacoplarnos del microservicio de inventario real durante los tests de Sales. Para la coreografía SAGA, utilizaremos los tests de integración ya existentes pero los consolidaremos en un reporte de verificación claro.

## Success Criteria
- [ ] `InventoryClientIntegrationTest` pasa al 100%.
- [ ] La coreografía SAGA está verificada mediante tests de integración automáticos.
- [ ] `ACADEMIC/LEARNING.md` contiene la nueva sección de aprendizajes.
- [ ] Todos los tests de `services/sales` pasan en el entorno local.

## Timeline
Inmediato (Modo Automático).

# Tasks: Sales Test Hardening

## Phase 1: Feign Client Testing
- [x] 1.1 Agregar dependencia de `WireMock` a `services/sales/build.gradle.kts`.
- [x] 1.2 Crear `InventoryClientIntegrationTest.kt` en `infrastructure/client/`.
- [x] 1.3 Implementar mocks de WireMock para casos Happy Path y Error 500.

## Phase 2: SAGA Verification
- [x] 2.1 Ejecutar `StockEventConsumerIntegrationTest` y asegurar que todos los escenarios de la SAGA están cubiertos.
- [x] 2.2 Reforzar el test con un escenario de "Reintentos en Kafka" si es posible.

## Phase 3: Documentation & LEARNING.md
- [x] 3.1 Actualizar `ACADEMIC/LEARNING.md` con los nuevos aprendizajes.
- [x] 3.2 Actualizar `docs/tests/es/services/sales/README.md` marcando los tests de integración como completados.
- [x] 3.3 Actualizar `openspec/core/STATUS.md` con el conteo final de tests de Sales.

## Phase 4: Final Validation
- [x] 4.1 Ejecutar `./gradlew :services:sales:test` completo.
- [x] 4.2 Commit de los cambios a la rama `migracion-microservicios`.

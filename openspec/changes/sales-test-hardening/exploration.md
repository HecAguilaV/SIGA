## Exploration: Sales Test Hardening & LEARNING.md Update

### Current State
El servicio de `siga-sales` cuenta con una base sólida de 176 tests (según Kotest), incluyendo tests unitarios para casos de uso, controladores y mappers, así como tests de integración con Kafka embebido para el consumidor de eventos de stock. 

Sin embargo, existen brechas identificadas en la documentación (`docs/tests/es/services/sales/README.md`):
1. **Integración Feign**: Existe un `InventoryClient` pero no tiene tests de integración (requiere WireMock).
2. **SAGA E2E**: No hay un test que orqueste `sales` e `inventory` juntos en un entorno controlado (Testcontainers).
3. **Registro en Eureka/Gateway**: Falta verificar que el servicio se registra correctamente y es accesible.

Además, el documento `ACADEMIC/LEARNING.md` debe ser actualizado para reflejar los aprendizajes sobre la arquitectura hexagonal y la implementación de la SAGA en este servicio.

### Affected Areas
- `services/sales/src/test/kotlin/com/siga/sales/infrastructure/client/InventoryClientIntegrationTest.kt` (Nuevo)
- `services/sales/src/test/kotlin/com/siga/sales/saga/SagaEndToEndTest.kt` (Nuevo)
- `ACADEMIC/LEARNING.md` — actualización de contenido.
- `docs/tests/es/services/sales/README.md` — actualización de estado.

### Approaches
1. **Enfoque Minimalista**: Completar solo el test de Feign con WireMock y actualizar documentos.
   - Pros: Rápido, baja complejidad.
   - Cons: No cubre el flujo E2E real.
   - Effort: Low

2. **Enfoque Robusto (Recomendado)**: Implementar tests de integración con WireMock para Feign y un test E2E que verifique la coreografía SAGA completa usando Testcontainers para Kafka y PostgreSQL.
   - Pros: Garantiza que la comunicación entre microservicios funciona.
   - Cons: Mayor tiempo de ejecución de tests.
   - Effort: Medium

### Recommendation
Se recomienda el **Enfoque Robusto** para asegurar la resiliencia del sistema de ventas antes de proceder con nuevas funcionalidades de negocio como la reconciliación de caja.

### Risks
- **Latencia en tests**: Testcontainers puede ser lento en entornos de pocos recursos.
- **Complejidad de Kafka**: Sincronizar eventos en tests E2E asíncronos requiere pollings cuidadosos.

### Ready for Proposal
Yes.

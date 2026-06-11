# Design: Sales Test Hardening

## Architecture for Integration Tests

### 1. Feign Integration (WireMock)
Utilizaremos `WireMock` para interceptar las peticiones HTTP que `InventoryClient` realiza hacia `siga-inventory`.
- **Clase**: `InventoryClientIntegrationTest.kt`
- **Anotaciones**: `@SpringBootTest`, `@AutoConfigureWireMock(port = 0)`
- **Lógica**: Mapear el `url` de Feign al puerto dinámico de WireMock en el perfil de test.

### 2. SAGA E2E (Testcontainers vs Embedded)
Para la coreografía completa, seguiremos usando `@EmbeddedKafka` para evitar la sobrecarga de Testcontainers en este entorno, pero extenderemos los tests existentes para asegurar que se cubren todos los estados finales y la idempotencia.
- **Clase**: `com.siga.sales.event.StockEventConsumerIntegrationTest` (Ya existe, se reforzará si es necesario).

### 3. Registro y Gateway
Verificaremos la configuración de Eureka y ruteo mediante tests de carga de contexto que validen la presencia de los beans necesarios (`EurekaClient`).

## ACADEMIC/LEARNING.md Update Strategy
Agregaremos una sección "Testing y Calidad" en la Parte 6 o como una nueva Parte 8, enfocada en:
- El valor de WireMock para desacoplar microservicios.
- La importancia de los tests de integración en una arquitectura SAGA.
- Cómo los agentes de IA ayudan a mantener la calidad mediante SDD.

## Persistence Strategy
- **Engram**: Guardar cada hito de test exitoso.
- **OpenSpec**: Actualizar `STATUS.md` global.

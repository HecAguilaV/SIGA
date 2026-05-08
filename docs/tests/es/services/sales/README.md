# Sales Service — Suite de Tests

> **Idioma:** Español | [**English**](../../../en/services/sales/README.md)

## Visión General

La suite de tests del servicio Sales cubre tests **unitarios** y de **integración** para la migración a arquitectura hexagonal. Todos los tests usan [Kotest](https://kotest.io/) (`DescribeSpec`) con [MockK](https://mockk.io/) para mocking y Spring MockMvc para validación HTTP.

**Tests totales:** 83 unitarios + integración
**Framework:** JUnit 5 platform + Kotest 6.0.0
**Ejecución:** `./gradlew :services:sales:test`
**Reporte de cobertura:** `./gradlew :services:sales:jacocoTestReport` → `build/reports/jacoco/`

---

## Capas de Test

### 1. Tests Unitarios — Casos de Uso (`application/usecase/`)

Prueban lógica de negocio pura. Los puertos están mockeados — sin base de datos, sin Spring.

| Clase de Test | Tests | Qué Cubre |
|--------------|-------|-----------|
| `CreateSaleUseCaseTest` | 2 | Creación de venta orquesta persistencia + evento SAGA. Verifica que los ítems reciben `saleId`. |
| `ManageCustomerUseCaseTest` | 5 | CRUD cliente: crear, buscar por ID, buscar por taxId. Escenarios encontrado/no encontrado. |
| `ManageCashShiftUseCaseTest` | 5 | Apertura (OPEN), cierre (CLOSED + monto final), caja inexistente retorna null. |

### 2. Tests Unitarios — Controladores (`controller/`)

Hexagonal puro: los controladores dependen solo de puertos. Testeados con puertos mockeados, **sin contexto Spring**.

| Clase de Test | Tests | Qué Cubre |
|--------------|-------|-----------|
| `SaleControllerTest` | 10 | Todos los endpoints REST: GET all/por ID/por tienda/por usuario/por estado, POST con DTO `CreateSaleRequest`, PUT. Escenarios 404/400. |
| `CashShiftControllerTest` | 8 | Todos los endpoints REST: GET all/por ID/por tienda/por usuario, POST apertura, PUT actualización. Escenarios 404. |

### 3. Tests Unitarios — Mappers (`infrastructure/mapper/`)

Conversión dominio ↔ entidad JPA. Verifican que cada campo se mapea correctamente, incluyendo el caso borde de UUID-cero para generación de IDs JPA.

| Clase de Test | Tests | Qué Cubre |
|--------------|-------|-----------|
| `SaleMapperTest` | 8 | `toDomain`, `toEntity`, roundtrip, UUID-cero → null, campos nullables |
| `SaleItemMapperTest` | 4 | Mismo patrón — mapeo completo + UUID-cero |
| `CustomerMapperTest` | 7 | Mismo patrón — mapeo completo + nullables + roundtrip |
| `CashShiftMapperTest` | 6 | Mismo patrón — NOTA: `toEntity` NO mapea `openedAt`, `closedAt` ni `finalAmount` (se setean en otro lado) |
| `SaleDocumentMapperTest` | 7 | Mismo patrón — mapeo completo con enums anidados (`DocumentType`, `DocumentStatus`) |
| `PaymentMethodMapperTest` | 5 | Mismo patrón — entidad simple de dos campos |

### 4. Tests Unitarios — Eventos (`event/`)

| Clase de Test | Tests | Qué Cubre |
|--------------|-------|-----------|
| `SaleEventProducerTest` | 2 | Producer de Kafka publica en el topic correcto con saleId como key |
| `StockEventConsumerTest` | 5 | SAGA paso 3: STOCK_RESERVED → COMPLETED, STOCK_FAILED → CANCELLED, duplicado salta, no encontrado salta, ya completado salta |

### 5. Tests de Integración — Kafka Embedded (no requiere Docker)

Usa `@EmbeddedKafka` de `spring-kafka-test` para verificar serialización/deserialización real de Kafka sin un broker externo.

| Clase de Test | Tests | Qué Cubre |
|--------------|-------|-----------|
| `StockEventConsumerIntegrationTest` | TBD | Roundtrip completo: producir StockEvent → consumir via @KafkaListener → verificar estado de venta en H2 |

**No cubierto aún:**
- Integración Feign (requiere Docker + servicio Inventory corriendo)
- Coreografía SAGA completa end-to-end (requiere Docker + Kafka + PostgreSQL + Inventory)
- Tests de registro en Eureka/Gateway

---

## Ejecutar Tests

```bash
# Todos los tests unitarios + integración
./gradlew :services:sales:test

# Solo tests unitarios (más rápido, sin contexto Spring)
./gradlew :services:sales:test --tests "*Test" --exclude-task compileTestKotlin

# Reporte de cobertura
./gradlew :services:sales:jacocoTestReport
# abrir build/reports/jacoco/html/index.html
```

---

## Historial de Bugs

| Bug | Encontrado Por | Fix |
|-----|---------------|-----|
| `createdAt` perdido en conversión dominio→entidad (SaleMapper, CustomerMapper, SaleDocumentMapper) | Tests de roundtrip | Se agregó `createdAt` a `toEntity()` |
| Double `@RequestBody` en `SaleController.createSale()` | Code review | Se creó DTO `CreateSaleRequest` |

---

*Ver también: [Estrategia de Testing](../../README.md) | [Arnes de Integración](../../ARNES_INTEGRACION.md)*

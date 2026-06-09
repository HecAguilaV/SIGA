# Billing Coverage Junio 2026

## Intent
Elevar la cobertura de tests del microservicio Billing del 20% al 85%+, incluyendo auditoría de seguridad en controllers.

## Scope
- Domain models: tests para todos los modelos y enums
- JPA entities: tests de creación y campos
- Mappers: tests de conversión domain↔entity
- Controllers: todos los endpoints con MockMvc + auditoría OWASP
- Events: BillingInvoiceConsumer + SaleCompletedEvent
- Config: KafkaConfig bean verification
- Use Cases: caminos de error + borde añadidos

## Non-Goals
- NO modificar código de producción
- NO agregar nuevas features
- NO tocar infraestructura cloud

## Impact
- 184 tests nuevos, 32 archivos
- Cobertura billing: 20% → 86%
- Cobertura global: 74% → 80%
- Tests totales: 672 → 856

## Security
- Input validation tests en todos los controllers
- UUID format validation
- IDOR risk documented (customerId endpoints)
- Error handling verified (no stack leak)

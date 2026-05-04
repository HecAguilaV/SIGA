# Sales Service (siga-sales)

Orquestador de ventas, transacciones de punto de venta (POS) y documentos tributarios.

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 4.0.6
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **BD**: PostgreSQL (Esquema: `sales`)

## Arquitectura
- [ ] Hexagonal (Pendiente de refactorización - Ver Billing/Inventory como referencia)
- [x] UUID v4 (Ley 21.719)
- [x] Idempotencia (Tabla: `processed_events`)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

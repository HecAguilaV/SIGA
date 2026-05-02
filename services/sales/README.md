# Sales Service (siga-sales)

Orquestador de ventas, transacciones de punto de venta (POS) y documentos tributarios.

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 3.2.x
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **BD**: PostgreSQL (Esquema: `sales`)

## APIs & Contratos
- **Ventas**: `POST /api/v1/sales`
- **Cierre de Caja**: `POST /api/v1/sales/cash-shifts`
- **Documentos**: `GET /api/v1/sales/documents`
- **Swagger**: `http://localhost:8083/swagger-ui.html`

## Interrelaciones
- **SAGA**: Inicia la transacción publicando en `sale-events`.
- **SAGA**: Escucha respuestas en `stock-events` para completar o cancelar la venta.
- **Service Registry**: Se registra en `siga-registry` (Eureka).

## Arquitectura
- [x] Hexagonal
- [x] UUID v4 (Ley 21.719)
- [x] Idempotencia (Tabla: `processed_events`)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

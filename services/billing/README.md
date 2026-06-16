# Billing Service (siga-billing)

*Read this in other languages: [![English](README.en.md)](README.en.md)*

Servicio de facturación corporativa para la plataforma SaaS **SIGA** (B2B).
Implementa el paso 4 del flujo SAGA: al completarse una venta en `siga-sales`, genera automáticamente una factura (`SaleInvoice`) en el esquema `billing`.

## Stack Tecnológico
- **Lenguaje**: Kotlin 2.2.0
- **Framework**: Spring Boot 3.4.3
- **BD**: PostgreSQL (Esquema: `billing`)
- **Mensajería**: Apache Kafka (SAGA Coreografía)
- **Migraciones**: Flyway
- **Service Discovery**: Eureka Client

## SAGA — Flujo de Eventos

| Paso | Origen | Evento / Topic | Destino | Estado |
|------|--------|----------------|---------|--------|
| 1 | Sales → Kafka | `sale-events` | Inventory | ✅ |
| 2 | Inventory → Kafka | `stock-events` | Sales | ✅ |
| 3 | Sales → Kafka | `sale-completed` | Billing | ✅ |
| 4 | Billing (consumer) | `SaleInvoice` → `billing.sale_invoices` | PostgreSQL | ✅ |

**Topic Kafka consumido**: `sale-completed`  
**Evento**: `SaleCompletedEvent` (producido por Sales, consumido por BillingInvoiceConsumer)  
**Idempotencia**: Tabla `processed_events` en esquema `billing` (evita facturación duplicada)

## Dominio — SaleInvoice

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `id` | UUID v4 | Identificador único |
| `saleId` | UUID v4 | Venta origen |
| `storeId` | UUID v4 | Tienda |
| `userId` | UUID v4 | Usuario que realizó la venta |
| `total` | BigDecimal | Monto total |
| `status` | String | Estado (PENDING, PAID, CANCELED) |
| `createdAt` | Instant | Fecha de creación |
| `updatedAt` | Instant | Última modificación |

## Migraciones Flyway

| Archivo | Descripción |
|---------|-------------|
| `V1__billing_init.sql` | Tabla `billing_user` |
| `V2__billing_add_customers.sql` | Tabla `customer` |
| `V3__billing_add_sale_invoices.sql` | Tabla `sale_invoices` con FK a `sales.sales` |

> **Nota**: Spring Boot 3.4.3 requiere `spring-boot-starter-flyway` + `flyway-database-postgresql`.
> Las migraciones usan schema prefix (`billing.`) para que las tablas se creen con el owner correcto del servicio.

## APIs & Contratos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/billing/plans` | Listar planes |
| POST | `/api/v1/billing/subscriptions` | Crear suscripción |
| GET | `/api/v1/billing/invoices` | Facturas SIGA |
| GET | `/api/v1/billing/sale-invoices` | Facturas de venta (SAGA) |

## Interrelaciones
- **Sales Service**: Consume `SaleCompletedEvent` vía Kafka (topic: `sale-completed`)
- **Admin Portal**: Provee datos financieros para el Backoffice
- **Service Registry**: Se registra en `siga-registry` (Eureka)

## Arquitectura
- [x] Hexagonal (Domain / Application / Infrastructure / Controller)
- [x] UUID v4 (Ley 21.719)
- [x] SAGA Coreografía (paso 4 completado — Sale → Invoice)
- [x] Idempotencia (`processed_events`)
- [x] Flyway (migraciones versionadas)

---
> "La salud financiera de la plataforma."

# Billing Service (siga-billing)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Corporate billing service for the **SIGA** SaaS (B2B) platform.
Implements SAGA step 4: when a sale is completed in `siga-sales`, it automatically generates an invoice (`SaleInvoice`) in the `billing` schema.

## Tech Stack
- **Language**: Kotlin 2.2.0
- **Framework**: Spring Boot 4.0.6
- **DB**: PostgreSQL (Schema: `billing`)
- **Messaging**: Apache Kafka (SAGA Choreography)
- **Migrations**: Flyway
- **Service Discovery**: Eureka Client

## SAGA — Event Flow

| Step | Origin | Event / Topic | Destination | Status |
|------|--------|---------------|-------------|--------|
| 1 | Sales → Kafka | `sale-events` | Inventory | ✅ |
| 2 | Inventory → Kafka | `stock-events` | Sales | ✅ |
| 3 | Sales → Kafka | `sale-completed` | Billing | ✅ |
| 4 | Billing (consumer) | `SaleInvoice` → `billing.sale_invoices` | PostgreSQL | ✅ |

**Kafka Topic consumed**: `sale-completed`
**Event**: `SaleCompletedEvent` (produced by Sales, consumed by BillingInvoiceConsumer)
**Idempotency**: `processed_events` table in `billing` schema (prevents duplicate invoicing)

## Domain — SaleInvoice

| Attribute | Type | Description |
|-----------|------|-------------|
| `id` | UUID v4 | Unique identifier |
| `saleId` | UUID v4 | Source sale |
| `storeId` | UUID v4 | Store |
| `userId` | UUID v4 | User who made the sale |
| `total` | BigDecimal | Total amount |
| `status` | String | Status (PENDING, PAID, CANCELED) |
| `createdAt` | Instant | Creation date |
| `updatedAt` | Instant | Last modification |

## Flyway Migrations

| File | Description |
|------|-------------|
| `V1__billing_init.sql` | `billing_user` table |
| `V2__billing_add_customers.sql` | `customer` table |
| `V3__billing_add_sale_invoices.sql` | `sale_invoices` table with FK to `sales.sales` |

> **Note**: Spring Boot 4.0.6 requires `spring-boot-starter-flyway` + `flyway-database-postgresql`.
> Migrations use schema prefix (`billing.`) so tables are created with the correct service owner.

## APIs & Contracts

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/billing/plans` | List plans |
| POST | `/api/v1/billing/subscriptions` | Create subscription |
| GET | `/api/v1/billing/invoices` | SIGA invoices |
| GET | `/api/v1/billing/sale-invoices` | Sale invoices (SAGA) |

## Interconnections
- **Sales Service**: Consumes `SaleCompletedEvent` via Kafka (topic: `sale-completed`)
- **Admin Portal**: Provides financial data for the Backoffice
- **Service Registry**: Registers with `siga-registry` (Eureka)

## Architecture
- [x] Hexagonal (Domain / Application / Infrastructure / Controller)
- [x] UUID v4 (Law 21.719)
- [x] SAGA Choreography (step 4 completed — Sale → Invoice)
- [x] Idempotency (`processed_events`)
- [x] Flyway (versioned migrations)

---
> "La salud financiera de la plataforma."

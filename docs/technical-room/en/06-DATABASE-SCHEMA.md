# 06. Database Schema (Distributed Persistence)

Describes the physical and logical data structure of SIGA, organized under the **One Database per Service** pattern.

[🇪🇸 Ver Versión en Español](../es/06-DATABASE-SCHEMA.mdx)

## 1. Management Strategy (Flyway)

Instead of manual and centralized scripts, SIGA uses **Flyway** for database version control.
*   **Location**: `src/main/resources/db/migration/` in each microservice.
*   **Execution**: Automatic upon service startup.
*   **Visibility**: Tables are created when the container starts, allowing immediate inspection in pgAdmin.

---

## 2. Stateless Modules (No Physical Persistence)

The following modules **DO NOT** require their own database, as they operate in memory or act as routers:
*   **siga-registry (Eureka)**: Dynamic service directory (Service Discovery).
*   **siga-gateway**: Request router and load balancer.

---

## 3. Data Dictionary per Service

### A. Database: `siga_auth`
Responsible for security, identity, and multi-tenancy.

| Table | Attribute | Type | Description |
|-------|----------|------|-------------|
| **users** | `id` | UUID (PK) | Unique identifier |
| | `email` | VARCHAR(255) | Unique, for login |
| | `password_hash` | VARCHAR(255) | BCrypt Hash |
| | `role` | ENUM | ADMIN, OPERATOR, OWNER |
| | `tenant_id` | UUID (FK) | Relationship with the company |
| **tenants** | `id` | UUID (PK) | Company (Tenant) identifier |
| | `name` | VARCHAR(100) | Commercial name |
| | `plan` | ENUM | FREE, PRO, ENTERPRISE |

### B. Database: `siga_inventory`
Asset and stock management.

| Table | Attribute | Type | Description |
|-------|----------|------|-------------|
| **products** | `id` | UUID (PK) | Global product ID |
| | `sku` | VARCHAR(50) | Unique stock code |
| | `name` | VARCHAR(255) | Descriptive name |
| | `base_price` | DECIMAL | Price without taxes |
| | `tenant_id` | UUID | Multi-tenant discriminator |

### C. Database: `siga_sales`
Transactions, POS, and cash shifts.

| Table | Attribute | Type | Description |
|-------|----------|------|-------------|
| **sales** | `id` | UUID (PK) | Unique transaction ID |
| | `total` | DECIMAL | Total sale amount |
| | `created_at` | TIMESTAMPTZ | Universal timestamp |

---

## 4. Physical Isolation Principles

1.  **No Foreign Keys**: Physical Foreign Keys across different databases are prohibited. Integrity is logical (UUID).
2.  **Independent Backups**: Each database can be backed up or restored without affecting others.
3.  **Horizontal Scalability**: Each database can be hosted on a different physical server if necessary.

# SIGA - Persistence Architecture Master Reference

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../es/ARQUITECTURA_PERSISTENCIA.md)*

This document serves as the definitive technical reference for the SIGA project's distributed persistence system. It details the transition from a monolithic schema to a **High-Availability Distributed Architecture** with physical data isolation.

## 1. Architectural Pillars

### 1.1. Physical Isolation
Each core microservice manages its own independent PostgreSQL database. Cross-database queries or physical Foreign Keys are strictly prohibited to ensure maximum decoupling and scalability. Logical references across services use UUID values without FK constraints.

### 1.2. Technology Stack
*   **Engine**: PostgreSQL 16+ with `pgvector` for AI embeddings
*   **Migration Engine**: Flyway (Versioned scripts with `V1__` prefix, `baseline-on-migrate: true`)
*   **ID Standard**: UUID v4 (Standardized for global consistency, generated via `uuid_generate_v4()`)
*   **Time Standard**: TIMESTAMPTZ (UTC-based universal time)
*   **Init Scripts**: `scripts/db-init/backup_sql/*.sql` — standalone SQL copies matching Flyway V1 exactly

---

## 2. Service-Database Mapping

| Service | Database Name | Port (Docker) | Role |
|---------|---------------|---------------|------|
| **auth** | `siga_auth` | 5433 | Identity, Multi-tenancy & Security |
| **inventory** | `siga_inventory` | 5434 | Catalog, Stock & Warehouse Logic |
| **sales** | `siga_sales` | 5435 | Real-time POS Transactions & Shifts |
| **billing** | `siga_billing` | 5436 | Subscriptions, Plans & Invoicing |
| **agent** | `siga_agent` | 5437 | AI, RAG & Vector Embeddings |

---

## 3. Data Dictionary (Consolidated)

### A. siga_auth (schema: `auth`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| **users** | Employee/operator identities for a business | `id (UUID)`, `email`, `password_hash`, `role`, `commercial_user_id (Int, logical ref to Billing Customer)` |
| **customers** | Business owners / tenants that register via SSO | `id (SERIAL)`, `email`, `name`, `company_name`, `plan_id (Int)`, `is_on_trial` |
| **permissions** | Fine-grained access control codes | `id (UUID)`, `code`, `name`, `category` |
| **role_permissions** | Which permissions each role has | `role (VARCHAR)`, `permission_id (UUID)` |
| **user_permissions** | Individual user permission overrides | `user_id (UUID)`, `permission_id (UUID)`, `assigned_by (UUID)` |
| **user_stores** | Which stores a user can access | `user_id (UUID)`, `store_id (UUID, logical ref to Inventory)` |

### B. siga_inventory (schema: `inventory`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| **categories** | Product classification | `id (UUID)`, `name`, `commercial_user_id (UUID)`, unique per business |
| **stores** | Physical branch / warehouse locations | `id (UUID)`, `name`, `address`, `city`, `commercial_user_id (UUID)` |
| **products** | Global product catalog per business | `id (UUID)`, `name`, `barcode`, `unit_price`, `category_id (UUID)`, `commercial_user_id (UUID)` |
| **stock** | Real-time stock per product+store | `product_id (UUID)`, `store_id (UUID)`, `quantity`, `minimum_quantity` |
| **movements** | Stock change audit trail (Kardex) | `product_id (UUID)`, `store_id (UUID)`, `type`, `previous_quantity`, `new_quantity`, `user_id (UUID)` |
| **alerts** | Low-stock / anomaly notifications | `type`, `product_id (UUID)`, `store_id (UUID)`, `message`, `is_read` |
| **processed_events** | Kafka idempotency guard | `event_id (UUID) PK`, `event_type`, `processed_at` |

### C. siga_sales (schema: `sales`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| **sales** | POS transaction headers (SAGA pattern) | `id (UUID)`, `store_id (UUID)`, `user_id (UUID)`, `total`, `status`, `commercial_user_id (Int)` |
| **sale_items** | Line items of a sale | `id (UUID)`, `sale_id (UUID)`, `product_id (UUID)`, `quantity`, `unit_price`, `subtotal` |
| **cash_shifts** | POS session (cash drawer open/close) | `id (UUID)`, `store_id (UUID)`, `user_id (UUID)`, `initial_amount`, `final_amount`, `status` |
| **pos_transactions** | Payment records within a shift | `id (UUID)`, `sale_id (UUID)`, `shift_id (UUID)`, `payment_method_id (UUID)`, `amount`, `last_4_digits` |
| **pos_cart** | Temporary cart items per user | `sale_id (UUID)`, `product_id (UUID)`, `quantity`, `store_id (UUID)`, `user_id (UUID)` |
| **payment_methods** | Supported payment types | `id (UUID)`, `name`, `is_active` |
| **customers** | End clients of the SME (for Factura/Boleta) | `id (UUID)`, `tax_id (RUT)`, `name`, `email`, `phone`, `address` |
| **sale_documents** | Legal tax documents (DTE: Boleta/Factura) | `sale_id (UUID)`, `customer_id (UUID)`, `type`, `folio`, `total_amount`, `tax_amount`, `status` |
| **processed_events** | Kafka idempotency guard | `event_id (UUID) PK`, `event_type`, `processed_at` |

### D. siga_billing (schema: `billing`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| **plans** | Subscription tier definitions | `id (UUID)`, `name`, `monthly_price`, `store_limit`, `user_limit`, `product_limit` |
| **customers** | Subscribers (business owners with payment data) | `id (UUID)`, `email`, `name`, `company_name`, `plan_id (UUID)`, `is_on_trial`, `tax_id` |
| **subscriptions** | Active / historical subscriptions | `id (UUID)`, `customer_id (UUID)`, `plan_id (UUID)`, `billing_period`, `status`, `starts_at`, `ends_at` |
| **invoices** | Invoice records for subscription payments | `id (UUID)`, `invoice_number`, `customer_id (UUID)`, `plan_id (UUID)`, `price_uf`, `price_clp`, `status` |
| **payments** | Payment transactions | `id (UUID)`, `subscription_id (UUID)`, `customer_id (UUID)`, `amount`, `status`, `reference` |
| **shopping_carts** | Plan selection cart before checkout | `id (UUID)`, `customer_id (UUID)`, `plan_id (UUID)`, `billing_period` |
| **sale_invoices** | Sales invoices generated by SAGA (Sales → Billing) | `id (UUID)`, `sale_id (UUID, logical ref to Sales)`, `store_id (UUID)`, `user_id (UUID)`, `total (DECIMAL)`, `status (VARCHAR)`, `created_at`, `updated_at` |
| **processed_events** | Kafka event idempotency guard | `event_id (UUID) PK`, `event_type`, `processed_at` |

### E. siga_agent (schema: `agent`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| **conversations** | AI-User chat history per session | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `session_id (UUID)`, `context (JSONB)` |
| **documents** | RAG source documents with vector embeddings | `id (UUID)`, `title`, `content`, `embedding (VECTOR(1536))`, `tenant_id (UUID)` |
| **intent_logs** | NLP intent classification history | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `query_text`, `detected_intent`, `confidence_score` |
| **intent_permissions** | Which intents each plan allows | `id (SERIAL)`, `plan_name`, `intent`, `is_allowed` |
| **pending_actions** | Async tasks awaiting user confirmation | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `intent`, `action_data (JSONB)`, `status`, `expires_at` |

---

## 4. Cross-Service Logical References

Since physical FKs across databases are prohibited, references are maintained at the application layer:

| Source | Column | Target | Type |
|--------|--------|--------|------|
| Auth `users.commercial_user_id` | INTEGER | Billing `customers.id` (UUID) | Legacy — planned migration |
| Auth `customers.plan_id` | INTEGER | Billing `plans.id` (UUID) | Legacy — planned migration |
| Inventory `commercial_user_id` | UUID | Billing `customers.id` (UUID) | Current standard |
| Sales `sales.commercial_user_id` | INTEGER | Billing `customers.id` (UUID) | Legacy — planned migration |
| Sales `sale_documents.customer_id` | UUID | Sales `customers.id` (UUID) | Same DB (with FK) |
| Sales `user_id` columns | UUID | Auth `users.id` (UUID) | Logical ref |
| Sales `store_id` columns | UUID | Inventory `stores.id` (UUID) | Logical ref |

---

## 5. Engineering Standards

*   **Logic Integrity**: As there are no physical FKs across DBs, the application layer must ensure data consistency using Saga patterns (Kafka choreography) or compensation logic.
*   **Auditing**: All tables must include `created_at` and `updated_at` as `TIMESTAMPTZ`.
*   **Pagination**: Use UUID-based keyset pagination for high-volume endpoints.
*   **Migration Safety**: Flyway V1 uses `CREATE TABLE IF NOT EXISTS` for idempotency. After V1, all schema changes are additive (ALTER TABLE ADD COLUMN, CREATE INDEX).
*   **ID Generation**: UUID v4 via PostgreSQL `uuid_generate_v4()` default. The application layer does NOT generate IDs — this prevents Hibernate 6 StaleObjectStateException with `@GeneratedValue`.

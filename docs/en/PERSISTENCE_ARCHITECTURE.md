# SIGA - Persistence Architecture Master Reference

This document serves as the definitive technical reference for the SIGA project's distributed persistence system. It details the transition from a monolithic schema to a **High-Availability Distributed Architecture** with physical data isolation.

## 1. Architectural Pillars

### 1.1. Physical Isolation
Each core microservice manages its own independent PostgreSQL database. Cross-database queries or physical Foreign Keys are strictly prohibited to ensure maximum decoupling and scalability.

### 1.2. Technology Stack
*   **Engine**: PostgreSQL 16+
*   **Migration Engine**: Flyway (Versioned scripts with `V1__` prefix)
*   **ID Standard**: UUID v4 (Standardized for global consistency)
*   **Time Standard**: TIMESTAMPTZ (UTC-based universal time)

---

## 2. Service-Database Mapping

| Service | Database Name | Port (Docker) | Role |
|---------|---------------|---------------|------|
| **auth** | `siga_auth` | 5433 | Identity, Multi-tenancy & Security |
| **inventory** | `siga_inventory` | 5434 | Catalog, Stock & Warehouse Logic |
| **sales** | `siga_sales` | 5435 | Real-time POS Transactions & Shifts |
| **billing** | `siga_billing` | 5436 | DTE, Invoicing & Subscription Plans |
| **agent** | `siga_agent` | 5437 | AI, RAG & Vector Embeddings |

---

## 3. Data Dictionary (Consolidated)

### A. siga_auth
| Table | Description | Key Columns |
|-------|-------------|-------------|
| **users** | Identity management | `id (UUID)`, `email`, `password_hash`, `role` |
| **tenants** | Multi-tenant companies | `id (UUID)`, `name`, `tax_id`, `plan` |
| **permissions** | Granular access control | `id (UUID)`, `code`, `description` |
| **role_permissions** | Role-Permission mapping | `role_id`, `permission_id` |
| **user_stores** | User-to-Branch access | `user_id`, `store_id` |

### B. siga_inventory
| Table | Description | Key Columns |
|-------|-------------|-------------|
| **products** | Global product catalog | `id (UUID)`, `sku`, `name`, `base_price` |
| **categories** | Product grouping | `id (UUID)`, `name`, `parent_id` |
| **stocks** | Real-time availability | `product_id`, `store_id`, `quantity` |
| **movements** | Stock audit trail | `id (UUID)`, `type`, `qty`, `timestamp` |
| **stores** | Physical branch locations | `id (UUID)`, `name`, `address` |
| **alerts** | Low stock notifications | `id (UUID)`, `product_id`, `threshold` |

### C. siga_sales
| Table | Description | Key Columns |
|-------|-------------|-------------|
| **sales** | Transaction headers | `id (UUID)`, `total`, `status`, `seller_id` |
| **sale_items** | Line item details | `id (UUID)`, `sale_id`, `product_id`, `subtotal` |
| **cash_shifts** | POS Session management | `id (UUID)`, `user_id`, `opening_balance` |
| **pos_cart** | Persistent session carts | `user_id`, `product_id`, `qty` |
| **payments** | Transaction payment info | `id (UUID)`, `sale_id`, `method`, `amount` |
| **payment_methods** | Supported methods | `id (UUID)`, `code`, `name` |

### D. siga_billing
| Table | Description | Key Columns |
|-------|-------------|-------------|
| **customers** | Billing entities | `id (UUID)`, `tax_id`, `name`, `email` |
| **invoices** | Legal tax documents (DTE) | `id (UUID)`, `folio`, `type`, `sale_id` |
| **payments** | Invoice payment tracking | `id (UUID)`, `invoice_id`, `amount` |
| **plans** | Subscription tier definitions | `id (UUID)`, `name`, `price` |
| **subscriptions** | Active tenant billing | `id (UUID)`, `tenant_id`, `plan_id` |

### E. siga_agent
| Table | Description | Key Columns |
|-------|-------------|-------------|
| **conversations** | AI-User chat history | `id (UUID)`, `user_id`, `thread_id` |
| **documents** | RAG source documents | `id (UUID)`, `title`, `vector_content` |
| **intent_logs** | NLP classification logs | `id (UUID)`, `raw_text`, `detected_intent` |
| **intent_permissions** | AI action restrictions | `intent_code`, `required_role` |
| **pending_actions** | Async tasks for AI | `id (UUID)`, `action_type`, `payload` |

---

## 4. Engineering Standards

*   **Logic Integrity**: As there are no physical FKs across DBs, the application layer must ensure data consistency using Saga patterns or Compensation logic.
*   **Auditing**: All tables must include `created_at` and `updated_at` as `TIMESTAMPTZ`.
*   **Pagination**: Use UUID-based keyset pagination for high-volume endpoints.

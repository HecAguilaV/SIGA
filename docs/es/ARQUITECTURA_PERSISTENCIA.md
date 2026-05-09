# SIGA - Referencia Maestra de Arquitectura de Persistencia

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../en/PERSISTENCE_ARCHITECTURE.md)*

Este documento sirve como la referencia técnica definitiva para el sistema de persistencia distribuida del proyecto SIGA. Detalla la transición de un esquema monolítico a una **Arquitectura Distribuida de Alta Disponibilidad** con aislamiento físico de datos.

## 1. Pilares Arquitectónicos

### 1.1. Aislamiento Físico
Cada microservicio core gestiona su propia base de datos PostgreSQL independiente. Las consultas cruzadas entre bases de datos o las Claves Foráneas (FK) físicas están estrictamente prohibidas para garantizar el máximo desacoplamiento y escalabilidad. Las referencias lógicas entre servicios usan valores UUID sin restricciones FK.

### 1.2. Stack Tecnológico
*   **Motor**: PostgreSQL 16+ con `pgvector` para embeddings de IA
*   **Motor de Migraciones**: Flyway (Scripts versionados con prefijo `V1__`, `baseline-on-migrate: true`)
*   **Estándar de IDs**: UUID v4 (Estandarizado para consistencia global, generado via `uuid_generate_v4()`)
*   **Estándar de Tiempo**: TIMESTAMPTZ (Tiempo universal basado en UTC)
*   **Scripts de Inicio**: `scripts/db-init/backup_sql/*.sql` — copias SQL independientes que coinciden exactamente con Flyway V1

---

## 2. Mapeo Servicio-Base de Datos

| Servicio | Nombre de BD | Puerto (Docker) | Rol Principal |
|----------|--------------|-----------------|---------------|
| **auth** | `siga_auth` | 5433 | Identidad, Multi-tenencia y Seguridad |
| **inventory** | `siga_inventory` | 5434 | Catálogo, Stock y Lógica de Bodega |
| **sales** | `siga_sales` | 5435 | Transacciones POS en Tiempo Real y Turnos |
| **billing** | `siga_billing` | 5436 | Suscripciones, Planes y Facturación |
| **agent** | `siga_agent` | 5437 | IA, RAG y Embeddings Vectoriales |

---

## 3. Diccionario de Datos (Consolidado)

### A. siga_auth (schema: `auth`)

| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **users** | Identidades de empleados/operadores de una empresa | `id (UUID)`, `email`, `password_hash`, `role`, `commercial_user_id (Int, ref. lógica a Billing Customer)` |
| **customers** | Dueños de empresa / tenants que se registran via SSO | `id (SERIAL)`, `email`, `name`, `company_name`, `plan_id (Int)`, `is_on_trial` |
| **permissions** | Códigos de control de acceso granular | `id (UUID)`, `code`, `name`, `category` |
| **role_permissions** | Qué permisos tiene cada rol | `role (VARCHAR)`, `permission_id (UUID)` |
| **user_permissions** | Sobrescrituras de permisos por usuario | `user_id (UUID)`, `permission_id (UUID)`, `assigned_by (UUID)` |
| **user_stores** | A qué sucursales tiene acceso un usuario | `user_id (UUID)`, `store_id (UUID, ref. lógica a Inventory)` |

### B. siga_inventory (schema: `inventory`)

| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **categories** | Clasificación de productos | `id (UUID)`, `name`, `commercial_user_id (UUID)`, único por negocio |
| **stores** | Sucursales / bodegas físicas | `id (UUID)`, `name`, `address`, `city`, `commercial_user_id (UUID)` |
| **products** | Catálogo global de productos por negocio | `id (UUID)`, `name`, `barcode`, `unit_price`, `category_id (UUID)`, `commercial_user_id (UUID)` |
| **stock** | Stock en tiempo real por producto+sucursal | `product_id (UUID)`, `store_id (UUID)`, `quantity`, `minimum_quantity` |
| **movements** | Auditoría de cambios de stock (Kardex) | `product_id (UUID)`, `store_id (UUID)`, `type`, `previous_quantity`, `new_quantity`, `user_id (UUID)` |
| **alerts** | Notificaciones de stock bajo / anomalías | `type`, `product_id (UUID)`, `store_id (UUID)`, `message`, `is_read` |
| **processed_events** | Guardia de idempotencia para Kafka | `event_id (UUID) PK`, `event_type`, `processed_at` |

### C. siga_sales (schema: `sales`)

| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **sales** | Cabeceras de transacciones POS (patrón SAGA) | `id (UUID)`, `store_id (UUID)`, `user_id (UUID)`, `total`, `status`, `commercial_user_id (Int)` |
| **sale_items** | Detalle de líneas de venta | `id (UUID)`, `sale_id (UUID)`, `product_id (UUID)`, `quantity`, `unit_price`, `subtotal` |
| **cash_shifts** | Sesiones POS (apertura/cierre de caja) | `id (UUID)`, `store_id (UUID)`, `user_id (UUID)`, `initial_amount`, `final_amount`, `status` |
| **pos_transactions** | Registros de pago dentro de un turno | `id (UUID)`, `sale_id (UUID)`, `shift_id (UUID)`, `payment_method_id (UUID)`, `amount`, `last_4_digits` |
| **pos_cart** | Carritos temporales por usuario | `sale_id (UUID)`, `product_id (UUID)`, `quantity`, `store_id (UUID)`, `user_id (UUID)` |
| **payment_methods** | Tipos de pago soportados | `id (UUID)`, `name`, `is_active` |
| **customers** | Clientes finales de la PyME (para Factura/Boleta) | `id (UUID)`, `tax_id (RUT)`, `name`, `email`, `phone`, `address` |
| **sale_documents** | Documentos tributarios (DTE: Boleta/Factura) | `sale_id (UUID)`, `customer_id (UUID)`, `type`, `folio`, `total_amount`, `tax_amount`, `status` |
| **processed_events** | Guardia de idempotencia para Kafka | `event_id (UUID) PK`, `event_type`, `processed_at` |

### D. siga_billing (schema: `billing`)

| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **plans** | Definición de niveles de suscripción | `id (UUID)`, `name`, `monthly_price`, `store_limit`, `user_limit`, `product_limit` |
| **customers** | Suscriptores (dueños de empresa con datos de pago) | `id (UUID)`, `email`, `name`, `company_name`, `plan_id (UUID)`, `is_on_trial`, `tax_id` |
| **subscriptions** | Suscripciones activas / históricas | `id (UUID)`, `customer_id (UUID)`, `plan_id (UUID)`, `billing_period`, `status`, `starts_at`, `ends_at` |
| **invoices** | Registros de facturas por suscripción | `id (UUID)`, `invoice_number`, `customer_id (UUID)`, `plan_id (UUID)`, `price_uf`, `price_clp`, `status` |
| **payments** | Transacciones de pago | `id (UUID)`, `subscription_id (UUID)`, `customer_id (UUID)`, `amount`, `status`, `reference` |
| **shopping_carts** | Carrito de selección de plan previo al checkout | `id (UUID)`, `customer_id (UUID)`, `plan_id (UUID)`, `billing_period` |
| **sale_invoices** | Facturas de venta generadas por SAGA (Sales → Billing) | `id (UUID)`, `sale_id (UUID, ref. lógica a Sales)`, `store_id (UUID)`, `user_id (UUID)`, `total (DECIMAL)`, `status (VARCHAR)`, `created_at`, `updated_at` |
| **processed_events** | Guardia de idempotencia para eventos Kafka | `event_id (UUID) PK`, `event_type`, `processed_at` |

### E. siga_agent (schema: `agent`)

| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **conversations** | Historial de chat IA-Usuario por sesión | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `session_id (UUID)`, `context (JSONB)` |
| **documents** | Documentos fuente para RAG con embeddings | `id (UUID)`, `title`, `content`, `embedding (VECTOR(1536))`, `tenant_id (UUID)` |
| **intent_logs** | Historial de clasificación NLP de intenciones | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `query_text`, `detected_intent`, `confidence_score` |
| **intent_permissions** | Qué intenciones permite cada plan | `id (SERIAL)`, `plan_name`, `intent`, `is_allowed` |
| **pending_actions** | Tareas asíncronas que esperan confirmación del usuario | `id (UUID)`, `tenant_id (UUID)`, `user_id (UUID)`, `intent`, `action_data (JSONB)`, `status`, `expires_at` |

---

## 4. Referencias Lógicas entre Servicios

Como no existen FKs físicas entre bases de datos, las referencias se mantienen en la capa de aplicación:

| Origen | Columna | Destino | Tipo |
|--------|---------|---------|------|
| Auth `users.commercial_user_id` | INTEGER | Billing `customers.id` (UUID) | Legado — migración planificada |
| Auth `customers.plan_id` | INTEGER | Billing `plans.id` (UUID) | Legado — migración planificada |
| Inventory `commercial_user_id` | UUID | Billing `customers.id` (UUID) | Estándar actual |
| Sales `sales.commercial_user_id` | INTEGER | Billing `customers.id` (UUID) | Legado — migración planificada |
| Sales `sale_documents.customer_id` | UUID | Sales `customers.id` (UUID) | Misma BD (con FK) |
| Sales `user_id` columns | UUID | Auth `users.id` (UUID) | Ref. lógica |
| Sales `store_id` columns | UUID | Inventory `stores.id` (UUID) | Ref. lógica |

---

## 5. Estándares de Ingeniería

*   **Integridad Lógica**: Al no existir FKs físicas entre bases de datos, la capa de aplicación debe garantizar la consistencia usando patrones Saga (coreografía Kafka) o lógica de compensación.
*   **Auditoría**: Todas las tablas deben incluir `created_at` y `updated_at` como `TIMESTAMPTZ`.
*   **Paginación**: Utilizar paginación basada en claves (keyset pagination) con UUID para endpoints de alto volumen.
*   **Seguridad de Migraciones**: Flyway V1 usa `CREATE TABLE IF NOT EXISTS` para idempotencia. Después de V1, todos los cambios de schema son aditivos (ALTER TABLE ADD COLUMN, CREATE INDEX).
*   **Generación de IDs**: UUID v4 via `uuid_generate_v4()` de PostgreSQL. La capa de aplicación NO genera IDs — esto previene StaleObjectStateException de Hibernate 6 con `@GeneratedValue`.

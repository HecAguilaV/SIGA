# SIGA - Referencia Maestra de Arquitectura de Persistencia

Este documento sirve como la referencia técnica definitiva para el sistema de persistencia distribuida del proyecto SIGA. Detalla la transición de un esquema monolítico a una **Arquitectura Distribuida de Alta Disponibilidad** con aislamiento físico de datos.

## 1. Pilares Arquitectónicos

### 1.1. Aislamiento Físico
Cada microservicio core gestiona su propia base de datos PostgreSQL independiente. Las consultas cruzadas entre bases de datos o las Claves Foráneas (FK) físicas están estrictamente prohibidas para garantizar el máximo desacoplamiento y escalabilidad.

### 1.2. Stack Tecnológico
*   **Motor**: PostgreSQL 16+
*   **Motor de Migraciones**: Flyway (Scripts versionados con prefijo `V1__`)
*   **Estándar de IDs**: UUID v4 (Estandarizado para consistencia global)
*   **Estándar de Tiempo**: TIMESTAMPTZ (Tiempo universal basado en UTC)

---

## 2. Mapeo Servicio-Base de Datos

| Servicio | Nombre de BD | Puerto (Docker) | Rol Principal |
|----------|--------------|-----------------|---------------|
| **auth** | `siga_auth` | 5433 | Identidad, Multi-tenencia y Seguridad |
| **inventory** | `siga_inventory` | 5434 | Catálogo, Stock y Lógica de Bodega |
| **sales** | `siga_sales` | 5435 | Transacciones POS en Tiempo Real y Turnos |
| **billing** | `siga_billing` | 5436 | DTE, Facturación y Planes de Suscripción |
| **agent** | `siga_agent` | 5437 | IA, RAG y Embeddings Vectoriales |

---

## 3. Diccionario de Datos (Consolidado)

### A. siga_auth
| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **users** | Gestión de identidad | `id (UUID)`, `email`, `password_hash`, `role` |
| **tenants** | Empresas multi-tenant | `id (UUID)`, `name`, `tax_id`, `plan` |
| **permissions** | Control de acceso granular | `id (UUID)`, `code`, `description` |
| **role_permissions** | Mapeo Rol-Permiso | `role_id`, `permission_id` |
| **user_stores** | Acceso de Usuario a Sucursal | `user_id`, `store_id` |

### B. siga_inventory
| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **products** | Catálogo global de productos | `id (UUID)`, `sku`, `name`, `base_price` |
| **categories** | Agrupación de productos | `id (UUID)`, `name`, `parent_id` |
| **stocks** | Disponibilidad en tiempo real | `product_id`, `store_id`, `quantity` |
| **movements** | Auditoría de movimientos de stock | `id (UUID)`, `type`, `qty`, `timestamp` |
| **stores** | Sucursales y ubicaciones físicas | `id (UUID)`, `name`, `address` |
| **alerts** | Notificaciones de stock bajo | `id (UUID)`, `product_id`, `threshold` |

### C. siga_sales
| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **sales** | Cabeceras de transacciones | `id (UUID)`, `total`, `status`, `seller_id` |
| **sale_items** | Detalle de líneas de venta | `id (UUID)`, `sale_id`, `product_id`, `subtotal` |
| **cash_shifts** | Gestión de sesiones POS (Cajas) | `id (UUID)`, `user_id`, `opening_balance` |
| **pos_cart** | Carritos de sesión persistentes | `user_id`, `product_id`, `qty` |
| **payments** | Información de pagos | `id (UUID)`, `sale_id`, `method`, `amount` |
| **payment_methods** | Métodos de pago soportados | `id (UUID)`, `code`, `name` |

### D. siga_billing
| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **customers** | Entidades de facturación | `id (UUID)`, `tax_id`, `name`, `email` |
| **invoices** | Documentos tributarios (DTE) | `id (UUID)`, `folio`, `type`, `sale_id` |
| **payments** | Seguimiento de pagos de facturas | `id (UUID)`, `invoice_id`, `amount` |
| **plans** | Definición de niveles de suscripción | `id (UUID)`, `name`, `price` |
| **subscriptions** | Facturación activa por tenant | `id (UUID)`, `tenant_id`, `plan_id` |

### E. siga_agent
| Tabla | Descripción | Columnas Clave |
|-------|-------------|----------------|
| **conversations** | Historial de chat IA-Usuario | `id (UUID)`, `user_id`, `thread_id` |
| **documents** | Documentos fuente para RAG | `id (UUID)`, `title`, `vector_content` |
| **intent_logs** | Logs de clasificación NLP | `id (UUID)`, `raw_text`, `detected_intent` |
| **intent_permissions** | Restricciones de acciones IA | `intent_code`, `required_role` |
| **pending_actions** | Tareas asíncronas para la IA | `id (UUID)`, `action_type`, `payload` |

---

## 4. Estándares de Ingeniería

*   **Integridad Lógica**: Al no existir FKs físicas entre bases de datos, la capa de aplicación debe garantizar la consistencia usando patrones Saga o lógica de compensación.
*   **Auditoría**: Todas las tablas deben incluir `created_at` y `updated_at` como `TIMESTAMPTZ`.
*   **Paginación**: Utilizar paginación basada en claves (keyset pagination) con UUID para endpoints de alto volumen.

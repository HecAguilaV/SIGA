# Estado de la Arquitectura - SIGA

**Última Actualización:** 2026-05-26
**Estado:** Hexagonal Completo (100% microservicios migrados)

## 1. Modelo de Despliegue
El sistema ha completado su transición de Monolito Modular a una arquitectura de **Microservicios Independientes con Hexagonal Architecture**.

### Componentes Core
*   **siga-auth**: Gestión de identidad y permisos (SSO). ✅ Hexagonal
*   **siga-billing**: Gestión de suscripciones y pagos del **SaaS de SIGA** (NO incluye facturación de ventas PYME). ✅ Hexagonal
*   **siga-inventory**: Control de stock y activos. ✅ Hexagonal
*   **siga-sales**: POS y registro de ventas de la PYME (Dominio Sales). ✅ Hexagonal
*   **siga-agent**: Inteligencia Artificial y búsqueda vectorial (pgvector).

### Frontend — Única App Activa

> **Decisión consolidada (26/05/2026)**: SIGA tiene **UNA SOLA** aplicación frontend activa: `apps/dashboard/` (SvelteKit 5). Funciona como BFF nativo con server-side data composition (`+page.server.ts` load functions).

No existen más frontends. Los directorios legacy (`apps/admin-portal`, `apps/customer-portal`, `apps/landing`, `apps/mobile`, `apps/pos`) fueron eliminados del repositorio — contenían solo READMEs que confundían. Sus funcionalidades previstas se unificarán como grupos de rutas dentro de `apps/dashboard/`.

| Grupo de rutas | Contenido | Estado |
|----------------|-----------|--------|
| `/(auth)/` | Login, logout | ✅ Implementado |
| `/(dashboard)/` | Categorías, productos, tiendas, usuarios, analytics | ✅ Implementado |
| `/(platform)/` | Admin SIGA: planes, clientes SaaS, suscripciones, monitoreo | 🚧 Pendiente |
| `/assistant` | Agente IA conversacional (A2UI) | ✅ Implementado |

## 2. Estrategia de Persistencia
Se aplica el principio de **Database per Service**. Cada microservicio es dueño absoluto de su base de datos.

| Microservicio | Base de Datos | Esquema Principal |
|---------------|---------------|-------------------|
| siga-auth     | siga_auth     | auth              |
| siga-billing  | siga_billing  | billing           |
| siga-inventory| siga_inventory| inventory         |
| siga-sales    | siga_sales    | sales             |
| siga-agent    | siga_agent    | agent             |

**Nota sobre Billing**: `siga-billing` gestiona exclusivamente la facturación SaaS de SIGA (planes, suscripciones, pagos). El modelo `SaleInvoice` dentro de billing es un registro interno generado por eventos de Sales (SAGA), sin endpoints REST ni UI — existe para futuros KPIs del cliente PYME.

## 3. Infraestructura
*   **Service Discovery**: Netflix Eureka.
*   **API Gateway**: Spring Cloud Gateway con RewritePath (mapeo `/api/*` → `/api/v1/*`), Eureka discovery locator deshabilitado.
*   **Event Broker**: **Apache Kafka** (Local) / **GCP Pub/Sub** (Cloud).
*   **Contenerización**: Docker Compose con inicialización automatizada (`init-db.sh` crea schemas + usuarios; Flyway gestiona el DDL como única fuente de verdad).
*   **Ops & Observability**: **ContainerFlow** (siga-ops) — visualizador de arquitectura Docker en tiempo real. Muestra topología de servicios, conexiones, métricas CPU/RAM, logs, terminal interactiva (`docker exec`), recomendaciones de buenas prácticas, y **notificaciones vía Discord** (cambios de estado, alertas de recursos, errores de acciones). Configuración persistente vía volumen `containerflow-data`. Puerto: `${OPS_PORT:-9470}`. [Fuente](https://github.com/RGJorge/ContainerFlow).

## 4. Patrones de Comunicación
*   **Sincrónico**: REST API vía Gateway para operaciones de lectura y comandos críticos.
*   **Asincrónico (SAGA)**: Coreografía de eventos vía Kafka para transacciones distribuidas (ej: Venta -> Stock -> Billing).
*   **Analítico**: Streaming de eventos hacia BigQuery/Vertex AI para ingesta Big Data.

## 5. Patrón Interno (Hexagonal Architecture - Implementado)
✅ **Completado**: Todos los microservicios core (auth, billing, inventory, sales) siguen **Arquitectura Hexagonal (Ports & Adapters)**.
*   **Dominio Puro**: Modelos de negocio en `domain/model/` sin dependencias JPA/Spring.
*   **Puertos**: Interfaces en `domain/port/` que definen contratos de persistencia.
*   **Adaptadores**: Implementaciones JPA en `infrastructure/adapter/` con mappers Entity ↔ Domain.
*   **Casos de Uso**: Lógica de aplicación en `application/usecase/` con validación.
*   **Controladores**: Capa de entrada HTTP que inyecta casos de uso (no repositorios).

## 6. Roles y Modelo de Jerarquía

### Estado Actual: Permisos Granulares (PBAC)
SIGA ha migrado de un control de acceso basado en roles estáticos (RBAC) a un modelo de **Permisos Granulares (Permission-Based Access Control - PBAC)** en el Dashboard.

- **Identidad**: Los usuarios mantienen un `rol` para fines descriptivos y de auditoría, pero la autorización se valida contra una lista de `permissions` enviada en el JWT.
- **Enforcement**:
    - **Servidor**: `hooks.server.ts` protege las rutas mediante el mapa `PERMISSION_GUARDS`.
    - **Interfaz**: `Sidebar.svelte` y los stores reactivos (`auth.svelte.ts`) filtran módulos y acciones dinámicamente.
- **Permisos Implementados**: `INVENTORY_READ`, `INVENTORY_WRITE`, `INVENTORY_DELETE`, `KIOSK_ADMIN`, `REPORTS_VIEW`, `SALES_CREATE`, `SALES_READ`, `*` (acceso total para Customers).

### Modelo de Jerarquía (Lógica de Negocio)
```
Godadmin (dueño de SIGA)
├── Control total de la plataforma
├── Ve todos los tenants
├── Políticas de seguridad globales
├── Recuperación de datos (Ley 21.719)
└── Acceso a /(platform)/ en dashboard

Super-admin (dueño de empresa PYME)
├── Control total de su empresa (Permiso '*')
├── Crea usuarios con permisos específicos
├── Ve KPIs de su negocio
└── Nadie puede quitarle privilegios

Admin / Cajero / Operador / Empleado
└── Permisos asignados granularmente por el Super-admin
```

## 7. Calidad y Cobertura de Tests

### Cobertura por Servicio (Mayo 2026)
| Servicio | Tests Unitarios | Tests Adaptadores | Tests Integración HTTP | Total |
|----------|----------------|-------------------|----------------------|-------|
| auth     | 59             | 29                | 38                   | 126   |
| billing  | 7              | 22                | 4                    | 33    |
| inventory| 12             | 25                | 10                   | 47    |
| sales    | 30             | 55                | 18                   | 103   |

### Infraestructura de Tests
- **H2** para tests de adaptadores y persistencia (rápido, sin Docker)
- **MockMvc** para tests de integración HTTP
- **Embedded Kafka** para tests de eventos SAGA
- **Flyway**: Activado en producción (`ddl-auto: validate`), deshabilitado en tests (H2 + `create-drop`). V1 migrations responsables de crear schemas (`CREATE SCHEMA IF NOT EXISTS`) y usar `schema.table` explícito.
- **Convención**: Tests en cada servicio replican el patrón hexagonal: adapter tests → use case tests → integration tests

### Patrón de Commits
- **Rama actual**: `migracion-microservicios`
- **Formato**: Conventional Commits en español e inglés (bilingüe)
- **No PRs**: Commits directos a la rama en uso

## 8. POS (Punto de Venta)

### Fase 1 — POS Simple (Actual)
- UI para cajeras con búsqueda de productos
- Carrito de ventas con múltiples métodos de pago
- Descuento de stock automático vía SAGA (Kafka)
- Comprobante interno (no fiscal)
- KPIs para el cliente PYME (productos más vendidos, ventas por período)

### Fase 2 — Facturación Electrónica (Futuro)
- Integración con servicio externo DTE (Nexxus/E-Sii.cl)
- Emisión de boletas y facturas electrónicas SII
- Cada PYME usa su propio certificado digital
- SIGA NO accede a los montos ni datos fiscales (Ley 21.719)

## 9. Billing — Alcance Definido

`siga-billing` gestiona **exclusivamente** el SaaS de SIGA:

| Recurso | Endpoint | Estado |
|---------|----------|--------|
| Planes de suscripción | `GET/POST /api/v1/billing/plans` | ✅ |
| Clientes del SaaS | `GET/POST /api/v1/billing/customers` | ✅ |
| Suscripciones | `GET/POST /api/v1/billing/subscriptions` | ✅ |
| Pagos | `GET/POST /api/v1/billing/payments` | ✅ |

El modelo `SaleInvoice` (en billing) se genera automáticamente desde eventos de Sales (SAGA). No tiene endpoints REST ni UI. Su propósito futuro es entregar KPIS agregados al cliente PYME sin exponer montos individuales.

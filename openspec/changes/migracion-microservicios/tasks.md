# Tasks: Descomposición de Monolito SIGA (Polyglot) v2.1

## Fase 1: Infraestructura y Normalización (COMPLETADO ✅)
- [x] **1.1 Service Registry y Gateway**
    - [x] Levantar Eureka y Gateway enrutando peticiones.
- [x] **1.2 Normalización de Datos Multi-tenant**
    - [x] Crear 5 esquemas PostgreSQL en Docker.
    - [x] Migrar 27 entidades JPA (Auth, Inv, Ventas, Backend) a sus esquemas correctos.
    - [x] Eliminar toda referencia al esquema legado `siga_saas`.

## Fase 2: Servicios Core (En Proceso 🔄)

### 2.1 Dominio de Identidad (`siga-auth`)
- [x] Migración de entidades base.
- [ ] Implementar modelo de **Permisos Granulares Dinámicos** (Dueño puede quitar/poner permisos).
- [ ] Implementar **Herencia de Privilegios** para el Agente de IA.

### 2.2 Dominio de Inventario y Ventas
- [x] Normalización de esquemas JPA.
- [ ] Implementar Reconciliación de Caja (Monto Contado vs Monto Sistema).
- [ ] Integrar Webhooks de stock entre Ventas e Inventario.

### 2.3 Dominio Billing (`siga-billing`)
- [x] Crear servicio `siga-billing` (schema: `siga_comercial`).
- [x] Entidades: CommercialUser, Plan, Subscription, Payment, ShoppingCart, Invoice.
- [ ] Implementar registro de empresas via Google OAuth2.
- [ ] Implementar flujo de compra de suscripción.
- [ ] Implementar gestión de planes (CRUD).
- [ ] Integrar con `siga-auth` para usuarios comerciales.

## Fase 3: Inteligencia y Resiliencia (PRÓXIMO PASO 🚀)

### 3.1 Agente de IA (`siga-agente`)
- [x] Estructura base Python / FastAPI / Eureka.
- [ ] Implementar validación de JWT Pass-through en el servicio Python.
- [ ] Refinar herramientas CRUD (Operador) con chequeo de permisos previo.

### 3.2 Servicio de Fallback (`siga-fallback`)
- [ ] **Diseño**: Definir si será un módulo en el Gateway o un servicio dedicado.
- [ ] **Implementación**: Crear lógica de ejecución de Queries SQL/PL-SQL predefinidas.
- [ ] **Frontend**: Implementar en el chat la UI para mostrar "Modo Resiliencia: Datos Crudos".

## Fase 4: Verificación y Despliegue
- [ ] Smoke Test: Levantar todos los servicios y verificar registro en Eureka.
- [ ] Auditoría de Seguridad: Verificar que un usuario de la Empresa A no pueda ver datos de la Empresa B vía Agente.

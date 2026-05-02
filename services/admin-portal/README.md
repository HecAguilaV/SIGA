# Admin Portal (Backoffice)

Este servicio es la interfaz de administración central para los propietarios y operadores de la plataforma **SIGA**.

## Propósito
Proporcionar una visión global del sistema, gestión de inquilinos (tenants), monitoreo de salud de microservicios y analíticas de alto nivel.

## Responsabilidades
- **Gestión de Clientes (Pymes)**: Alta, baja y suspensión de cuentas.
- **Métricas Globales**: Visualización de ventas consolidadas de todas las sucursales.
- **Configuración de Sistema**: Gestión de parámetros globales y límites de stock.
- **Soporte Técnico**: Acceso a logs de auditoría (vía `siga-common`).

## Detalles Técnicos (Propuesta)
- **Stack**: [PENDIENTE DEFINIR] (Recomendado: React o SvelteKit).
- **Consumo de APIs**: Se comunica exclusivamente a través del `siga-gateway`.
- **Autenticación**: Integración con `siga-auth` mediante roles de `ADMIN_MASTER`.

## ⛓️ Interrelaciones (Flujos Técnicos)

El Admin Portal no tiene base de datos de negocio propia; consume datos de los microservicios a través del `siga-gateway`.

### 1. Con Microservicio de Ventas (`siga-sales`)
- **Cómo**: Consulta agregada de transacciones y estados financieros.
- **Ejemplo Claro**: El administrador de SIGA entra al dashboard y ve un gráfico de "Ventas Totales del Mes" de todas las Pymes registradas. 
  - *Detrás de escena*: El portal llama a `GET /api/v1/sales/analytics/global-stats`.

### 2. Con Microservicio de Inventario (`siga-inventory`)
- **Cómo**: Gestión de catálogos globales y supervisión de stock crítico.
- **Ejemplo Claro**: El administrador necesita estandarizar una categoría de productos (ej: "Abarrotes") para que aparezca en todas las Pymes.
  - *Detrás de escena*: El portal envía un `POST /api/v1/inventory/categories` para actualizar el catálogo maestro.

### 3. Con Microservicio de Autenticación (`siga-auth`)
- **Cómo**: Control de acceso de alto nivel y gestión de inquilinos (Tenants).
- **Ejemplo Claro**: Una Pyme no ha pagado su suscripción y el administrador debe suspender su acceso al sistema.
  - *Detrás de escena*: El portal ejecuta un `PATCH /api/v1/auth/tenants/{uuid}/status` con el estado `SUSPENDED`.

### 4. Con Microservicio de Auditoría (`siga-common`)
- **Cómo**: Visualización de trazas de actividad para cumplimiento legal.
- **Ejemplo Claro**: Ante una auditoría por la Ley 21.719, el administrador busca quién modificó el precio de un producto X en la Pyme Y.
  - *Detrás de escena*: El portal consulta los logs centralizados filtrando por `entity_type: PRODUCT`.

---
> "La torre de control de SIGA."

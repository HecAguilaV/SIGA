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

## Interrelaciones
- **Sales**: Consulta de reportes de ventas agregados.
- **Inventory**: Gestión de catálogos maestros de productos.
- **Auth**: Gestión de permisos y roles administrativos.

---
> "La torre de control de SIGA."

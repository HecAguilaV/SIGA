# Admin Portal (Backoffice)

Este servicio es la interfaz de administración central para los propietarios y operadores de la plataforma **SIGA**.

## Propósito
Proporcionar una visión global del sistema, gestión de inquilinos (tenants), monitoreo de salud de microservicios y analíticas de alto nivel.

## Responsabilidades
- **Gestión de Inquilinos (Tenants)**: Control de ciclo de vida de cuentas de Pymes (Alta, Baja, Suspensión por pago).
- **Monitoreo de Infraestructura**: Estado de salud de los microservicios y latencia de red por región.
- **Analíticas de Carga**: Volumen de transacciones procesadas (sin acceso a montos económicos).
- **Gobernanza de Auditoría**: Supervisión de accesos de alto nivel (vía `siga-common`) para garantizar la integridad del sistema.

## Cumplimiento Legal y Privacidad (Ley 21.719)
Este portal está diseñado bajo el principio de **Privacidad por Diseño (Privacy by Design)**:
- **Zero-Knowledge Architecture**: El administrador de la plataforma **NO** tiene visibilidad sobre los montos financieros, detalles de clientes finales, ni niveles de stock de las Pymes.
- **Soberanía de Datos**: Cada Pyme es dueña absoluta de su base de datos. El Backoffice solo gestiona la "capacidad" y "disponibilidad" del servicio.
- **Aislamiento Total**: Se prohíbe el acceso a datos sensibles de negocio, cumpliendo estrictamente con la normativa chilena sobre protección de datos personales.

## Detalles Técnicos (Propuesta)
- **Stack**: [PENDIENTE DEFINIR] (Recomendado: React o SvelteKit).
- **Consumo de APIs**: Se comunica exclusivamente a través del `siga-gateway`.
- **Autenticación**: Integración con `siga-auth` mediante roles de `ADMIN_MASTER`.

## Interrelaciones (Flujos Técnicos)

El Admin Portal no tiene base de datos de negocio propia; consume datos de los microservicios a través del `siga-gateway`.

### 1. Con Microservicio de Ventas (`siga-sales`)
- **Cómo**: Monitoreo de throughput (rendimiento) y volumen de eventos SAGA.
- **Ejemplo Claro**: El administrador de SIGA ve cuántos "Eventos de Venta" se han procesado exitosamente para asegurar que el sistema no esté saturado.
  - *Detrás de escena*: El portal llama a `GET /api/v1/sales/metrics/throughput`.

### 2. Con Microservicio de Inventario (`siga-inventory`)
- **Cómo**: Supervisión de consistencia de datos y salud del broker (Kafka).
- **Ejemplo Claro**: Verificar que los eventos de reserva de stock se estén procesando sin lag para todas las Pymes.
  - *Detrás de escena*: El portal consulta métricas de lag en los tópicos de inventario.

### 3. Con Microservicio de Autenticación (`siga-auth`)
- **Cómo**: Control de acceso de alto nivel y gestión de inquilinos (Tenants).
- **Ejemplo Claro**: Una Pyme no ha pagado su suscripción y el administrador debe suspender su acceso al sistema.
  - *Detrás de escena*: El portal ejecuta un `PATCH /api/v1/auth/tenants/{uuid}/status` con el estado `SUSPENDED`.

### 4. Con Microservicio de Auditoría (`siga-common`)
- **Cómo**: Visualización de trazas de actividad para cumplimiento legal.
- **Ejemplo Claro**: Ante una auditoría por la Ley 21.719, el administrador busca quién modificó el precio de un producto X en la Pyme Y.
  - *Detrás de escena*: El portal consulta los logs centralizados filtrando por `entity_type: PRODUCT`.


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

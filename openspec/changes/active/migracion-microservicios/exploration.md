## Exploration: Definición Arquitectónica Definitiva de Microservicios SIGA

### Current State
He analizado el código fuente. Actualmente, el proyecto depende de un **Backend Monolítico** (`services/backend/src/main/kotlin/com/siga/backend`) estructurado por capas (`controller`, `service`, `entity`, `repository`), NO por dominios.
Existen 13 entidades acopladas en el mismo paquete:
`Categoria`, `Factura`, `Local`, `Permiso`, `Plan`, `Producto`, `RolPermiso`, `Stock`, `Suscripcion`, `UsuarioComercial`, `UsuarioPermiso`, `UsuarioSaas`, `Venta`.

Este diseño rompe los principios de alta cohesión y bajo acoplamiento necesarios para una arquitectura de microservicios.

### Affected Areas
- [x] **Eliminación del Núcleo Monolítico**: El módulo `services/backend/` ha sido eliminado físicamente. El sistema es ahora 100% microservicios.
- [ ] Todo el resto del particionamiento de servicios está en progreso.
- A nivel de base de datos, el empaquetado actual asume relaciones foráneas (`@ManyToOne`, `@OneToMany`) directas entre tablas que, en microservicios, estarán en bases de datos separadas.

### Approaches (Estrategia de Partición Definitiva)

Tras clasificar las 13 entidades, aquí están las opciones de partición:

1. **Microservicios Granulares (No Recomendado para el nivel actual)**
   - Servicios separados para `Ventas`, `Facturas`, `Locales`, `Stock`, `Catálogo`, etc.
   - **Cons**: Exceso de latencia de red, demasiada complejidad en transacciones distribuidas y orquestación (Sagas) muy complejas para defender en una evaluación parcial.

2. **Microservicios Orientados a "Bounded Contexts" (Estrategia Definitiva Recomendada)**
   - Agrupar entidades íntimamente relacionadas en dominios funcionales (Domain-Driven Design).
   - **Pros**: Equilibrio perfecto entre autonomía de despliegue y simplicidad de mantenimiento. Excelente narrativa técnica para defender la sostenibilidad y escalabilidad (ítem clave de la rúbrica).

### Recommendation: La Arquitectura Definitiva (Para tu Defensa)

Aplicando la **Opción 2**, estos son los **Microservicios Definitivos** que debes presentar y defender:

1.  **`siga-auth` (Microservicio de Identidad y Acceso)**
    - *Entidades*: `UsuarioComercial`, `UsuarioSaas`, `Permiso`, `RolPermiso`, `UsuarioPermiso`.
    - *Responsabilidad*: Todo lo relacionado con usuarios, contraseñas, roles y emisión de tokens JWT. 

2.  **`siga-inventario` (Microservicio de Catálogo y Logística)**
    - *Entidades*: `Producto`, `Categoria`, `Local`, `Stock`.
    - *Responsabilidad*: Maestro de productos, ubicaciones físicas y existencias. Es el servicio que más lecturas recibirá.

3.  **`siga-ventas` (Microservicio Transaccional)**
    - *Entidades*: `Venta`, `Factura` (asumiendo que es la factura/boleta de la venta comercial).
    - *Responsabilidad*: Registrar las transacciones, procesar los carritos y generar los comprobantes. 

4.  **`siga-comercial` (Microservicio SaaS Administrativo)**
    - *Entidades*: `Plan`, `Suscripcion` (y posiblemente una entidad `FacturaSaaS` si cobras por el uso de SIGA).
    - *Responsabilidad*: Cobros recurrentes de las empresas clientes, validación de que un comercio tiene su suscripción al día.

5.  **`siga-asistente` (Microservicio IA - Sin estado transaccional fuerte)**
    - *Responsabilidad*: Conexión con Gemini AI, RAG. Consultará a los demás servicios vía REST o gRPC.

**La Base Infraestructural Definitiva**:
- **Gateway (`siga-gateway`)**: El guardia de seguridad. Recibe el front y rutea y valida el JWT.
- **Service Registry (`siga-registry` / Eureka)**: La guía telefónica. Permite que el Gateway encuentre a los servicios sin IPs quemadas.

### Risks de la Solución Propuesta
- **Riesgo**: Desacoplamiento de Base de Datos. No puedes tener una FK directa entre `Venta` (en DB de Ventas) y `Producto` (en DB de Inventario).
- **Mitigación**: En la clase `Venta`, en lugar de `@ManyToOne Producto`, tendrás `UUID productoId`.

### Ready for Proposal
**Yes**. La división de dominios está clara y basada en el código real.

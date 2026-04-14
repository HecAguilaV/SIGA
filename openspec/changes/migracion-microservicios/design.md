# Design: Arquitectura Definitiva de Microservicios SIGA

## Technical Approach

Monorepo Gradle multi-módulo con Spring Cloud. Cada microservicio es un módulo independiente
que comparte dependencias base pero tiene su propio ciclo de vida de despliegue.

## Architecture Decisions

### Decision: Catálogo de Microservicios

| Servicio | Tipo | Esquema DB | Entidades / Responsabilidad |
|----------|------|------------|----------------------------|
| `siga-eureka` | Infraestructura | — | Service Discovery (Registro de servicios) |
| `siga-gateway` | Infraestructura | — | Ruteo, CORS, validación JWT |
| `siga-auth` | Negocio | `siga_saas` | UsuarioSaas, Permiso, RolPermiso, UsuarioPermiso + OAuth2 (Google, Apple) |
| `siga-inventario` | Negocio | `siga_saas` | Producto, Categoria, Local, Stock |
| `siga-ventas` | Negocio | `siga_saas` | Venta, DetalleVenta |
| `siga-billing` | Negocio | `siga_comercial` | UsuarioComercial, Plan, Suscripcion, Factura |
| `siga-agente` | Negocio | — (stateless) | GeminiService, Assistant |
| `siga-fallback` | Soporte | — | Resiliencia y respuestas por defecto para la IA |

**Descartados**:
- ~~`siga-comercial`~~: Renombrado a `siga-billing`. "Comercial" confunde con el módulo frontend.
- ~~`siga-landing`~~: Es un cliente estático (HTML/CSS), no un microservicio. Se despliega en Vercel/GitHub Pages.
- ~~`siga-db`/`siga-backend`~~: No existe como servicio. Cada microservicio accede directamente a su esquema.
- ~~Eureka Server~~: En un monorepo con Docker Compose, usamos DNS de Docker para descubrimiento. Eureka añade complejidad sin beneficio real a esta escala.

### Decision: Autenticación con OAuth 2.0

**Choice**: Spring Security OAuth2 Client + JWT propios
**Alternatives**: Firebase Auth, Keycloak
**Rationale**: El usuario quiere Google e iCloud (Apple). Spring Boot tiene soporte nativo con
`spring-boot-starter-oauth2-client`. Flujo: el usuario se autentica con Google/Apple →
`siga-auth` recibe el token → valida → emite un JWT propio de SIGA con `tenant_id` y `rol`.
Apple Sign-In requiere firma JWT del lado del servidor (más complejo que Google pero factible).

### Decision: Base de Datos

**Choice**: 1 instancia PostgreSQL, 2 esquemas lógicos (`siga_saas`, `siga_comercial`)
**Alternatives**: 2 instancias separadas, DB por microservicio
**Rationale**: Ya existe esta estructura en el código actual. Separar en instancias distintas
añade costo de infraestructura y complejidad operacional sin beneficio real para la escala
actual. La separación por esquemas ya provee aislamiento lógico multi-tenant.
Cada microservicio se conecta a la misma instancia pero SOLO accede a su esquema asignado.

### Decision: Service Discovery con Eureka

**Choice**: Netflix Eureka (`siga-eureka`)
**Alternatives**: Docker DNS, Consul
**Rationale**: Requisito explícito de la rúbrica académica. Provee un registro centralizado donde todos los microservicios notifican su estado y ubicación, facilitando el ruteo dinámico desde el Gateway.

### Decision: Gateway con ruteo dinámico

**Choice**: Spring Cloud Gateway + Eureka Discovery
**Alternatives**: Rutas estáticas
**Rationale**: Al usar Eureka, el Gateway no necesita conocer las IPs/Puertos fijos. Simplemente pide a `siga-eureka` el servicio por su nombre (`siga-ventas`) y Eureka resuelve la ubicación.

## Data Flow

```
                    ┌──────────────┐
   Clientes         │   Gateway    │
   (Web/Mobile) ───▶│   :8080      │
                    └──────┬───────┘
                           │ JWT validation
              ┌────────────┼────────────┐
              ▼            ▼            ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │  Auth    │ │ Inventario│ │  Ventas  │
        │  :8081   │ │  :8082   │ │  :8083   │
        └────┬─────┘ └────┬─────┘ └────┬─────┘
             │             │            │
             ▼             ▼            ▼
        ┌─────────────────────────────────────┐
        │         PostgreSQL :5432            │
        │  ┌─────────────┐ ┌───────────────┐ │
        │  │  siga_saas   │ │siga_comercial │ │
        │  └─────────────┘ └───────────────┘ │
        └─────────────────────────────────────┘

        ┌──────────┐  ┌──────────┐  ┌──────────┐
        │ Billing  │  │ Agente   │──▶ siga-fallback
        │  :8084   │  │  :8085   │  │  :8086   │
        └────┬─────┘  └────┬─────┘  └──────────┘
             │             │
             ▼             ▼
        siga_comercial   Gemini API
```

## File Changes (Estructura Monorepo)

| File | Action | Description |
|------|--------|-------------|
| `settings.gradle.kts` (raíz) | Create | Define los módulos del monorepo |
| `build.gradle.kts` (raíz) | Create | Dependencias compartidas (Spring Boot BOM) |
| `services/registry/` | Create | Eureka Server (`siga-eureka`) |
| `services/gateway/` | Create | Spring Cloud Gateway con Eureka Discovery |
| `services/auth/` | Create | Extraer AuthController, JWTService, PasswordService + OAuth2 |
| `services/inventario/` | Create | Extraer Producto, Categoria, Local, Stock + controllers |
| `services/ventas/` | Create | Extraer Venta, DetalleVenta + VentasController |
| `services/billing/` | Create | Extraer UsuarioComercial, Plan, Suscripcion, Factura |
| `services/agente/` | Create | Extraer GeminiService, AssistantServices, ChatController |
| `services/fallback/` | Create | Lógica de contingencia para la IA |
| `services/backend/` | Deprecate | Se mantiene como referencia hasta completar la migración |
| `docker-compose.yml` | Modify | Añadir todos los servicios nuevos |

## Interfaces / Contracts

Comunicación entre servicios: REST síncrono vía Gateway.
Cada servicio expone su API bajo un prefijo:

```
/api/auth/**      → siga-auth
/api/inventario/** → siga-inventario
/api/ventas/**    → siga-ventas
/api/billing/**   → siga-billing
/api/agente/**    → siga-agente
```

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Lógica de servicios | JUnit 5 + Mockito |
| Integration | Endpoints REST | @SpringBootTest + TestRestTemplate |
| E2E | Flujo Completo | Docker Compose + scripts de humo |

## Migration / Rollout

Patrón Strangler Fig:
1. Crear servicios nuevos EN PARALELO al backend monolítico
2. Gateway rutea gradualmente endpoints al nuevo servicio
3. Cuando un dominio está 100% migrado, eliminar del monolito
4. El backend original se mantiene como fallback durante la transición

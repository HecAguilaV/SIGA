# Design: Arquitectura Definitiva de Microservicios SIGA

## Technical Approach

Monorepo multi-módulo con arquitectura poli-glota (Polyglot Programming). El Core transaccional utiliza Kotlin + Spring Cloud, mientras que el motor de IA utiliza Python. Cada microservicio es independiente y tiene su propio ciclo de vida de despliegue y pipelines CI/CD aislados.

## Architecture Decisions

### Decision: Catálogo de Microservicios

| Servicio | Tipo | Esquema DB | Entidades / Responsabilidad | Stack Tecnológico |
|----------|------|------------|----------------------------|-------------------|
| `siga-eureka` | Infraestructura | — | Service Discovery | Kotlin / Spring Boot |
| `siga-gateway` | Infraestructura | — | Ruteo, CORS, validación JWT | Kotlin / Spring Cloud |
| `siga-auth` | Negocio | `siga_saas` | Identity Provider, Emisión JWT + OAuth2 | Kotlin / Spring Boot |
| `siga-inventario` | Negocio | `siga_saas` | Producto, Stock, Local | Kotlin / Spring Boot |
| `siga-ventas` | Negocio | `siga_saas` | Transacciones y Facturación | Kotlin / Spring Boot |
| `siga-billing` | Negocio | `siga_comercial`| Suscripciones SaaS | Kotlin / Spring Boot |
| `siga-agente` | Negocio | — (stateless) | Orquestador de Agentes IA (Strands) | **Python** / Strands |
| `siga-fallback` | Soporte | — | Resiliencia (Circuit Breaker target) | Kotlin / Spring Boot |

**Descartados**:
- ~~`siga-asistente`~~: Renombrado a `siga-agente` para reflejar su nueva capacidad autónoma.
- ~~`siga-comercial`~~: Renombrado a `siga-billing`.
- ~~`siga-backend`~~: El monolito será deprecado.

### Decision: Polyglot Architecture para IA (SIGA-Agente)

**Choice**: Microservicio en **Python** usando framework **Strands** conectado a **Ollama Cloud**.
**Alternatives**: Kotlin + Spring AI + Gemini API.
**Rationale**: Python domina el ecosistema de IA. Strands permite crear "Agentes" reales que exponen `@tool`s. En lugar de un RAG monolítico en Kotlin, el servicio Python se registra en Eureka (vía `py_eureka_client`) y, de forma autónoma, el Agente realiza HTTP GET/POST a `siga-inventario` o `siga-ventas` utilizando el JWT del usuario (Pass-through) para mantener la seguridad mandatada por la Ley 21.719. Ollama Cloud saca la carga de inferencia del hardware local.

### Decision: Autenticación con OAuth 2.0 y JWT Pass-through

**Choice**: Spring Security OAuth2 Client + JWT propios
**Alternatives**: Firebase Auth, Keycloak
**Rationale**: `siga-auth` emite un JWT firmado. Este JWT es el "pasaporte" que `siga-agente` (Python) debe presentar al llamar a endpoints de inventario para asegurar aislamiento Multi-Tenant.

### Decision: Base de Datos y Aislamiento

**Choice**: 1 Servidor PostgreSQL (Infraestructura Compartida), esquemas lógicos aislados por servicio.
**Alternatives**: 1 Servidor por microservicio.
**Rationale**: Mantiene costos bajos. Ningún microservicio puede ver las tablas del otro. Si `siga-ventas` necesita stock, no hace un `JOIN` a la base de datos de inventario, le hace una llamada REST a `siga-inventario`.

## Data Flow

```
                     ┌──────────────┐
    Clientes         │   Gateway    │
    (Web/Mobile) ───▶│   :8080      │
                     └──────┬───────┘
                            │ JWT validation & Routing
               ┌────────────┼────────────┐
               ▼            ▼            ▼
         ┌──────────┐ ┌──────────┐ ┌──────────┐
         │  Auth    │ │ Inventario│ │  Ventas  │
         │ (Kotlin) │ │ (Kotlin) │ │ (Kotlin) │
         └────┬─────┘ └────┬─────┘ └────┬─────┘
              │            │             │
              ▼            ▼             ▼
         ┌─────────────────────────────────────┐
         │      PostgreSQL (Infraestructura)   │
         │  ┌─────────────┐ ┌───────────────┐  │
         │  │  siga_saas  │ │siga_comercial │  │
         │  └─────────────┘ └───────────────┘  │
         └─────────────────────────────────────┘
                               ▲
         ┌──────────┐ ┌────────┴─┐ ┌──────────┐
         │ Billing  │ │ Fallback │ │ Agente   │▶─▶ Ollama
         │ (Kotlin) │ │ (Kotlin) │ │ (Python) │    Cloud
         └────┬─────┘ └──────────┘ └────┬─────┘
              │           ▲             │
              ▼           │ (Circuit Breaker)
        siga_comercial    └─────────────┘
```

## File Changes (Estructura Polyglot)

| File | Action | Description |
|------|--------|-------------|
| `services/registry/` | Create | Eureka Server (Kotlin) |
| `services/gateway/` | Create | API Gateway (Kotlin) |
| `services/auth/` | Create | Identity Provider (Kotlin) |
| `services/inventario/` | Create | Stock API (Kotlin) |
| `services/ventas/` | Create | Transacciones API (Kotlin) |
| `services/billing/` | Create | Suscripciones API (Kotlin) |
| `services/agente/` | Create | Microservicio **Python** + Strands (`main.py`, `requirements.txt`) |
| `services/fallback/` | Create | Contingencia analítica SQL (Kotlin) |
| `.github/workflows/` | Create | Pipelines CI/CD independientes por servicio |

## Interfaces / Contracts

- Comunicación inter-servicios: REST síncrono.
- Registro cruzado de lenguajes: El cliente Python usa `py_eureka_client` para notificar al `registry` de Kotlin.
- Propagación de Seguridad: Todo microservicio debe leer el header `Authorization: Bearer <jwt>`.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit (Kt) | Lógica de negocio Kotlin | JUnit 5 + Mockito |
| Unit (Py) | Tools del Agente Python | Pytest + Unit tests de Strands |
| Integration | Endpoints REST | @SpringBootTest / FastAPI TestClient |
| E2E | Polyglot Flow | Docker Compose + Jest/Cypress |

## Open Questions

- [ ] ¿Cómo empaquetaremos el contenedor Docker del servicio de Python para que coexista en el mismo `docker-compose.yml` de los servicios Gradle construidos con Jib o Dockerfiles de Java?

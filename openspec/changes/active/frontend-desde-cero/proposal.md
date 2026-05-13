# Proposal: Frontend desde Cero

## Intent

SIGA tiene 5 frontends legacy (`apps/`) con deuda técnica crítica — imports rotos, mock login bypass, sin refresh token, SvelteKit 2 sin runes. En vez de parchar, construimos **un frontend SvelteKit 5 desde cero** que unifica toda la UI, implementa BFF pattern con server-side composition, integra streaming SSE para A2UI, y entierra el legacy. Pipeline SDD + TDD desde el día 1.

## Scope

### In Scope

- Nuevo frontend SvelteKit 5 + TypeScript, estructura modular por dominio de negocio
- BFF layer: server load functions que componen desde gateway, fetch cero del cliente
- Auth completo: login dual Customer→User, refresh token con rotación, route guards
- Módulos core: Dashboard, Products, Stores, Categories, Users (CRUDs vía gateway)
- Integración A2UI: chat con siga-agent vía streaming SSE desde BFF
- Design system propio: CSS nativo + tokens de diseño, glassmorphism, WCAG AA
- Testing: Vitest (unit + stores + components) + Playwright (E2E) + TDD estricto
- Rescate selectivo: theme CSS legacy, A11yToolbar, ToastContainer, chart wrappers, CrudTable pattern
- Deprecación formal de los 5 frontends legacy
- CI/CD pipeline para el nuevo frontend (build, test, deploy)

### Out of Scope

- POS/caja (requiere sales module completo con turnos)
- Facturación electrónica (SII / AFIP — módulo billing separado)
- SSO con proveedores externos (Google, GitHub — deferred)
- Mobile app (futuro proyecto separado)
- Backend: refresh token endpoint y gateway-level JWT filter son prerrequisitos, no parte de este cambio

## Capabilities

> Contrato entre proposal y sdd-spec. Cada item genera un delta spec en `openspec/changes/active/frontend-desde-cero/specs/`.

### New Capabilities

- `ui-theme`: Design tokens, glassmorphism, modo claro/oscuro, WCAG AA — sistema visual completo
- `ui-bff`: BFF layer — server load functions que componen respuestas desde el gateway (auth headers, paginación, errores)
- `ui-auth-flow`: Login, logout, refresh token automático, route guards, manejo de sesión
- `ui-dashboard`: Dashboard con KPIs (total productos, locales, valor inventario) server-composed desde gateway
- `ui-crud`: Patrón genérico CRUD (products, stores, categories, users) con tabla, formulario, búsqueda
- `ui-a2ui`: Chat A2UI con streaming SSE, ContextualAssistant flotante, analista/operador
- `ui-testing`: Setup de Vitest + Playwright + patrones de test TDD para frontend
- `ui-a11y`: Accesibilidad — A11yToolbar (rescatada), navegación teclado, contraste, lectores

### Modified Capabilities

- `customer-auth` (requirement change): Actualizar spec existente para incluir refresh token endpoint y rotación
- `database` (requirement change): Si refresh token require store, actualizar spec DB

### Unchanged Capabilities (not affected by this change)

- `bdd-integration`: No se modifica — los escenarios BDD del backend no cambian
- `tdd-enforcement`: No se modifica — se aplica al frontend con vitest, no al pipeline kotlin
- `verify-feedback`: No se modifica — el ciclo de verify es el mismo

## Approach

```
┌──────────────────────────────────────────────────────┐
│                   SvelteKit 5 App                     │
│  ┌──────────┐  ┌──────────┐  ┌─────────────────────┐ │
│  │  Client   │  │  Server  │  │   Design System      │ │
│  │  (.svelte)│  │  (load   │  │   (tokens CSS,       │ │
│  │  + runes  │  │   func.) │  │    glassmorphism)     │ │
│  └─────┬─────┘  └────┬─────┘  └─────────────────────┘ │
│        │              │                                │
│  ┌─────▼──────────────▼──────┐                        │
│  │    BFF Service Layer      │                        │
│  │  (server-side fetch → GW) │                        │
│  └──────────────────────────-┘                        │
└───────────────────┬──────────────────────────────────-┘
                    │
         ┌──────────▼──────────┐
         │   Gateway :8080     │
         │  (CORS restringido) │
         └────┬────┬────┬─────┘
              │    │    │
         auth │ inv │ agent...
```

- **BFF**: Cada ruta del frontend tiene una `+page.server.ts` (load function) que hace fetch al gateway con el token JWT. El cliente recibe data ya compuesta, nunca hace fetch directo.
- **SSE streaming**: El chat A2UI usa `ReadableStream` desde el server de SvelteKit (endpoint `+server.ts` que se conecta a siga-agent SSE), el cliente recibe chunks vía streaming.
- **Refresh token**: Interceptor en el server (hooks.server.ts) que refresca el token automáticamente antes de expirar. El cliente nunca maneja tokens.
- **CSS**: Cero frameworks. Sistema de tokens vía CSS custom properties en `:root` y `[data-theme="dark"]`. Componentes con scoped styles. Utility classes generadas por necesidad.
- **Testing**: RED-GREEN-REFACTOR explícito. Cada componente nuevo arranca con test fallando. Vitest para unit/stores, Playwright para integración/E2E.

## Affected Areas

| Área | Impacto | Descripción |
|------|---------|-------------|
| `apps/webapp/` | Deprecado | Legacy SvelteKit 2 — no tocar, solo marcar deprecado |
| `apps/admin-portal/` | Deprecado | Legacy React — marcar deprecado |
| `apps/customer-portal/` | Deprecado | Legacy — marcar deprecado |
| `apps/mobile/` | Deprecado | Legacy — marcar deprecado |
| `apps/landing/` | Deprecado | Legacy — marcar deprecado |
| `packages/frontend/*` | Nuevo | Nuevo frontend SvelteKit 5 — TODO el código |
| `services/gateway` | Modificado | CORS restringido, refresh token filter (backend team) |
| `services/auth` | Modificado | Refresh token endpoint + rotación (backend team) |
| `services/siga-agent` | Modificado | SSE streaming endpoint (backend team) |
| `.github/workflows/` | Nuevo/Modificado | CI/CD para frontend + testing |
| `openspec/specs/customer-auth/spec.md` | Modificado | Delta spec para refresh token |
| `openspec/specs/database/spec.md` | Posible mod | Delta spec si refresh token store en DB |

## Phases

| Fase | Entregables | Testing |
|------|-------------|---------|
| **1. Scaffold + Auth** | SvelteKit 5 scaffold, BFF structure, theme tokens, login page, refresh token, route guards | Vitest auth store, Playwright login flow |
| **2. Core modules** | Dashboard KPIs, Products CRUD, Stores CRUD, Categories CRUD, Users CRUD, CrudTable genérico | Vitest components + stores, Playwright CRUD E2E |
| **3. A2UI Streaming** | SSE endpoint integration, ContextualAssistant flotante, analyst/operator chat, memory context | Vitest SSE mock handler, Playwright chat flow |
| **4. Analytics** | Chart.js wrappers (refactorizados a genérico), dashboard extendido, KPIs server-composed | Vitest chart helpers, Playwright analytics |
| **5. Legacy burial** | Deprecation notices, redirects, clean up CI old builds, archivar apps/ legacy | Verificación de que nada legacy queda en el pipeline |

## Visual Identity Direction

- **Glassmorphism**: background blur + semitransparencia en cards, modales, sidebar. Paleta existente (`#5E6AD2` accent) mejorada con más variantes.
- **Design tokens**: CSS custom properties → `--color-accent`, `--surface-glass`, `--radius-card`, `--shadow-glow`, etc. Documentados en spec `ui-theme`.
- **Tipografía**: Inter (sans-serif), JetBrains Mono (mono/números). Mantener del legacy.
- **Iconos**: Phosphor Icons vía `phosphor-svelte` (mantener).
- **Modo claro/oscuro**: Soporte nativo con `prefers-color-scheme` + toggle manual. Persistencia localStorage.
- **Accesibilidad WCAG AA**: Rescatar A11yToolbar del legacy (alto contraste, escala de grises, fuente legible, subrayar enlaces). Navegación por teclado en todos los CRUDs. Roles ARIA en componentes críticos.
- **NO frameworks CSS**: Cero Bulma, cero Tailwind, cero Bootstrap. CSS nativo + design tokens + scoped styles de Svelte.

## Risks

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|-------------|---------|------------|
| Dependencia de backend para refresh token | Alta | Bloqueante | Coordinar con backend team: spec primero, mock en frontend mientras tanto |
| SSE streaming en SvelteKit server edge | Media | Medio | Prototipo en Fase 1/2 para validar factibilidad técnica |
| Migración de usuarios activos durante Fase 5 | Media | Alto | Feature flags + rollout gradual por tenant |
| Scope creep (querer hacer POS u otros módulos) | Media | Bajo | Out of Scope explícito en proposal, validar en cada fase |
| Curva de aprendizaje SvelteKit 5 + runes | Baja | Bajo | El equipo ya tiene experiencia Svelte (legacy). Runes son mejora, no cambio radical |
| Pruebas de accesibilidad automatizadas | Media | Medio | axe-core + Playwright integration desde Fase 1 |

## Rollback Plan

1. **Por fase**: Cada fase es autónoma y desplegable independientemente. Rollback por feature flag.
2. **Token de sesión**: Si el nuevo refresh token falla, el login legacy con JWT 24h sigue funcionando en backend. El frontend puede caer a mock token para desarrollo.
3. **Legacy coexiste**: Los 5 frontends legacy NO se eliminan hasta que Fase 5 verifique cero dependencias. Rollback = desactivar feature flag del nuevo frontend y redirigir tráfico al legacy.
4. **Git revert**: Cada fase en rama separada. Revertir la rama de la fase problemática sin afectar fases anteriores.

## Dependencies

| Dependencia | Quién | Cuándo |
|-------------|-------|--------|
| Refresh token endpoint (`POST /api/v1/auth/refresh`) | Backend team (auth) | Antes de Fase 1 (mínimo spec) |
| Gateway CORS restringido | Backend team (gateway) | Antes de Fase 1 |
| SSE streaming en siga-agent (`/api/agent/chat/stream`) | Backend team (agent) | Antes de Fase 3 |
| Gateway-level JWT validation filter | Backend team (gateway) | Antes de Fase 2 (deseable) |
| Services locales en Railway o Docker | DevOps | Durante todo el desarrollo |

## Success Criteria

- [ ] Login + refresh token funciona en ciclo completo (login → token expira → refresh → sesión continua)
- [ ] Dashboard renderiza KPIs server-composed desde gateway (sin fetch directo del cliente)
- [ ] CRUD products: create, read, update, delete vía BFF con gateway
- [ ] Chat A2UI muestra respuestas del agente vía streaming SSE
- [ ] Vitest coverage ≥ 70% en todos los módulos (por `config.yaml`)
- [ ] Playwright E2E pasa en login, CRUD products, y chat flow
- [ ] WCAG AA verificado con axe-core en rutas principales (login, dashboard, products)
- [ ] Legacy apps deprecadas: ninguna referencia activa en CI/CD, documentación updated

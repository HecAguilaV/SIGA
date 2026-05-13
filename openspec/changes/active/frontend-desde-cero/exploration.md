# Exploration: frontend-desde-cero

> Reporte generado el 13 de mayo de 2026 — Rama: `migracion-microservicios`

---

## 1. apps/webapp (Legacy Activo)

### 1.1 Hallazgos

**Tecnología** (`apps/webapp/package.json`):
- **SvelteKit 2** (`@sveltejs/kit: ^2.0.0`) con adapter-vercel
- Svelte 5 (`svelte: ^5.0.0`) pero **NO usa runes** — todo es Svelte 4 legacy syntax ($:, export let, etc.)
- Dependencias: `bulma: ^1.0.2`, `chart.js: ^4.4.6`, `phosphor-svelte: ^3.0.1`
- No hay testing coverage (solo Sidebar.test.ts existe, `datosNegocio` import no existe)

**Estructura** (`apps/webapp/src/`):
```
src/
├── app.css            # 340 líneas — tema claro/oscuro, glassmorphism, Bulma overrides
├── app.html           # Layout HTML base
├── lib/
│   ├── components/    # 11 componentes
│   │   ├── A11yToolbar.svelte
│   │   ├── BarChart.svelte
│   │   ├── ContextualAssistant.svelte   # 771 líneas — chat flotante + insights
│   │   ├── CrudTable.svelte
│   │   ├── LineChart.svelte
│   │   ├── MultiLineChart.svelte
│   │   ├── PieChart.svelte
│   │   ├── ProductAssignmentModal.svelte
│   │   ├── Sidebar.svelte
│   │   ├── Sidebar.test.ts
│   │   └── ToastContainer.svelte
│   ├── services/
│   │   ├── api.js     # Fetch wrapper con Bearer token
│   │   └── auth.js    # Login + SSO validation
│   └── stores/
│       ├── authStore.js
│       ├── businessStore.js
│       ├── toast.js
│       └── uiStore.js
└── routes/
    ├── +layout.svelte  # Sidebar + ContextualAssistant + A11yToolbar
    ├── +page.svelte    # Dashboard — KPIs + tabla de productos
    ├── about/
    ├── analytics/      # KPIs + gráficos Chart.js
    ├── assistant/      # Chat page standalone
    ├── categories/     # CRUD con CrudTable
    ├── login/          # Login form
    ├── products/       # CRUD productos + gestión de stock por local
    ├── sso/            # SSO token handler
    ├── stores/         # CRUD locales
    └── users/          # CRUD usuarios
```

**Conexión con Backend** (`vite.config.js`):
- Vite proxy reenvía `/api` → `https://siga-backend-production.up.railway.app`
- **NO pasa por el gateway local** — apunta directo a producción
- `api.js` usa fetch plano con `Authorization: Bearer <token>` y maneja 401 → logout

**Auth Flow** (`authStore.js`, `auth.js`):
- Token en `localStorage` con key `siga_auth`
- Mock login: `admin@siga.cl / admin` genera mock token
- Login real: POST `/api/auth/login` → recibe `{ accessToken, user }`
- SSO: GET `/api/auth/me` con token en query param
- **NO hay refresh token** — 401 fuerza logout inmediato
- Roles: `ADMINISTRATOR`, `OPERATOR`, `CASHIER` (desde backend `Enums.kt`)
- `logoutUser()` hace `window.location.href = '/login'` — full page reload

**State Management**:
- `authStore`: writable con persistencia localStorage
- `businessStore`: writable con loadData() que llama a `/api/stores`, `/api/products`, `/api/stock`, `/api/categories`
- `uiStore`: sidebar + accesibilidad (tema, contraste, fuente)
- `toast`: notificaciones transientes

### 1.2 Problemas

1. **🚨 datosNegocio.js NO EXISTE** (`apps/webapp/src/lib/stores/datosNegocio.js` no está en disco) pero es importado en 3 archivos: `ContextualAssistant.svelte:5`, `assistant/+page.svelte:3`, `products/[id]/+page.svelte:6`. Esto rompe el build.
2. **🚨 Import incorrecto**: `analytics/+page.svelte:4` importa `LineChartMultiple` de `LineChartMultiple.svelte`, pero el archivo real se llama `MultiLineChart.svelte`.
3. **API endpoints inconsistentes**: Conviven endpoints en inglés (`/api/stores`, `/api/products`) manejados por `businessStore`, y endpoints en español (`/api/saas/productos`, `/api/saas/stock`) manejados por `datosNegocio`. El gateway NO rutea `/api/saas/**` — la ruta solo existe en el Railway de producción.
4. **Mock login bypass**: `admin@siga.cl / admin` hardcodeado en `auth.js:12-25` — riesgo de seguridad en producción.
5. **Sin refresh token**: Token JWT de 24h sin renovación. Si expira → sesión perdida sin recuperación.
6. **Svelte 5 no aprovechado**: Se usa Svelte 5 package pero todo el código es Svelte 4 legacy (`export let`, `$:`, no runes).
7. **Sin tests**: Solo `Sidebar.test.ts` existe. No hay tests de integración ni de stores.
8. **No hay server load functions**: Todo es client-side fetching, no se usa `load` de SvelteKit.
9. **Chart.js dynamic import**: Bien hecho (lazy load en cliente), pero los wrappers tienen código duplicado entre BarChart, LineChart, PieChart.

### 1.3 Qué Rescatar

| Componente | Archivo | Por qué rescatarlo |
|---|---|---|
| **Theme system** | `app.css:1-340` | Sistema de tema claro/oscuro, glassmorphism, accesibilidad. Base sólida para el nuevo diseño. |
| **A11yToolbar** | `A11yToolbar.svelte` | Herramienta de accesibilidad completa (alto contraste, escala de grises, etc.). Reutilizable tal cual. |
| **ToastContainer** | `ToastContainer.svelte` | Sistema de notificaciones funcional con phosphor icons. |
| **CrudTable** | `CrudTable.svelte` | Tabla genérica con sort, create/edit/delete dispatch. Patrón a mantener. |
| **Chart components** | `BarChart.svelte`, `LineChart.svelte`, `PieChart.svelte`, `MultiLineChart.svelte` | Wrappers Chart.js con lazy loading y paleta corporativa. Código duplicado pero patrón válido. Refactorizar a uno genérico. |
| **api.js pattern** | `api.js` | Fetch wrapper con Bearer injection y 401 handling. Migrar a SvelteKit load functions + fetch. |
| **toast store** | `toast.js` | Store de notificaciones simple y efectiva. |
| **uiStore a11y** | `uiStore.js` | Gestión de preferencias de accesibilidad con persistencia localStorage. |
| **ContextualAssistant UI** | `ContextualAssistant.svelte` | Diseño visual del chat flotante (glassmorphism, drag, insight window). El HTML/CSS vale la pena; la lógica JS hay que reescribirla. |

### 1.4 Recomendaciones

- **NO migrar componente por componente** — es código legacy con deuda técnica significativa. Construir desde cero con SvelteKit 5 y runes.
- **SÍ rescatar** el theme CSS (`app.css`), A11yToolbar, toast system, y wrappers Chart.js.
- **Migrar a SvelteKit `load` functions** para data fetching server-side y BFF pattern.
- **Unificar API**: decidir un solo naming convention (recomiendo inglés) y rutear todo por el gateway.
- **Tipar todo**: el legacy usa JS plano — el nuevo frontend DEBE ser TypeScript estricto.
- **Test desde el día 1**: cada componente nuevo con vitest + testing-library.

---

## 2. Gateway (`services/gateway`)

### 2.1 Hallazgos

**Archivo**: `services/gateway/src/main/resources/application.yml`

```yaml
Routes definidas:
  siga-auth:      /api/auth/**           → lb://siga-auth     (rewrite: /api/v1/auth/{segment})
  siga-inventory: /api/products/**
                  /api/stores/**
                  /api/inventory/**      → lb://siga-inventory (rewrite: /api/v1/{service}/{segment})
  siga-sales:     /api/sales/**
                  /api/cash-shifts/**    → lb://siga-sales     (rewrite: /api/v1/{segment})
  siga-billing:   /api/billing/**
                  /api/comercial/**      → lb://siga-billing   (rewrite: /api/v1/billing/{segment})
  siga-agent:     /api/agent/**          → lb://siga-agent     (sin rewrite)

CORS: globalcors → allowedOrigins: "*"   ← INSEGURO
```

**Puertos**: Gateway 8080, Eureka 8761

### 2.2 Problemas

1. **🚨 CORS wildcard** (`allowedOrigins: "*"`) — riesgo de seguridad. En producción debe restringirse al dominio del frontend.
2. **Sin auth filter**: El gateway no valida JWT — delega a cada servicio. El agent usa pass-through via `X-Tenant-Id`.
3. **Sin rate limiting**: No hay protección contra abuso de endpoints.
4. **Eureka locator disabled** (`discovery.locator.enabled: false`) — las rutas están hardcodeadas en YAML.
5. **No hay ruta para `/api/saas/**`**: El frontend legacy llama a `/api/saas/chat`, `/api/saas/productos`, etc. que NO están en el gateway. El proxy de Vite las manda directo a Railway.

### 2.3 Qué Rescatar

- **RewritePath pattern**: El patrón de reescritura es correcto. Sirve como template para nuevas rutas.
- **Estructura de rutas**: La organización por service ID es clara y mantenible.

### 2.4 Recomendaciones

- El nuevo frontend DEBE apuntar al gateway local en desarrollo (no a Railway).
- Agregar ruta `/api/agent/**` para el chat A2UI (ya existe, bien).
- **Agregar gateway-level JWT validation** con Spring Cloud Gateway filter.
- **Restringir CORS** al origen del frontend en cada entorno.
- **Agregar rate limiting** con Redis o bucket4j.

---

## 3. siga-agent (A2UI)

### 3.1 Hallazgos

**Stack**: Python FastAPI, Strands agents, PGVector, sentence-transformers

**Endpoint**: `POST /api/agent/chat`
- Payload: `{ prompt, bot_type }` (bot_type default: "analyst")
- Headers: `X-Tenant-Id` (required)
- Response: `{ reply, status, bot_type }`

**Bots**:
| Bot | Tools | Propósito |
|---|---|---|
| **analyst** (`analyst.py`) | `get_inventory_kpis`, `get_sales_metrics`, `learn`, `recall` | Análisis de negocio, KPIs, tendencias |
| **operator** (`operator.py`) | `get_exact_stock`, `register_sale`, `learn`, `recall` | Transacciones operativas, stock, ventas |

**Memory** (`memory.py`):
- PGVector en schema `agent.documents`
- Embeddings locales con `all-MiniLM-L6-v2` (384 dimensiones)
- `learn()` guarda, `recall()` busca semántica con `<=>` cosine distance

**Tools** (`tools.py`):
- `get_inventory_kpis`: llama a `siga-inventory:/api/products`
- `get_sales_metrics`: llama a `siga-sales:/api/sales`
- `get_exact_stock`: mock (retorna stock simulado)
- `register_sale`: mock (retorna éxito simulado)

**Infra** (`database.py`):
- AsyncConnectionPool con psycopg
- Auto-creación de schema `agent` si no existe

### 3.2 Problemas

1. **Mock tools**: `get_exact_stock` y `register_sale` son stubs, no hacen llamadas reales.
2. **Sin streaming**: El endpoint es request/response bloqueante. No hay SSE (Server-Sent Events) ni WebSocket para streaming de respuestas del LLM.
3. **Modelo local**: `ollama_chat/llama3` con sentence-transformers local — limitado en capacidad vs. Gemini/OpenAI.
4. **X-Tenant-Id passthrough**: El gateway reenvía pero no valida el header. El agent lo recibe confiando en que el gateway ya autenticó — pero el gateway NO autentica.
5. **Endpoint legacy**: El frontend legacy llama a `/api/saas/chat`, no a `/api/agent/chat`.

### 3.3 Qué Rescatar

- **Arquitectura de bots**: Separación analyst/operator es buen patrón. Mantener para el nuevo frontend.
- **PGVector memory**: Sistema de memoria funcional y bien implementado.
- **Strands agents**: Framework de agentes válido, con tool registration y system prompts.

### 3.4 Recomendaciones

- **Implementar SSE streaming** para el chat A2UI — es clave para UX de asistente conversacional.
- El nuevo frontend debe llamar a `/api/agent/chat` (ya ruteado en gateway).
- Implementar herramientas reales (no mocks) conectando a inventory/sales.
- Agregar WebSocket o SSE endpoint específico para streaming (`/api/agent/chat/stream`).
- El frontend legacy llama a `/api/saas/chat` pero no existe en gateway — investigar si Railway tiene ese endpoint.

---

## 4. Auth

### 4.1 Hallazgos

**Backend**: Kotlin Spring Boot, `services/auth/`
- `AuthController.kt:13` → `/api/v1/auth/register`, `/api/v1/auth/login`, `/api/v1/auth/verify`
- `LoginUseCase.kt` → Dual login: primero intenta Customer, luego User
- `JwtService.kt` → HMAC256, claims: email, rol, principalType, tenantId, exp: 24h
- `Enums.kt` → Roles: `ADMINISTRATOR`, `OPERATOR`, `CASHIER`
- 126 tests (según STATUS.md)

**Frontend Flow**:
- `auth.js:login()` → mock bypass check → fetch `/api/auth/login`
- `authStore.js` → localStorage persist + subscribe para logout automático en 401
- `+layout.svelte:16-26` → protección de rutas: redirect a `/login` si no autenticado
- `+layout.svelte:44-72` → render condicional: sidebar solo si autenticado

### 4.2 Problemas

1. **🚨 Sin refresh token**: `JwtService.kt:37` → `plus(24, ChronoUnit.HOURS)`. Sin renovación, el token expira y el usuario pierde sesión.
2. **Mock bypass en frontend**: `auth.js:12` → hardcode de credenciales admin.
3. **JWT secret en variable de entorno**: Bien, pero HMAC256 (simétrico) en microservicios es riesgoso. RS256 (asimétrico) sería más seguro.
4. **No hay logout en backend**: El logout solo borra localStorage. El token sigue siendo válido hasta expirar.
5. **Gateway no valida JWT**: Cada microservicio debe validar el token por separado.

### 4.3 Qué Rescatar

- **Dual login (Customer + User)**: Patrón correcto para multi-tenant con admins y empleados.
- **Estructura Hexagonal**: Auth sigue Clean Architecture con puertos y adaptadores.
- **126 tests**: Base sólida de verificación.

### 4.4 Recomendaciones

- **Implementar refresh token** con endpoint dedicado y rotación.
- **Gateway-level JWT validation** con Spring Cloud Gateway filter (JWK Set URI o secret compartido).
- El nuevo frontend debe manejar refresh automático con interceptor en fetch.
- Mantener estructura de claims: `rol`, `tenantId`, `principalType`.

---

## 5. Contexto de Negocio

### 5.1 Hallazgos

**Qué es SIGA** (de `VISION.md`, `ecosistema_siga.md`, `ACADEMIC/LEARNING.md`):
- **SIGA** = Sistema de Gestión de Activos
- Target: PYMEs latinoamericanas, específicamente el "Guerrero Multi-rol" (emprendedor que hace de todo)
- Propósito: **Recuperar tiempo** del emprendedor — eliminar planillas, automatizar inventario

**Módulos de negocio**:

| Módulo | Dominio | Schema DB |
|---|---|---|
| **Auth** | Usuarios, roles, tenants | `siga_auth` |
| **Inventory** | Productos, locales, stock, categorías | `siga_inventario` |
| **Sales** | Ventas, turnos de caja | `siga_ventas` |
| **Billing** | Facturación, pagos, comercial | `siga_comercial` |
| **Agent** | Chat IA, memoria vectorial | `siga_agente` |

**Usuarios**:
- **Customer**: Dueño de PYME (tenant admin) — ve todo, gestiona usuarios
- **User (ADMINISTRATOR)**: Admin operativo del tenant
- **User (OPERATOR)**: Operador de local — gestiona stock, registra ventas
- **User (CASHIER)**: Cajero — solo POS y consultas básicas

**Lo que necesita la UI**:
1. **Login/SSO** → autenticación multi-tenant
2. **Dashboard** → KPIs: total productos, locales, valor inventario
3. **Products CRUD** → crear/editar/eliminar productos, buscar por SKU/nombre
4. **Stock management** → ver stock por producto/local, actualizar cantidades
5. **Store management** → CRUD de locales
6. **Categories** → CRUD + asignación de productos
7. **Users** → CRUD de usuarios del tenant
8. **Analytics** → gráficos de ventas, mermas, tendencias
9. **AI Assistant** → chat contextual con analyst/operator bots
10. **Sales (futuro)** → POS, turnos de caja

### 5.2 Recomendaciones

- El nuevo frontend debe priorizar las rutas en este orden: Login → Dashboard → Products → Stock → AI Assistant → Analytics
- Mantener la separación de dominios en la estructura del frontend (no mezclar inventory con sales)
- Cada "feature" del frontend debe mapear a un módulo de negocio

---

## 6. Visual Design Actual

### 6.1 Hallazgos

**Design System** (`app.css`, `openspec/core/STATUS.md`):

- **Framework CSS**: Bulma 1.0.2 (customizado con variables CSS)
- **Estilo**: Glassmorphism/Void (según STATUS.md: "Estética premium para diferenciación competitiva")
- **Paleta**:
  - Accent: `rgb(94, 106, 210)` — púrpura-azulado (#5E6AD2)
  - Dark surface: `rgb(26, 27, 30)` 
  - Light surface: `rgb(255, 255, 255)`
  - Text primary: `#111827` (light) / `#FFFFFF` (dark)
- **Componentes visuales**: `glass-card`, `glow-accent`, `kpi-card`, `premium-table`, `pulse-indicator`
- **Iconos**: Phosphor Icons (vía `phosphor-svelte`)
- **Tipografía**: Inter (sans-serif), JetBrains Mono (mono para números)
- **Accesibilidad**: 8 modificadores (alto contraste, escala de grises, fuente legible, subrayar enlaces, etc.)

**Assets**:
- Logo: `/S.png` (letra S estilizada), `/brand/Logo_SIGA.png`
- Favicon completo en `/favicon/`

### 6.2 Problemas

1. **Bulma + CSS custom**: Mezcla de Bulma classes con CSS variables custom. Inconsistencias.
2. **Sin design tokens**: Los colores están en CSS variables pero no hay un sistema de tokens formal.
3. **Sin documentación visual**: No hay guía de componentes ni storybook.
4. **openspec/ no tiene guías visuales**: No hay design system documentado en openspec/ core.

### 6.3 Qué Rescatar

- **CSS theme system** (`app.css`): El sistema de variables CSS para tema claro/oscuro es excelente. Migrar tal cual.
- **A11yToolbar**: Componente de accesibilidad completo y bien pensado.
- **Glassmorphism**: El estilo visual es distintivo. Mantenerlo en el nuevo frontend.
- **Phosphor icons**: Buena elección de librería de iconos.

### 6.4 Recomendaciones

- **Migrar CSS theme** a SvelteKit 5 manteniendo las variables CSS exactas (o mejoradas).
- **NO usar Bulma** en el nuevo frontend — el legacy ya lo overrides en su mayoría. Usar CSS nativo + utility classes.
- **Crear design tokens** en CSS custom properties (o con un sistema como Open Props).
- **Mantener glassmorphism** como identidad visual.
- **Storybook** para el nuevo design system.

---

## Resumen de Problemas Críticos

| # | Problema | Severidad | Archivo |
|---|---|---|---|
| 1 | `datosNegocio.js` importado pero no existe | 🔴 CRÍTICO | `ContextualAssistant.svelte:5`, `assistant/+page.svelte:3`, `products/[id]/+page.svelte:6` |
| 2 | `LineChartMultiple` import incorrecto | 🔴 CRÍTICO | `analytics/+page.svelte:4` |
| 3 | Mock login bypass hardcodeado | 🔴 CRÍTICO | `auth.js:12-25` |
| 4 | Sin refresh token | 🟠 ALTO | `JwtService.kt:37`, `authStore.js` |
| 5 | CORS wildcard en gateway | 🟠 ALTO | `application.yml:12` |
| 6 | Frontend apunta a Railway, no gateway local | 🟠 ALTO | `vite.config.js:26` |
| 7 | Endpoint /api/saas/chat no ruteado en gateway | 🟠 MEDIO | `application.yml`, `ContextualAssistant.svelte:216` |
| 8 | Sin tests en frontend | 🟡 BAJO | Solo `Sidebar.test.ts` |
| 9 | Svelte 5 no aprovechado (sintaxis legacy) | 🟡 BAJO | Todos los `.svelte` |

---

## Ready for Proposal

**SÍ** — La exploración es completa. Hay suficiente información para proceder a la fase de proposal.

### Lo que debe saber el equipo:
1. **El legacy tiene deuda técnica significativa** — no vale la pena migrar componente por componente.
2. **Sí hay piezas rescatables**: theme CSS, A11yToolbar, ToastContainer, CrudTable pattern, chart wrappers.
3. **El gateway ya tiene la ruta `/api/agent/` lista** para el chat A2UI.
4. **Auth necesita refresh token** antes o durante la migración del frontend.
5. **El design system visual (glassmorphism) es la identidad** — mantenerla en el nuevo frontend.
6. **La estructura de rutas del frontend legacy** (dashboard, products, stores, categories, users, analytics, assistant) **es correcta** — replicarla en el nuevo.

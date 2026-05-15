# Design: Frontend desde Cero — SvelteKit 5 BFF

## Technical Approach

Nuevo frontend SvelteKit 5 + TypeScript en `packages/frontend/` con BFF pattern: server load functions componen datos desde el gateway, fetch cero del cliente. Auth con httpOnly cookies + refresh token automático desde hooks. Design system propio con CSS custom properties + glassmorphism + WCAG AA. SSE streaming para A2UI chat. Rescate selectivo de componentes legacy (A11yToolbar, ToastContainer, CrudTable, chart wrappers).

---

## Architecture Decisions

| Decisión | Opciones | Tradeoffs | Resolución |
|----------|----------|-----------|------------|
| Ubicación del frontend | `packages/frontend/` vs `apps/` | `apps/` legacy SvelteKit 2 — confusión. `packages/` no existe pero aísla del legacy explícitamente. | **`packages/frontend/`** — fresco, sin asociación legacy. Se crea el directorio. |
| CSS Framework | Tailwind vs Bulma vs Native | Tailwind: utility-first, ~200KB, curva. Bulma: legacy pero con deuda. Native: 0 dependencias, control total, sin bundle extra. | **CSS Nativo + Design Tokens**. Scoped styles de Svelte + CSS custom properties. Utility classes generadas por necesidad. |
| Chat protocolo | SSE vs WebSocket | WS: bidireccional, más complejo, estado persistente. SSE: unidireccional server→client, reconexión nativa, más simple, EventSource API. siga-agent ya emite SSE. | **SSE via `ReadableStream`**. El server de SvelteKit crea un proxy SSE hacia siga-agent. Reconexión manual con backoff exponencial. |
| State management | Stores Svelte 4 vs Runes ($state) | Stores legacy Svelte 4. Runes son Svelte 5 nativos, más reactivos, menos boilerplate. | **Runes (`$state`, `$derived`)**. Migración natural a Svelte 5. |
| Dashboard data | Composición BFF vs endpoint gateway dedicado | Composición: múltiples llamadas, lógica de agregación duplicada. Endpoint dedicado: una llamada, acoplamiento a nuevo endpoint. | **Endpoint dedicado `GET /api/v1/dashboard/insights`** (nuevo). Fallback: composición en BFF desde `/api/inventory/`, `/api/stores/`, etc. |
| Refresh token flow | Client-side vs server-side (hooks) | Client-side: riesgo XSS, token visible. Server-side: httpOnly cookie, invisible al JS, más seguro. | **Server-side en `hooks.server.ts`**. Cookie httpOnly para access token, refresh cookie path-restricted a `/api/auth/refresh`. |
| Charts | Chart.js vs D3 vs lightweight-charts | D3: heavy, curva alta. lightweight-charts: solo financieros. Chart.js ya está en el proyecto y es rescatable. | **Chart.js rescatado del legacy**, envuelto en wrappers genéricos type-safe. Lazy load con `onMount`. |

---

## Directory Tree

```
packages/frontend/
├── src/
│   ├── app.html
│   ├── app.css                           # Design tokens + theme variables
│   ├── app.d.ts                          # Global type augmentations
│   ├── lib/
│   │   ├── server/
│   │   │   ├── gateway.ts                # fetchWithAuth() — JWT injection + auto-refresh
│   │   │   ├── auth.server.ts            # login(), logout(), refreshToken() helpers
│   │   │   └── chat.server.ts            # SSE proxy — ReadableStream pipe from siga-agent
│   │   ├── components/
│   │   │   ├── ui/                       # Atomic design system
│   │   │   │   ├── Button.svelte         # variant (primary|secondary|ghost|danger), size, loading
│   │   │   │   ├── Input.svelte          # type, label, error, aria-describedby
│   │   │   │   ├── Card.svelte           # surface-glass, padding, slot header/content/footer
│   │   │   │   ├── Modal.svelte          # open, title, onClose, focusTrap, aria-modal
│   │   │   │   ├── Toast.svelte          # type (success|error|info), message, autoDismiss
│   │   │   │   ├── Spinner.svelte        # size, variant
│   │   │   │   ├── Badge.svelte          # variant (info|warning|danger|success)
│   │   │   │   └── Skeleton.svelte       # variant (text|card|table-row)
│   │   │   ├── layout/
│   │   │   │   ├── Sidebar.svelte        # NavItem[], collapsed state, UserMenu
│   │   │   │   ├── Header.svelte         # Breadcrumb, ThemeToggle, A11yToolbar
│   │   │   │   ├── Breadcrumb.svelte     # items: {label, href}[]
│   │   │   │   ├── ThemeToggle.svelte    # sun/moon icon, localStorage persist
│   │   │   │   └── A11yToolbar.svelte    # Rescatado: highContrast, grayscale, largeFont, underlineLinks
│   │   │   ├── crud/
│   │   │   │   ├── CrudTable.svelte      # Generic<T>: columns, data, total, page, actions
│   │   │   │   ├── CrudForm.svelte       # Generic<T>: fields, onSubmit, initialValues, mode
│   │   │   │   ├── SearchBar.svelte      # debounce 300ms, URL query param sync
│   │   │   │   └── ConfirmDelete.svelte  # Modal de confirmación reutilizable
│   │   │   ├── charts/
│   │   │   │   ├── ChartWrapper.svelte   # Generic: type (bar|line|pie|doughnut), data, options
│   │   │   │   └── ChartContainer.svelte # Resize observer, skeleton loading, responsive
│   │   │   └── a2ui/
│   │   │       ├── ContextualAssistant.svelte  # FAB + chat widget flotante
│   │   │       ├── ChatBubble.svelte     # role (user|assistant), content, streaming, timestamp
│   │   │       ├── ChatInput.svelte      # textarea + send button, debounce spam, Enter to send
│   │   │       ├── ToolIndicator.svelte  # name, status (running|done|error), animación pulso
│   │   │       └── AssistantFab.svelte   # Floating action button con badge de estado
│   │   ├── stores/
│   │   │   ├── auth.svelte.ts            # $state user | null, $derived isAuthenticated
│   │   │   ├── theme.svelte.ts           # $state 'light'|'dark', toggle(), init()
│   │   │   ├── toast.svelte.ts           # $state Toast[], add(), remove(), autoDismiss
│   │   │   └── chat.svelte.ts            # $state messages[], status, send(), reconnect()
│   │   ├── utils/
│   │   │   ├── validators.ts             # isEmail(), isRequired(), min(), max(), isPositive()
│   │   │   ├── formatters.ts             # formatCurrency(), formatDate(), truncate(), pluralize()
│   │   │   └── debounce.ts               # debounce(fn, ms)
│   │   └── types/
│   │       ├── auth.ts                   # LoginRequest, LoginResponse, UserSession, PrincipalType
│   │       ├── inventory.ts              # Product, ProductListItem, ProductDetail, Category
│   │       ├── sales.ts                  # Sale, SaleSummary
│   │       ├── stores.ts                 # Store, StoreListItem
│   │       ├── dashboard.ts             # Insight, KpiCard, TrendDirection, Anomaly
│   │       └── chat.ts                   # ChatMessage, ChatStatus, SSEEvent, ToolCall
│   ├── routes/
│   │   ├── (auth)/
│   │   │   ├── +layout.svelte            # Minimal layout, sin sidebar
│   │   │   ├── login/
│   │   │   │   ├── +page.svelte          # LoginPage → CustomerLoginForm | UserLoginForm
│   │   │   │   └── +page.server.ts       # actions.default: POST /api/auth/login
│   │   │   └── logout/
│   │   │       └── +page.server.ts       # actions.default: POST /api/auth/logout + clear cookies
│   │   ├── (dashboard)/
│   │   │   ├── +layout.svelte            # Sidebar + Header + slot + ContextualAssistant
│   │   │   ├── +layout.server.ts         # load: verify session, refresh if needed
│   │   │   ├── +page.svelte              # DashboardPage
│   │   │   └── +page.server.ts           # loadDashboard()
│   │   ├── products/
│   │   │   ├── +page.svelte              # ProductListPage
│   │   │   ├── +page.server.ts           # loadProducts()
│   │   │   ├── [id]/
│   │   │   │   ├── +page.svelte          # ProductEditPage
│   │   │   │   └── +page.server.ts       # loadProduct(), actions: PUT /api/inventory/products/{id}
│   │   │   └── new/
│   │   │       ├── +page.svelte          # ProductCreatePage
│   │   │       └── +page.server.ts       # actions: POST /api/inventory/products
│   │   ├── stores/                       # Mismo patrón que products
│   │   │   ├── +page.server.ts           # loadStores()
│   │   │   ├── [id]/+page.server.ts
│   │   │   └── new/+page.server.ts
│   │   ├── categories/                   # Mismo patrón
│   │   │   ├── +page.server.ts           # loadCategories()
│   │   │   ├── [id]/+page.server.ts
│   │   │   └── new/+page.server.ts
│   │   ├── users/                        # Mismo patrón
│   │   │   ├── +page.server.ts           # loadUsers()
│   │   │   ├── [id]/+page.server.ts
│   │   │   └── new/+page.server.ts
│   │   ├── analytics/
│   │   │   ├── +page.svelte              # AnalyticsPage: charts + panels
│   │   │   └── +page.server.ts           # loadAnalytics()
│   │   └── assistant/
│   │       ├── +page.svelte              # Full-page chat variant
│   │       └── +server.ts                # GET /api/chat/stream — SSE proxy
│   ├── hooks.server.ts                   # handle(): refresh token interceptor + route guard
│   ├── hooks.client.ts                   # Theme init from localStorage, A11yToolbar restore
│   └── params.ts                         # Custom param matchers
├── tests/
│   ├── unit/
│   ├── integration/
│   └── e2e/
├── svelte.config.js                       # adapter-node for deployment flexibility
├── vite.config.ts
├── tsconfig.json
├── vitest.config.ts
└── playwright.config.ts
```

---

## BFF Contracts

### `fetchWithAuth()` — `src/lib/server/gateway.ts`

```typescript
// Wrapper central. Inyecta Authorization header desde la cookie httpOnly.
// Si 401 → intenta refresh via POST /api/auth/refresh (1 solo reintento)
// Si refresh ok → retry original request con nuevo token
// Si refresh fail → lanza RedirectException a /login
// Timeout por defecto: 8s
function fetchWithAuth(fetch: typeof globalThis.fetch, request: Request, endpoint: string, init?: RequestInit): Promise<Response>
```

### Contratos por ruta

| Ruta | Load Function | Gateway Endpoint | Transformación | Error Handling |
|------|--------------|-----------------|----------------|----------------|
| `/dashboard` | `loadDashboard()` | `GET /api/v1/dashboard/insights` | Mapea KPIs → `Insight[]`, trends, anomalías. Si 404 (no existe endpoint), fallback a composición manual: `GET /api/inventory/products?size=1` + `GET /api/stores?size=1` | ⚠️ Fallo completo → `error(503)`; fallo parcial → data parcial con `error` en sección |
| `/products` | `loadProducts({ url })` | `GET /api/inventory/products?page={page}&size={pageSize}&search={search}` | Mapea `Page<Product>` → `{ items: ProductListItem[], total, page }`. Renombra campos, agrega `trend` calculado | 401→refresh; 403→redirect /login; 500→`error(503)` |
| `/products/[id]` | `loadProduct({ params })` | `GET /api/inventory/products/{id}` | Mapea `Product` → `ProductDetail` con categoría resuelta y local asignado | 404→`error(404)`; 403→`error(403)` |
| `/products/[id]` (action) | `actions: PUT` | `PUT /api/inventory/products/{id}` | Pasa body validado. Retorna `{ success, product }` | 409→retorna error field-level |
| `/products/new` (action) | `actions: POST` | `POST /api/inventory/products` | Body validado. Retorna `{ success, product }` | 409→SKU duplicado en field |
| `/products/delete` (action) | `actions: DELETE` | `DELETE /api/inventory/products/{id}` | Retorna `{ success }` | 409→recurso en uso |
| `/stores` | `loadStores({ url })` | `GET /api/stores?page={page}&size={pageSize}&search={search}` | Mapea `Page<Store>` → `StoreListItem[]` | Ídem products |
| `/stores/[id]` | load + actions | `GET/PUT/DELETE /api/stores/{id}` | Ídem patrón | Ídem |
| `/categories` | `loadCategories({ url })` | `GET /api/inventory/categories?page={page}&size={pageSize}` | Mapea `Page<Category>` → `CategoryListItem[]` | Ídem |
| `/users` | `loadUsers({ url })` | `GET /api/auth/users?page={page}&size={pageSize}&tenant={tenantId}` | Mapea `Page<User>` → `UserListItem[]`. Filtra por tenant del usuario autenticado | 403 si no admin |
| `/analytics` | `loadAnalytics()` | `GET /api/v1/dashboard/insights?deep=true` | Mapea datos extendidos → chart-ready series + panels | Fallback a datos reducidos |
| `/assistant` | `GET /api/chat/stream` (SSE) | `GET /api/agent/chat/stream?message={msg}&context={route}&history={...}` | Proxy ReadableStream → transforma eventos SSE | Timeout 60s, reconexión 3 intentos |

### Patrón de load function típico

```typescript
// src/routes/products/+page.server.ts
import { fetchWithAuth } from '$lib/server/gateway';
import type { ProductListItem } from '$lib/types/inventory';

export async function load({ fetch, url, locals }) {
  const page = url.searchParams.get('page') ?? '1';
  const search = url.searchParams.get('search') ?? '';
  const pageSize = '20';

  const res = await fetchWithAuth(fetch, url, `/api/inventory/products?page=${page}&size=${pageSize}&search=${encodeURIComponent(search)}`);

  if (!res.ok) {
    if (res.status === 403) error(403, 'Sin permisos');
    if (res.status === 500) error(503, 'Servicio no disponible');
    error(res.status, res.statusText);
  }

  const body = await res.json();
  return {
    products: body.items.map(mapToProductListItem),
    total: body.total,
    page: body.page,
  };
}
```

---

## Auth Flow Design

### hooks.server.ts — handle()

```
Request entrante
  │
  ├─ ¿Ruta pública? (/login, /logout, /api/chat/*, /_app/*)
  │    └─ OK → pasa
  │
  ├─ Leer cookie "siga_token" (JWT)
  │    ├─ No existe → redirect /login?redirect={pathname}
  │    └─ Existe → decodificar payload (sin verificar, solo exp)
  │         ├─ exp < now + 5min → refresh token
  │         │    ├─ POST /api/auth/refresh (cookie "siga_refresh" en header)
  │         │    │    ├─ 200 → setea nueva cookie "siga_token", continua
  │         │    │    └─ 401 → limpia cookies, redirect /login
  │         │    └─ Race condition lock: si 2 requests simultáneos,
  │         │       solo 1 hace refresh, el otro espera y usa el nuevo token
  │         └─ exp > now + 5min → OK, setea event.locals.user = decoded
  │
  └─ event.locals.user disponible en todas las load functions
```

### Cookies

| Cookie | Tipo | Path | HttpOnly | Secure | SameSite | Duración |
|--------|------|------|----------|--------|----------|----------|
| `siga_token` | JWT access | `/` | Sí | Sí | Lax | 15 min (configurable) |
| `siga_refresh` | Refresh token | `/api/auth/refresh` | Sí | Sí | Strict | 7 días |

### login/+page.server.ts

```
actions.default(event):
  1. Validar email + password en cliente (requerido, formato email)
  2. POST /api/auth/login con { email, password }
     - El gateway prueba primero Customer, luego User (login dual)
  3. Si 401 → retornar { error: "Credenciales inválidas" } (mensaje genérico)
  4. Si 200 → { accessToken, refreshToken, principalType, ... }
     - Setear cookie "siga_token" = accessToken (httpOnly, secure, sameSite=lax, path=/, maxAge=15min)
     - Setear cookie "siga_refresh" = refreshToken (httpOnly, secure, sameSite=strict, path=/api/auth/refresh, maxAge=7d)
     - throw redirect(303, redirectParam || '/dashboard')
```

### Route Guards

Las rutas `(dashboard)/*` estan protegidas por el `hooks.server.ts`. Rutas públicas explícitas:

```
const PUBLIC_ROUTES = ['/login', '/logout', '/api/chat/stream'];
```

---

## SSE Streaming Design (A2UI)

### `src/routes/assistant/+server.ts`

```typescript
// GET /api/chat/stream?message=&context=&history=
export async function GET({ url, fetch }) {
  const message = url.searchParams.get('message');
  const context = url.searchParams.get('context');
  const history = url.searchParams.get('history'); // JSON string

  // 1. Conexión al siga-agent
  const agentUrl = new URL(`/api/agent/chat/stream`, GATEWAY_BASE);
  agentUrl.searchParams.set('message', message);
  agentUrl.searchParams.set('context', context);
  agentUrl.searchParams.set('history', history);

  const agentRes = await fetch(agentUrl.toString());

  // 2. Pipe de eventos: ReadableStream → transform → client
  const { readable, writable } = new TransformStream();
  const writer = writable.getWriter();
  const encoder = new TextEncoder();
  const decoder = new TextDecoder();

  pipeAgentStream(agentRes.body, writer, encoder, decoder);

  return new Response(readable, {
    headers: {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive',
    },
  });
}
```

### Protocolo de Eventos SSE

| Evento | Payload | Cuando |
|--------|---------|--------|
| `chunk` | `{ type: "chunk", content: string, done: false }` | Cada fragmento de respuesta |
| `done` | `{ type: "done", content: string, done: true }` | Respuesta completa |
| `error` | `{ type: "error", code: string, message: string }` | Error del agente |
| `tool` | `{ type: "tool", name: string, status: "running" \| "done" \| "error" }` | Tool call en ejecución |

### Reconexión (cliente)

```
ContextualAssistant.svelte:
  onSSEError:
    1. Esperar backoff: 1s → 2s → 4s (máx 3 intentos)
    2. Reenviar último mensaje (el server responde con idempotencia)
    3. Si agota → mostrar "Conexión perdida. Intenta de nuevo."
    4. Timeout global: 60s por mensaje
```

### Chat Store (runes)

```typescript
// src/lib/stores/chat.svelte.ts
let messages = $state<ChatMessage[]>([]);
let status = $state<'idle' | 'connecting' | 'streaming' | 'error'>('idle');
let abortController = $state<AbortController | null>(null);

const send = async (text: string, context?: string) => { ... };
const reconnect = () => { ... };
```

---

## Component Tree

```
+layout.svelte (auth)                         # Minimal, sin sidebar
└── LoginPage
    ├── Logo + branding
    ├── TabSwitcher: "Customer" | "User"
    ├── CustomerLoginForm
    │   ├── Input (email) + Input (password)
    │   └── Button "Iniciar sesión" [loading]
    └── UserLoginForm
        ├── Input (email) + Input (password)
        └── Button "Iniciar sesión" [loading]

+layout.svelte (dashboard)                    # Sidebar + Header + slot + floating assistant
├── Sidebar
│   ├── NavItem (icon, label, href, active, badge?)
│   │   ├── Dashboard     → /
│   │   ├── Productos      → /products
│   │   ├── Locales        → /stores
│   │   ├── Categorías     → /categories
│   │   ├── Usuarios       → /users
│   │   └── Analíticas     → /analytics
│   └── UserMenu (profile, config, logout)
├── Header
│   ├── Breadcrumb (items: {label, href}[])
│   ├── ThemeToggle (sun/moon icon)
│   └── A11yToolbar (highContrast, grayscale, largeFont, underlineLinks)
├── <slot/>  ← main content
│   ├── DashboardPage (/)                   # Props: insights, kpis, anomalies, trends
│   │   ├── Skeleton (loading state)
│   │   ├── KpiGrid
│   │   │   ├── InsightCard (title, value, trend, icon, variant)
│   │   │   ├── InsightCard (total productos)
│   │   │   ├── InsightCard (total locales)
│   │   │   └── InsightCard (valor inventario + trend badge)
│   │   ├── InsightPanel (productos stock bajo → link /products?filter=low-stock)
│   │   ├── AnomalyList (eventos recientes)
│   │   └── ChartWrapper (tendencia 7 días)
│   │
│   ├── ProductsPage (/products)            # Props: products, total, page
│   │   ├── SearchBar (value, onSearch)
│   │   ├── CrudTable<Product>
│   │   │   ├── columns: nombre, categoría, stock, precio, acciones
│   │   │   ├── actions: edit → /products/[id], delete → ConfirmDelete
│   │   │   └── pagination: server-side
│   │   ├── CrudForm<Product> (/products/[id] o /products/new)
│   │   │   ├── fields: nombre, sku, categoría, precio, stock, stockMin, local
│   │   │   └── mode: 'create' | 'edit'
│   │   └── ConfirmDelete (modal)
│   │
│   ├── StoresPage (/stores)                # Mismo patrón CrudTable + CrudForm
│   ├── CategoriesPage (/categories)        # Mismo patrón
│   ├── UsersPage (/users)                  # Mismo patrón
│   │
│   ├── AnalyticsPage (/analytics)
│   │   ├── ChartWrapper (lazy Chart.js, type: bar | line | pie | doughnut)
│   │   ├── ChartContainer (responsive, resize observer)
│   │   └── InsightPanel (texto analítico)
│   │
│   └── AssistantPage (/assistant)
│       └── ChatBubble[] + ChatInput + ToolIndicator
│
└── ContextualAssistant (floating, global)
    ├── AssistantFab (badge: online/offline)
    ├── ChatBubble[] (user/assistant, streaming state)
    ├── ChatInput (with debounce, Enter to send)
    └── ToolIndicator (name, status, pulse animation)
```

### Props key de componentes

| Componente | Props |
|-----------|-------|
| `Button` | `variant: 'primary'\|'secondary'\|'ghost'\|'danger'`, `size: 'sm'\|'md'\|'lg'`, `loading: boolean`, `disabled: boolean`, `type: 'button'\|'submit'` |
| `Input` | `type: string`, `label: string`, `value: string`, `error: string`, `placeholder: string`, `aria-describedby: string` |
| `Card` | `variant: 'default'\|'glass'`, `padding: 'sm'\|'md'\|'lg'` |
| `Modal` | `open: boolean`, `title: string`, `onClose: () => void`, `size: 'sm'\|'md'\|'lg'` |
| `Toast` | `type: 'success'\|'error'\|'info'\|'warning'`, `message: string`, `duration: number`, `onDismiss: () => void` |
| `CrudTable<T>` | `columns: ColumnDef<T>[]`, `data: T[]`, `total: number`, `page: number`, `pageSize: number`, `actions: ActionDef[]`, `loading: boolean` |
| `CrudForm<T>` | `fields: FieldDef<T>[]`, `onSubmit: (data: T) => Promise`, `initialValues: Partial<T>`, `mode: 'create'\|'edit'`, `errors: Record<string, string>` |
| `SearchBar` | `value: string`, `placeholder: string`, `onSearch: (q: string) => void`, `debounceMs: number` |
| `ChartWrapper` | `type: 'bar'\|'line'\|'pie'\|'doughnut'`, `data: ChartData`, `options: ChartOptions`, `loading: boolean`, `height: number` |
| `ContextualAssistant` | `mode: 'analyst'\|'operator'`, `currentRoute: string`, `position: 'floating'\|'fullpage'` |
| `ChatBubble` | `role: 'user'\|'assistant'`, `content: string`, `streaming: boolean`, `timestamp: Date` |
| `ToolIndicator` | `name: string`, `status: 'running'\|'done'\|'error'`, `label: string` |

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                          SvelteKit 5 App                            │
│                                                                     │
│  ┌──────────┐    ┌──────────────────────┐    ┌───────────────────┐  │
│  │  Client   │    │  Server (SvelteKit)   │    │   Gateway :8080    │  │
│  │  (.svelte)│    │                       │    │                   │  │
│  │           │    │  hooks.server.ts      │    │  /api/auth/**     │  │
│  │  Page     │───>│  ┌─────────────────┐  │───>│  → siga-auth      │  │
│  │  render   │    │  │ handle():       │  │    │                   │  │
│  │           │<───│  │ refresh + guard │  │<───│  /api/inventory/**│  │
│  │           │    │  └─────────────────┘  │    │  → siga-inventory │  │
│  │           │    │                       │    │                   │  │
│  │  CrudTable│    │  +page.server.ts      │    │  /api/stores/**   │  │
│  │  (data)   │<───│  ┌─────────────────┐  │───>│  → siga-inventory │  │
│  │           │    │  │ loadProducts()  │  │    │                   │  │
│  │  SearchBar│───>│  │ fetchWithAuth() │  │<───│  /api/agent/**    │  │
│  │  (debounce)│   │  └─────────────────┘  │    │  → siga-agent     │  │
│  │           │    │                       │    │                   │  │
│  │  Chat     │    │  +server.ts            │    └───────────────────┘  │
│  │  SSE      │<───│  ┌─────────────────┐  │          │               │
│  │  events   │    │  │ ReadableStream  │  │──────────┘               │
│  │  chunk    │    │  │ → SSE proxy     │  │                          │
│  │  done     │    │  └─────────────────┘  │                          │
│  │  error    │    │                       │                          │
│  │  tool     │    └──────────────────────┘                          │
│  └──────────┘                                                       │
└─────────────────────────────────────────────────────────────────────┘

Flujo de datos típico (CRUD):

1. Usuario navega a /products?page=1
2. SvelteKit ejecuta loadProducts() en el servidor
3. hooks.server.ts verifica JWT cookie → OK (y refresca si próximo a expirar)
4. fetchWithAuth() hace GET /api/inventory/products?page=1&size=20
   → Gateway → siga-inventory → DB → response
5. Servidor transforma respuesta → { products: ProductListItem[], total, page }
6. Client renderiza CrudTable con data server-composed
7. Usuario busca "harina" → debounce 300ms → URL cambia a ?search=harina
8. SvelteKit re-ejecuta loadProducts() con search param
9. Todo server-side, cero fetch del cliente
```

---

## A2UI Protocol Integration

### Dual-Mode Architecture

```
┌──────────────────────────────────────────────┐
│              SIGA WebApp                       │
│                                                 │
│  ┌──────────────┐   ┌──────────────────────┐  │
│  │ MODO CLÁSICO │   │  MODO AGENTIVO A2UI  │  │
│  │              │   │                      │  │
│  │ load fns     │   │ A2UIRenderer.svelte  │  │
│  │ rutas fijas  │   │ mapea type→component │  │
│  │ polling 60s  │   │ árbol reactivo       │  │
│  └──────┬───────┘   └─────────┬────────────┘  │
│         │                     │                │
│         └──────┬──────────────┘                │
│                ▼                               │
│  ┌──────────────────────────────┐             │
│  │  Catálogo de Componentes     │             │
│  │  (Card, Button, CrudTable,   │             │
│  │   ChartWrapper, Insight, etc)│             │
│  └──────────────────────────────┘             │
│                                                 │
│  ┌──────────────────────────────┐             │
│  │  SSE (extendido)             │             │
│  │  {type:"text"} → burbuja     │             │
│  │  {type:"a2ui"} → renderer    │             │
│  │  {type:"update"} → parche    │             │
│  └──────────────────────────────┘             │
└──────────────────────────────────────────────┘
```

### A2UI Component Catalog

| A2UI Type | Componente | Props |
|-----------|-----------|-------|
| card | Card.svelte | variant, padding, header, children, footer |
| button | Button.svelte | variant, size, loading, disabled |
| input | Input.svelte | type, label, error, placeholder |
| crud-table | CrudTable.svelte | columns, data, total, page, actions |
| crud-form | CrudForm.svelte | fields, onSubmit, initialValues, mode |
| chart | ChartWrapper.svelte | type, data, options, loading, height |
| insight-panel | InsightPanel.svelte | insights, variant |
| anomaly-list | AnomalyList.svelte | anomalies, emptyMessage |
| search-bar | SearchBar.svelte | value, placeholder, onSearch |
| badge | Badge.svelte | variant, children |
| modal | Modal.svelte | open, title, onClose |
| spinner | Spinner.svelte | size, variant |
| skeleton | Skeleton.svelte | variant |
| container | — (div wrapper) | layout, gap, direction |

### A2UI Renderer Design

```
A2UIRenderer.svelte:
  Props: tree: A2UINode | A2UINode[]
  State: expandedNodes: Map<string, any> (reactivo)

  Función renderNode(node):
    match node.type:
      "card" → <Card {...node.props}><renderNode children/></Card>
      "button" → <Button {...node.props} />
      "chart" → <ChartWrapper {...node.props} />
      ...

  Manejo de actualizaciones:
    - Si llega evento "a2ui" con tree completo → reemplazar
    - Si llega evento "update" con nodeId + props → merge parcial
    - Si llega evento "patch" con nodeId + children → reemplazar children
```

### SSE Protocol Extension

```typescript
// Eventos SSE actuales (F3) + extensiones A2UI
type SSEEvent =
  | { type: "chunk"; content: string; done: false }
  | { type: "done"; content: string; done: true }
  | { type: "error"; code: string; message: string }
  | { type: "tool"; name: string; status: "running" | "done" | "error" }
  // NUEVOS:
  | { type: "a2ui"; tree: A2UINode; action: "replace" | "append" }
  | { type: "update"; nodeId: string; props: Record<string, unknown> }
  | { type: "patch"; nodeId: string; children: A2UINode[] }
```

### Mode Transition Flow

```
1. Usuario en dashboard clásico
2. Click "Ahorremos tiempo: SIGA" (en Header o FAB)
3. isA2UIMode = true (store reactivo)
4. ContextualAssistant se expande
5. Contenido principal pasa a A2UIRenderer
6. Se envía mensaje al agente con contexto actual
7. Agente responde con payload A2UI
8. Renderizador muestra UI generativa
9. Usuario conversa y la UI se reshapea dinámicamente
```

### A2UI State Store

```typescript
// src/lib/stores/a2ui.svelte.ts
let mode = $state<'classic' | 'a2ui'>('classic');
let tree = $state<A2UINode | null>(null);
let selectedNodeId = $state<string | null>(null);

function enterAgentiveMode(context: { route: string; data?: unknown }): void { ... }
function exitAgentiveMode(): void { ... }
function updateTree(node: A2UINode, action: 'replace' | 'append'): void { ... }
function patchNode(nodeId: string, props: Record<string, unknown>): void { ... }
```

### Component Tree (actualización)

Agregar bajo el dashboard layout:

```
├── A2UIRenderer (solo en modo agentivo)
│   └── [árbol dinámico mapeado del payload A2UI]
├── Botón "Ahorremos tiempo" (Header o FAB global)
```

### Responsive Design (A2UI)

El diseño responsive es **fundacional**, no un afterthought. Tres breakpoints:

| Breakpoint | Viewport | Layout A2UI | Chat |
|------------|----------|-------------|------|
| **Mobile** | < 768px | Stack vertical 1 columna. Cards full-width. Container hints `stack`. | Bottom sheet anclado abajo. FAB minimizado. |
| **Tablet** | 768-1024px | Grilla 2 columnas. Sidebar colapsable. Container hints `grid columns: 2`. | Sidebar colapsable a derecha, o FAB. |
| **Desktop** | > 1024px | Grilla 3-4 columnas. Container hints `grid columns: {desktop: 3|4}`. | Flotante (FAB expandible), posición default derecha. |

#### A2UI Layout Hints (payload del agente)

El agente puede sugerir layout, pero el renderizador siempre adapta al viewport:

```json
{
  "type": "container",
  "props": {
    "layout": "grid",
    "columns": { "desktop": 3, "tablet": 2, "mobile": 1 },
    "gap": "md"
  },
  "children": [
    { "type": "insight-panel", "props": { ... } },
    { "type": "chart", "props": { ... } },
    { "type": "anomaly-list", "props": { ... } }
  ]
}
```

#### Mobile-specific behavior

- **ContextualAssistant** se transforma en bottom sheet con handle de drag. Ocupa ~60% de la pantalla al abrirse, cubre 100% en foco (input activo).
- **Touch targets**: todos los botones, links y elementos interactivos A2UI respetan mínimo 44x44px (WCAG 2.1 SC 2.5.8).
- **Swipe gestures**: las cards A2UI pueden swipearse horizontalmente en contenedores `scroll-x`.
- **Pull-to-refresh**: en el árbol A2UI, pull-to-refresh reenvía el último mensaje al agente.
- **Keyboard avoidance**: cuando el teclado virtual está abierto, el bottom sheet del chat se achica automáticamente.

#### Tablet-specific behavior

- **Sidebar colapsable**: el chat puede mostrarse como sidebar derecha (350px) colapsable a icono.
- **Split view**: en horizontal (1024px), la UI agentiva muestra 2 columnas + sidebar opcional.
- **Drag & drop**: en tablets, el usuario puede reorganizar cards A2UI con drag (ideal para iPads con Stage Manager).

#### CSS Strategy

```css
/* Ya existe en app.css via CSS custom properties + media queries.
   Extender para A2UI: */

/* A2UI container responsive grid */
.a2ui-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-md);
}

@media (max-width: 768px) {
  .a2ui-grid {
    grid-template-columns: 1fr;
  }
  .a2ui-chat {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    max-height: 60vh;
    border-radius: var(--radius-lg) var(--radius-lg) 0 0;
    z-index: var(--z-drawer);
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .a2ui-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
```

El renderizador A2UI aplica clases según `layout` hint + viewport. Nunca fuerza un layout que no entre en la pantalla.

### Testing

Agregar los siguientes archivos de test:

| Test | Archivo | Qué cubre |
|------|---------|-----------|
| Unit: A2UIRenderer | `tests/unit/components/a2ui/A2UIRenderer.test.ts` | Renderiza payloads A2UI, mapeo type→component, actualizaciones replace/update/patch |
| Unit: A2UI Store | `tests/unit/stores/a2ui.test.ts` | Mode transitions (classic↔a2ui), tree updates, patching |
| Integration: SSE A2UI | `tests/integration/bff/a2ui.stream.test.ts` | SSE con payloads A2UI, transformación de eventos, manejo de errores |

---

## Testing Architecture

### Vitest (Unit + Integration)

```
tests/unit/
├── components/
│   ├── Button.test.ts           # Renderiza variantes, verifica loading state
│   ├── CrudTable.test.ts        # Renderiza datos mockeados, paginación, sort
│   ├── CrudForm.test.ts         # Validación cliente, submit, errores servidor
│   ├── LoginForm.test.ts        # Submit con credenciales, estado error
│   ├── ChatBubble.test.ts       # Render streaming vs completo, roles
│   ├── SearchBar.test.ts        # Debounce 300ms, emisión onSearch
│   └── Modal.test.ts            # Focus trap, Escape cierra, aria-modal
├── stores/
│   ├── auth.test.ts             # $state user, $derived isAuthenticated
│   ├── theme.test.ts            # toggle(), init(), localStorage mock
│   ├── toast.test.ts            # add(), remove(), autoDismiss
│   └── chat.test.ts             # send(), reconnect(), status transitions
└── utils/
    ├── validators.test.ts       # isEmail, isRequired, boundary cases
    └── formatters.test.ts       # formatCurrency, formatDate, edge cases

tests/integration/
├── bff/
│   ├── gateway.test.ts          # fetchWithAuth: 401→refresh→retry, timeout, errors
│   ├── products.load.test.ts    # Mock gateway, verifica transformación
│   ├── dashboard.load.test.ts   # Composición de insights, fallback parcial
│   └── chat.load.test.ts        # SSE proxy, transformación de eventos
└── auth/
    ├── login.flow.test.ts       # Login dual, seteo de cookies, redirect
    └── refresh.flow.test.ts     # Refresh exitoso, fallido, race condition
```

### Playwright (E2E)

```
tests/e2e/
├── auth.spec.ts                 # Login → redirect → logout → login again
├── products.spec.ts             # CRUD completo: create → edit → delete
├── dashboard.spec.ts            # KPIs visibles, skeleton → data, polling
├── chat.spec.ts                 # Send message → streaming response → tool indicator
└── accessibility.spec.ts        # axe-core AA en login, dashboard, products
```

### Mocks necesarios

| Mock | Propósito | Scope |
|------|-----------|-------|
| `$lib/server/gateway` mock | Simula fetchWithAuth sin red | Unit + Integration |
| `Response` stream mock | Simula SSE events para chat | Integration |
| `cookies` mock | Simula httpOnly cookies | Auth tests |
| `locals` mock | Simula `event.locals.user` | Load function tests |
| Chart.js mock | Evita render real de canvas | Component tests |
| `localStorage` mock | Persistencia de preferencias | Store tests |

### Estrategia RED-GREEN-REFACTOR

1. **RED**: Escribir test que falla (componente no existe / funcionalidad no implementada)
2. **GREEN**: Implementar mínimo necesario para que pase
3. **REFACTOR**: Mejorar sin romper tests

```bash
# CI pipeline
npx vitest run          # Unit + Integration (mínimo 70% coverage)
npx playwright test     # E2E (contra preview build)
```

---

## Migration / Rollout

No migration de datos requerida. Estrategia:

1. **Fase 1-4**: Desarrollo en `packages/frontend/` en rama `migracion-microservicios`. Gateway apunta a servicios existentes.
2. **Convivencia**: Los 5 frontends legacy (`apps/*`) siguen funcionando. El nuevo frontend se despliega en ruta separada (ej: `/app/v2`).
3. **Corte (Fase 5)**: Feature flag habilita nuevo frontend para todos los tenants. Legacy se depreca. DNS corta a nuevo frontend.
4. **Rollback**: Desactivar feature flag → tráfico vuelve a legacy. Sin pérdida de datos.

---

## Open Questions

- [ ] Endpoint `GET /api/v1/dashboard/insights` — ¿quién lo implementa? ¿gateway compuesto o nuevo microservicio? Diseño contempla fallback a composición BFF.
- [ ] Refresh token endpoint `POST /api/auth/refresh` — el backend team debe implementar antes de Fase 1. ¿Dónde está el spec del endpoint exacto? (path, request/response bodies)
- [ ] SSE endpoint en siga-agent — el spec `ui-a2ui` asume `GET /api/agent/chat/stream`. Confirmar path exacto y formato de eventos que emite el agente.
- [ ] Gateway `application.yml` no tiene ruta `dashboard` ni `categories` explícita — ¿se agregan o se usan las existentes?
- [ ] Testing: ¿se requiere axe-core en CI? De ser así, integrar en playwright.config.ts con `@axe-core/playwright`.

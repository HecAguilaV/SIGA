# Tasks: Frontend desde Cero

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~5500–6000 |
| 400-line budget risk | High |
| Chained PRs recommended | No (sin PRs — commit directo a branch) |
| Delivery strategy | commit-only |
| Suggested split | 5 fases, 29 tareas, 1+ commit por tarea |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: size-exception
400-line budget risk: High

### Work Units (Commits Estratégicos)

| Unit | Commits | Entregable visible |
|------|---------|-------------------|
| F1 | ~8 | Login funcional + design system corriendo |
| F2 | ~10 | Dashboard + 4 CRUDs operativos |
| F3 | ~5 | Chat A2UI streaming en vivo |
| F4 | ~4 | Analytics con gráficos |
| F5 | ~2 | Legacy deprecado |

---

## Phase 1: Scaffold + Auth

- [x] **T-01** Scaffold SvelteKit 5 — `packages/frontend/` init, `package.json`, `svelte.config.js`, `vite.config.ts`, `tsconfig.json`, `vitest.config.ts`, `playwright.config.ts`, `app.html`, `app.d.ts`
- [x] **T-02** Design tokens CSS — `app.css` con CSS custom properties en `:root`/`[data-theme="dark"]`, glassmorphism, tipografía Inter+JetBrains Mono, variantes accent
- [x] **T-03** Theme store (`theme.svelte.ts`) + `ThemeToggle.svelte` — writable store, localStorage persist, `prefers-color-scheme` detection
- [x] **T-04** UI atoms — `Button.svelte`, `Input.svelte`, `Card.svelte`, `Modal.svelte`, `Toast.svelte`, `Spinner.svelte`, `Badge.svelte`, `Skeleton.svelte`
- [x] **T-05** Toast store (`toast.svelte.ts`) — writable store Toast[], add/remove/autoDismiss
- [x] **T-06** `gateway.ts` (fetchWithAuth) + `auth.server.ts` — JWT injection, 401→refresh→retry, timeout 8s
- [x] **T-07** `hooks.server.ts` — handle() con refresh interceptor (5min umbral), route guards, race condition lock
- [x] **T-08** Auth store (`auth.svelte.ts`) — writable store user, derived isAuthenticated
- [x] **T-09** Login page — `(auth)/login/+page.svelte` (login dual Customer→User) + `+page.server.ts` actions.default (setea httpOnly cookies, fallback mock)
- [x] **T-10** Logout — `(auth)/logout/+page.server.ts` actions.default, limpia cookies, redirect
- [x] **T-11** Tests F1 — `Button.test.ts`, `auth.test.ts`, `theme.test.ts`, `toast.test.ts`, `gateway.test.ts`, `login.flow.test.ts` (Vitest), `auth.spec.ts` (Playwright E2E) — **52 tests, all passing**

## Phase 2: Core CRUDs

- [x] **T-12** Move packages/frontend/ → apps/dashboard/ + extract ui-kit + shared
- [x] **T-13** Layout shell — `(dashboard)/+layout.svelte` (Sidebar+Header+slot+ContextualAssistant) + `+layout.server.ts` (session verify + role guards)
- [x] **T-14** `Sidebar.svelte` — NavItem[], collapsed, active highlight, UserMenu (logout), role-aware hiding
- [x] **T-15** `Header.svelte` + `Breadcrumb.svelte` + `ThemeToggle.svelte` + `A11yToolbar.svelte` (rescatado legacy)
- [x] **T-16** `CrudTable.svelte` genérico — ColumnDef[], data, paginación server-side, loading/empty slots
- [x] **T-17** `SearchBar.svelte` — debounce 300ms, URL query param sync
- [x] **T-18** `CrudForm.svelte` genérico + `ConfirmDelete.svelte` — FieldDef[], validación cliente+servidor
- [x] **T-19** Products CRUD — `routes/products/` (list, create, edit, delete) con load functions + actions + mock fallback
- [x] **T-20** Stores CRUD — `routes/stores/` (mismo patrón)
- [x] **T-21** Categories CRUD — `routes/categories/` (mismo patrón)
- [x] **T-22** Users CRUD — `routes/users/` (mismo patrón, filtro por tenant)
- [x] **T-23** Tests F2 — `CrudTable.test.ts`, `CrudForm.test.ts`, `Sidebar.test.ts`, `products.load.test.ts`, `dashboard.load.test.ts`, `crud-products.spec.ts` (Playwright)

## Phase 3: A2UI Streaming

- [x] **T-24** SSE proxy — `routes/assistant/+server.ts` GET /api/chat/stream, ReadableStream pipe desde siga-agent, transformación eventos SSE (chunk/done/error/tool)
- [x] **T-25** Chat store (`chat.svelte.ts`) — `$state messages[]`, status idle|connecting|streaming|error, send(), reconnect() con backoff, abortController
- [x] **T-26** Chat components — `ChatBubble.svelte` (roles, streaming indicator), `ChatInput.svelte` (debounce spam, Enter), `ToolIndicator.svelte` (pulse anim), `AssistantFab.svelte` (online/offline badge)
- [x] **T-27** `ContextualAssistant.svelte` — FAB global en dashboard layout, modo analyst/operator, reconexión 1s/2s/4s, timeout 60s
- [x] **T-28** Tests F3 — `chat.test.ts` (store transitions), `ChatBubble.test.ts`, `chat.load.test.ts` (SSE mock)

## Phase 4: Insights & Analytics

- **T-29** Chart wrappers — `ChartWrapper.svelte` (genérico bar/line/pie/doughnut) + `ChartContainer.svelte` (resize observer, skeleton), Chart.js lazy load rescatado
- **T-30** Analytics page — `routes/analytics/+page.server.ts` (loadAnalytics) + `+page.svelte` (gráficos + InsightPanel)
- **T-31** Dashboard extendido — InsightPanel (hallazgos), AnomalyList, ChartWrapper tendencia 7 días
- **T-32** Tests F4 — `ChartWrapper.test.ts`, `analytics.load.test.ts`, `accessibility.spec.ts` (axe-core AA en login/dashboard/products)

## Phase 5: Legacy Burial

- **T-33** Deprecation notices — `NOTICE.md` + banner "DEPRECATED" en README de cada `apps/*`
- **T-34** CI/CD cleanup — Workflows legacy desactivados, CI apunta a `packages/frontend/`, limpiar referencias en docs
- **T-35** Archive legacy — Mover `apps/webapp`, `admin-portal`, `customer-portal`, `mobile`, `landing` a `archived/` con cross-ref al nuevo frontend

---

## Dependencias entre tareas

```
F1 completa → F2 (layout depende de auth, tokens)
F2 completa → F3 (chat layout depende de dashboard layout)
F2 completa → F4 (analytics usa CrudTable+ChartWrapper)
F1..F4 completas → F5 (no archivar hasta migración completa)
```

## Entregable mínimo viable por fase

- **Post-F1**: Login funcional + design system visible + navegación protegida — el usuario puede loguearse y ver la UI
- **Post-F2**: Dashboard con KPIs reales + CRUD products operativo — el usuario puede gestionar su negocio
- **Post-F3**: Chat A2UI funcional en todas las rutas — el usuario conversa con siga-agent
- **Post-F4**: Analytics con gráficos + dashboard enriquecido — visibilidad completa del negocio
- **Post-F5**: Zero legacy en CI/CD — solo el nuevo frontend importa

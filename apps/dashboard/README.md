# Dashboard — SIGA

**Estado**: En desarrollo 🏗️
**Stack**: SvelteKit 5 + TypeScript + pnpm
**Para quién**: Dueños de PYME y sus empleados (admin, operador, cajero)
**Propósito**: Gestionar el negocio — productos, stock, ventas, chat A2UI, analytics

## Funcionalidades
- Login dual (Customer → User) con refresh token
- Dashboard con insights y KPIs
- CRUD de productos, locales, categorías, usuarios
- Chat A2UI con streaming SSE
- Analytics y gráficos
- POS (Punto de Venta) — integrado como ruta

## F1: Scaffold + Auth + Design System ✅
Scaffold SvelteKit 5, design tokens CSS con glassmorphism, auth flow con httpOnly cookies y refresh automático, UI atoms (Button, Input, Card, Modal, Toast, Spinner, Badge, Skeleton). 52 tests F1.

## F2: Core CRUDs ✅
Dashboard layout con Sidebar + Header, componentes CRUD genéricos (CrudTable, CrudForm, SearchBar, ConfirmDelete), páginas CRUD completas para Products, Stores, Categories, Users con load functions server-side y mock fallback. 78 tests F1+F2.

## F3: A2UI Streaming ✅
Chat A2UI con streaming SSE via `ReadableStream` proxy desde siga-agent. Arquitectura de componentes:

- **SSE Proxy** (`routes/assistant/+server.ts`) — endpoint GET `/api/chat/stream` que conecta con siga-agent, transforma eventos (chunk/done/error/tool) y retorna `ReadableStream` SSE. Timeout 60s.
- **Chat Store** (`stores/chat.svelte.ts`) — store con runes `$state`: messages[], status (idle|connecting|streaming|error), send(), reconnect() con backoff exponencial (1s → 2s → 4s, máx 3), cancel() con AbortController.
- **Componentes A2UI** (`components/a2ui/`):
  - `ChatBubble.svelte` — burbujas user/assistant con dots animados de streaming y timestamp.
  - `ChatInput.svelte` — textarea con auto-resize, Enter to send, Shift+Enter newline, anti-spam debounce 2s.
  - `ToolIndicator.svelte` — indicador de herramienta con animación pulse-spin (running/done/error).
  - `AssistantFab.svelte` — FAB con badge online/offline y contador de no leídos.
  - `ContextualAssistant.svelte` — widget flotante global (FAB + chat), integrado en dashboard layout, modo analyst/operator, reconexión automática, timeout 60s, envía ruta actual como contexto.
- **Full-page chat** (`routes/assistant/+page.svelte`) — página completa para conversaciones largas.
- **Tests F3** — store, components, SSE proxy integration.

## Desarrollo

```bash
cd apps/dashboard
pnpm install
pnpm dev
```

## Dependencias
- Gateway `:8080` para API
- `packages/ui-kit` para componentes compartidos
- `packages/shared` para types y validators

# Dashboard — Status

## F1: Scaffold + Auth + Design System — DONE
Scaffold SvelteKit 5, design tokens CSS (light/dark theme, glassmorphism), auth flow with httpOnly cookies + automatic refresh, UI atoms (Button, Input, Card, Modal, Toast, Spinner, Badge, Skeleton), theme store, toast store, auth store, gateway.ts fetchWithAuth. 52 tests passing.

## F2: Core CRUDs — DONE
Dashboard layout with sidebar + header, CRUD components (CrudTable, CrudForm, SearchBar, ConfirmDelete), full CRUD pages for Products, Stores, Categories, Users with server-side load functions and mock fallback. 78 tests passing (F1+F2).

## F3: A2UI Streaming — DONE
SSE proxy to siga-agent via Gateway (`/api/agent/chat/stream`), ContextualAssistant FAB floating widget, chat components (ChatBubble, ChatInput, ToolIndicator, AssistantFab), auto-reconnect with backoff (1s → 2s → 4s, max 3 retries), 60s timeout, full-page chat at `/assistant`. 85 tests passing.

## F4: Insights & Analytics — DONE
ChartWrapper, ChartContainer, AnalyticsPage, InsightPanel. Integration with `siga-agent` for predictive narratives. Real-time data from `siga-sales` and `siga-inventory`.

## F5: POS UI — DONE
Complete POS interface with real-time SAGA feedback via SSE. Real shift management (Caja). Search, cart, and multi-payment support. Zero hardcoded data.

## Próximas fases

- **F6**: Platform Admin — /(platform)/ for SIGA subscription management.
- **F7**: Legacy Burial — deprecation notices, CI/CD cleanup, archive legacy apps.

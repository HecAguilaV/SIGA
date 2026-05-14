# Dashboard — Status

## F1: Scaffold + Auth + Design System ✅
Scaffold SvelteKit 5, design tokens CSS (light/dark theme, glassmorphism), auth flow with httpOnly cookies + automatic refresh, UI atoms (Button, Input, Card, Modal, Toast, Spinner, Badge, Skeleton), theme store, toast store, auth store, gateway.ts fetchWithAuth. 52 tests passing.

## F2: Core CRUDs ✅
Dashboard layout with sidebar + header, CRUD components (CrudTable, CrudForm, SearchBar, ConfirmDelete), full CRUD pages for Products, Stores, Categories, Users with server-side load functions and mock fallback. 78 tests passing (F1+F2).

## F3: A2UI Streaming ✅ (just implemented)
SSE proxy to siga-agent via Gateway (`/api/agent/chat/stream`), ContextualAssistant FAB floating widget, chat components (ChatBubble, ChatInput, ToolIndicator, AssistantFab), auto-reconnect with backoff (1s → 2s → 4s, max 3 retries), 60s timeout, full-page chat at `/assistant`. [N] tests passing.

## Próximas fases

- **F4**: Insights & Analytics — ChartWrapper, ChartContainer, AnalyticsPage, InsightPanel
- **F5**: Legacy Burial — deprecation notices, CI/CD cleanup, archive legacy apps

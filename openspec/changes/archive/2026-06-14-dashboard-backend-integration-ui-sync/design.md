# Design: Dashboard Backend Integration and UI Sync

## Technical Approach

The design focuses on two main pillars: visual alignment with the Stitch design system (M3) and the transition from mock data to real backend integration via a BFF (Backend For Frontend) pattern.

The frontend will adopt a token-driven approach for styling, utilizing CSS variables that map to the Stitch M3 palette. The layout will be refactored into a Bento Grid structure using CSS Grid primitives, providing a modern and organized operational view.

On the backend, the `gateway` service will be extended to act as an aggregator (BFF), providing a single `/api/v1/dashboard/insights` endpoint. this endpoint will orchestrate calls to `sales` (for performance metrics) and `inventory` (for stock alerts and status), simplifying the frontend data acquisition.

## Architecture Decisions

### Decision: BFF Aggregator in Gateway

**Choice**: Implement dashboard aggregation logic in the `gateway` service.
**Alternatives considered**: Direct frontend calls to multiple services, or a dedicated "Dashboard Service".
**Rationale**: Direct calls increase latency and frontend complexity. A dedicated service adds overhead. Gateway already handles routing and security, making it the ideal place for lightweight aggregation (BFF).

### Decision: Repository-Level Time-Series Aggregation

**Choice**: Perform sales data aggregation (daily/weekly/monthly) at the repository level in the `sales` service.
**Alternatives considered**: Aggregating in-memory in the service layer.
**Rationale**: Database-level aggregation is significantly more performant for large datasets and reduces the volume of data transferred between layers.

### Decision: Glassmorphism via Utility Classes

**Choice**: Define glassmorphism effects (backdrop-filter, semi-transparent borders) as utility classes in `app.css`.
**Alternatives considered**: Inline styles or hardcoded properties in every component.
**Rationale**: Promotes reuse and consistency while keeping the Svelte component logic focused on behavior rather than style details.

## Data Flow

Data flows from the microservices through the Gateway BFF to the Dashboard UI.

```
[Dashboard UI] ──(GET /api/v1/dashboard/insights)──→ [Gateway BFF]
                                                         │
                                           ┌─────────────┴─────────────┐
                                           ↓                           ↓
                                    [Sales Service]           [Inventory Service]
                                   (Time-series Data)         (Alerts & Metadata)
```

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `apps/dashboard/src/app.css` | Modify | Sync with Stitch M3 tokens and add layout utilities. |
| `apps/dashboard/src/lib/components/layout/BentoGrid.svelte` | Create | New layout primitive for dashboard widgets. |
| `services/gateway/src/main/.../DashboardInsightsService.java` | Create | Aggregation logic for the insights endpoint. |
| `services/sales/src/main/.../SalesTimeSeriesRepository.java` | Create | JQL/SQL for time-series data aggregation. |
| `services/inventory/src/main/.../CategoryAdapter.java` | Modify | Implement Hexagonal adapters for Category CRUD. |

## Interfaces / Contracts

### Dashboard Insights DTO (Gateway)
```typescript
interface DashboardInsights {
  dailySales: number;
  salesGrowth: number;
  criticalStockAlerts: number;
  activeStores: number;
  topCategories: Array<{name: string, value: number}>;
}
```

### Sales Analytics Endpoint
`GET /api/v1/sales/analytics?period=DAILY&range=30`

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | Aggregation Logic | JUnit tests for Sales service repo and service layers. |
| Integration | Gateway BFF | WireMock to simulate Sales/Inventory responses in Gateway tests. |
| E2E | Dashboard Rendering | Playwright tests verifying the UI renders real-time data correctly. |

## Migration / Rollout

No data migration required. Feature is additive. Rollout will be managed via a backend configuration toggle that controls whether the Gateway points to the real services or a mock provider.

## Open Questions

- [ ] Should the Gateway cache insights data, and if so, for how long (TTL)?
- [ ] Do we need real-time updates via WebSockets for the "Critical Stock Alerts" widget?

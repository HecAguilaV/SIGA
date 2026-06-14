# Tasks: Dashboard Backend Integration and UI Sync

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | 600 - 800 |
| 400-line budget risk | High |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (UI Foundations) → PR 2 (Backend Services) → PR 3 (Integration) |
| Delivery strategy | auto-chain |
| Chain strategy | stacked-to-main |

Decision needed before apply: No
Chained PRs recommended: Yes
Chain strategy: stacked-to-main
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Notes |
|------|------|-----------|-------|
| 1 | UI Tokens & Styling (app.css sync) | PR 1 | Base M3 tokens, glassmorphism, and bento grid layout |
| 2 | Backend: Sales Time-series & Insights | PR 2 | New endpoints in Sales and Gateway aggregator |
| 3 | Frontend: API Connection | PR 3 | Connecting UI to real backend endpoints |

## Phase 1: UI Foundation & Tokens

- [x] 1.1 Synchronize `apps/dashboard/src/app.css` with Stitch design tokens (M3 color palette, typography).
- [x] 1.2 Implement Bento Grid layout primitives using CSS Grid in `apps/dashboard/src/lib/components/layout/`.
- [x] 1.3 Apply Glassmorphism utility classes (backdrop-filter, border-transparency) to Dashboard Card components.
- [x] 1.4 Refactor `Dashboard.svelte` to use the new Bento Grid and Glassmorphism components.

## Phase 2: Backend - Sales Analytics & Inventory Ports

- [x] 2.1 Implement `SalesTimeSeriesRepository` in `services/sales` to support daily/weekly/monthly aggregation.
- [x] 2.2 Create `SalesAnalyticsService` and `/api/v1/sales/analytics` endpoint in `services/sales`.
- [x] 2.3 Expose Category and Store management CRUD operations in `services/inventory` (Hexagonal adapters).
- [x] 2.4 Update `services/inventory` API to support soft-delete (deactivation) for Stores.

## Phase 3: Backend - Gateway Aggregator (Insights)

- [x] 3.1 Define `DashboardInsights` DTOs in `services/gateway`.
- [x] 3.2 Implement `DashboardInsightsService` in `services/gateway` using OpenFeign to aggregate data from Sales and Inventory.
- [x] 3.3 Create `/api/v1/dashboard/insights` endpoint in `services/gateway` with `DASHBOARD_VIEW` permission check.
- [x] 3.4 Implement graceful degradation in the aggregator when Sales or Inventory services are down.

## Phase 4: Frontend Integration & API Connection

- [x] 4.1 Replace mock data in `apps/dashboard/src/lib/stores/dashboard.ts` with real `fetch` calls to Gateway Insights API.
- [x] 4.2 Connect Sales time-series charts to the real Sales Analytics API.
- [x] 4.3 Update Store and Category management views to use real Inventory API endpoints.
- [x] 4.4 Implement error handling and "No Data" placeholders for all dashboard widgets.

## Phase 5: Testing & Verification

- [x] 5.1 Unit tests for Sales time-series aggregation logic in `services/sales`.
- [x] 5.2 Integration tests for Gateway Insights aggregator using WireMock for downstream services.
- [x] 5.3 Playwright tests for Dashboard UI: verify M3 alignment and successful data rendering.
- [x] 5.4 Verify access control (403 Forbidden) for unauthorized users on Insights and Sales Analytics.

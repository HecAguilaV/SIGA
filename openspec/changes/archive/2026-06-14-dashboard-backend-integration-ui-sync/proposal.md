# Proposal: Dashboard Backend Integration and UI Sync

## Intent

Synchronize the dashboard UI with the Stitch design system (M3 palette, glassmorphism, bento grid) and integrate real-time backend data for insights and sales analytics, replacing current mock implementations with functional API connections.

## Scope

### In Scope
- Dashboard UI redesign using Stitch design tokens and M3 color palette.
- Implementation of Glassmorphism effects and Bento Grid layout in the web dashboard.
- Creation of `/api/v1/dashboard/insights` endpoint for data aggregation.
- Implementation of time-series sales analytics in the sales service.
- Integration of Category and Store management UI with real Inventory API endpoints.

### Out of Scope
- Mobile application UI synchronization.
- Advanced predictive AI forecasting (reserved for future phases).
- Full domain refactor of the inventory microservice.

## Capabilities

### New Capabilities
- `dashboard-insights`: Backend support for aggregated dashboard metrics and summary data.
- `sales-analytics`: Time-series data processing and retrieval for sales performance tracking.

### Modified Capabilities
- `inventory`: Update API and ports to support category and store management operations from the dashboard.

## Approach

High-level technical approach based on the exploration:
- **Frontend**: Update `apps/dashboard` with Svelte components following M3 design specs. Implement a bento-grid layout using CSS Grid and glassmorphism via Backdrop Filter.
- **Backend**: Implement the `/api/v1/dashboard/insights` endpoint in the `gateway` or a new aggregator service using `OpenFeign` to pull data from `sales` and `inventory`.
- **Sales Service**: Extend the sales domain to include time-series aggregation logic (daily/weekly/monthly).
- **Inventory Service**: Expose Store and Category management via standard Hexagonal adapters.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `apps/dashboard/src/` | Modified | New layout, components, and API integration. |
| `services/sales/` | Modified | New time-series analytics logic and ports. |
| `services/inventory/` | Modified | Category and Store management adapters. |
| `services/gateway/` | New | Dashboard insights aggregator endpoint. |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Data aggregation latency | Med | Implement caching for non-real-time metrics. |
| UI breakage during M3 migration | Low | Playwright regression testing for core workflows. |
| Service unavailability | Med | Graceful degradation and clear error states in UI. |

## Rollback Plan

Revert git changes to a stable tag. Maintain a feature flag or a configuration toggle to switch back to mock data stores if backend services fail to meet performance requirements.

## Dependencies

- `inventory` service Category/Store API readiness.
- Stitch Design tokens availability (CSS variables).

## Success Criteria

- [ ] Dashboard displays live data from `/api/v1/dashboard/insights`.
- [ ] UI alignment with Stitch design (verified by UX review).
- [ ] CRUD operations for categories and stores persist in the database.
- [ ] Sales time-series charts render correctly with backend data.

## Proposal question round

*Questions intended to improve the proposal and uncover business/product tradeoffs.*

1. **Business Problem**: Is the primary goal of "insights" for immediate operational reaction or for long-term strategic reporting?
   * *Assumption*: Operational reaction (stock alerts, daily sales trends).
2. **Business Rules**: Should "Store Management" include the ability to deactivate stores, or only edit details?
   * *Assumption*: Include deactivation (soft delete) to maintain data integrity.
3. **Product Outcome**: Is there a specific "hero metric" that must be most prominent in the Bento Grid?
   * *Assumption*: Daily Total Sales and Critical Stock Alerts.
4. **Edge Cases**: How should the UI handle stores with zero sales data for a selected period?
   * *Assumption*: Display a "No data available" placeholder rather than an empty chart.
5. **Decision Gaps**: Do we need to support multi-currency insights in this phase?
   * *Assumption*: Single currency (CLP) for the initial sync.

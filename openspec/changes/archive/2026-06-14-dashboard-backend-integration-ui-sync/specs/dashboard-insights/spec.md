# Dashboard Insights Specification

## Purpose
Provide a high-level overview of the business operations by aggregating critical metrics from sales, inventory, and AI-driven analysis. This domain serves as the primary data source for the dashboard "Bento Grid" UI.

## Requirements

### Requirement: Aggregated Metrics Retrieval
The system MUST provide a single endpoint to retrieve aggregated metrics including total daily sales, critical stock alerts, and active AI insights.

#### Scenario: Successful metrics retrieval
- GIVEN the dashboard services are operational
- WHEN a request is made to `/api/v1/dashboard/insights`
- THEN the system SHOULD return a JSON object containing `dailySales`, `criticalStockCount`, and `aiSuggestions`
- AND the data MUST reflect the current state of the CLP currency context.

#### Scenario: Handling missing service data
- GIVEN the sales service is temporarily unavailable
- WHEN a request is made to `/api/v1/dashboard/insights`
- THEN the system MUST return the available inventory data
- AND SHOULD include a warning/status flag indicating partial data for the sales section.

### Requirement: Stock Alert Aggregation
The system SHALL identify items with stock levels below their defined threshold across all stores.

#### Scenario: Items below threshold
- GIVEN several products have stock levels below their `minThreshold`
- WHEN the insights aggregator runs
- THEN the `criticalStockCount` MUST include the count of these specific items.

## Security Requirements

### Requirement: Insights Access Control
The system MUST ensure that only users with `DASHBOARD_VIEW` permissions can access the insights endpoint.

#### Scenario: Unauthorized access attempt
- GIVEN a user without `DASHBOARD_VIEW` permission
- WHEN a request is made to `/api/v1/dashboard/insights`
- THEN the system MUST return a 403 Forbidden error.

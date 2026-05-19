# Stock Reconciliation Specification

## Purpose

Enable Héctor (inventory manager) to register physical counts, detect discrepancies between system stock and actual stock, record the motive (MERMA/ROBO/CADUCADO/ERROR_INGRESO/OTRO), adjust stock to real values, and generate alerts for significant variances (>10%).

## Dependencies

- `consolidated-stock-view` (reads current stock before reconciliation)

## Requirements

| # | Requirement | Strength |
|---|------------|----------|
| 1 | The system MUST accept a physical count for a specific product at a specific store. | MUST |
| 2 | The system MUST calculate the discrepancy as `systemStock - actualStock`. | MUST |
| 3 | When discrepancy is non-zero, the system MUST request a motive from an allowed set: MERMA, ROBO, CADUCADO, ERROR_INGRESO, OTRO. | MUST |
| 4 | The system MUST adjust the stock to the actual (physical) value after reconciliation. | MUST |
| 5 | The system MUST record the reconciliation event as a `RECONCILIATION` movement type in the audit log. | MUST |
| 6 | The system MUST log the reconciling user, motive, previous stock, and new stock. | MUST |
| 7 | When the absolute discrepancy exceeds 10% of system stock, the system MUST create an alert for the tenant owner. | MUST |
| 8 | The system MUST reject a physical count with a negative value. | MUST |

## Scenarios

### Scenario: Happy path — discrepancy detected and reconciled

- GIVEN system stock for "Jugo Caja 1L" at Kiosko Norte is 45 units
- WHEN Héctor registers a physical count of 12 units with motive MERMA
- THEN the system calculates discrepancy of -33
- AND adjusts stock to 12
- AND logs a RECONCILIATION movement with motive MERMA

### Scenario: Discrepancy > 10% triggers alert

- GIVEN system stock for "Jugo Caja 1L" at Kiosko Norte is 45 units
- WHEN the physical count is 12 units (discrepancy 73% > 10%)
- THEN after adjusting stock, the system creates an alert for Elizabeth
- AND the alert includes product name, store, previous stock, new stock, and motive

### Scenario: Zero discrepancy — no adjustment needed

- GIVEN system stock for "Galleta Surtida" at Kiosko Sur is 30 units
- WHEN Héctor registers a physical count of exactly 30 units
- THEN the system records the count as verified with zero discrepancy
- AND no stock adjustment is made
- AND no alert is created

### Scenario: Invalid negative count

- WHEN Héctor submits a physical count of -5 units
- THEN the system returns 400 with `{ "error": "INVALID_COUNT", "message": "Physical count must be >= 0" }`

## API Contracts

### POST /api/v1/inventory/stock/reconciliations

Request body:

```json
{
  "productId": "uuid",
  "storeId": "uuid",
  "physicalCount": 12,
  "motive": "MERMA"
}
```

Response `200 OK`:

```json
{
  "reconciliationId": "uuid",
  "productId": "uuid",
  "storeId": "uuid",
  "previousStock": 45,
  "newStock": 12,
  "discrepancy": -33,
  "motive": "MERMA",
  "reconciledBy": "uuid",
  "reconciledAt": "2026-05-18T11:00:00Z",
  "alertCreated": true
}
```

### GET /api/v1/inventory/stock/reconciliations/history

Query parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `productId` | UUID | No | Filter by product |
| `storeId` | UUID | No | Filter by store |
| `from` | ISO date | No | Start date |
| `to` | ISO date | No | End date |

## Error Cases

| Condition | HTTP Status | Detail |
|-----------|-------------|--------|
| Product not found at store | 404 | `PRODUCT_NOT_FOUND_AT_STORE` |
| Negative physical count | 400 | `INVALID_COUNT` |
| Invalid motive (not in allowed set) | 400 | `INVALID_MOTIVE` — allowed values: MERMA, ROBO, CADUCADO, ERROR_INGRESO, OTRO |
| Product or store ID invalid UUID | 400 | `INVALID_UUID` |

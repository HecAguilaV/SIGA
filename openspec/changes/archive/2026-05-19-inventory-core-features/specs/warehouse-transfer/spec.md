# Warehouse (Store-to-Store) Transfer Specification

## Purpose

Enable Antonia (warehouse manager) to register stock transfers between operational points with full traceability — recording origin, destination, quantity, responsible user, and a correlation ID that pairs the OUT (origin debit) and IN (destination credit) movements.

## Dependencies

- `consolidated-stock-view` (to verify stock before transfer)

## Requirements

| # | Requirement | Strength |
|---|------------|----------|
| 1 | The system MUST register a `TRANSFER` movement type at the origin store (debit). | MUST |
| 2 | The system MUST register a `TRANSFER` movement type at the destination store (credit). | MUST |
| 3 | The system MUST use a single correlation UUID to pair the OUT and IN movements. | MUST |
| 4 | The system MUST execute the transfer atomically — if either leg fails, the entire transfer MUST roll back. | MUST |
| 5 | The system MUST record the user responsible for initiating the transfer. | MUST |
| 6 | The system MUST reject a transfer if origin stock is insufficient. | MUST |
| 7 | The system MUST support querying movement history filtered by destination store and date range. | MUST |
| 8 | The system MUST reject transfers where origin and destination are the same store. | MUST |

## Scenarios

### Scenario: Happy path — successful transfer between stores

- GIVEN Antonia has 200 units of "Servilleta 100u" in Bodega Central
- WHEN she registers a transfer of 50 units to Casino Colegio
- THEN Bodega Central stock decreases to 150 (OUT movement)
- AND Casino Colegio stock increases to 50 (IN movement)
- AND both movements share a correlation UUID
- AND the audit log records: date, product, quantity, origin, destination, responsible user

### Scenario: Insufficient stock at origin

- GIVEN Bodega Central has 10 units of "Jugo Caja 1L"
- WHEN Antonia attempts to transfer 20 units to Kiosko Norte
- THEN the system returns 409 with `{ "error": "INSUFFICIENT_STOCK", "available": 10, "requested": 20 }`
- AND no stock is modified at either location

### Scenario: Same origin and destination

- WHEN Antonia attempts a transfer where origin and destination are the same store
- THEN the system returns 400 with `{ "error": "SAME_ORIGIN_DESTINATION", "message": "Origin and destination must be different" }`

### Scenario: Movement history filtered by destination and date

- GIVEN multiple transfers have been recorded across different stores and dates
- WHEN Antonia queries history with `?destination=casino-colegio-uuid&from=2026-05-01&to=2026-05-18`
- THEN the response includes all transfers to Casino Colegio within the date range
- AND each entry shows product, quantity, origin, destination, correlationId, and responsible user

## API Contracts

### POST /api/v1/inventory/stock/transfers

Request body:

```json
{
  "productId": "uuid",
  "originStoreId": "uuid",
  "destinationStoreId": "uuid",
  "quantity": 50,
  "notes": "Reposición semanal"
}
```

Response `201 Created`:

```json
{
  "transferId": "uuid",
  "correlationId": "uuid",
  "productId": "uuid",
  "originStoreId": "uuid",
  "destinationStoreId": "uuid",
  "quantity": 50,
  "originNewStock": 150,
  "destinationNewStock": 50,
  "transferredBy": "uuid",
  "transferredAt": "2026-05-18T12:00:00Z"
}
```

### GET /api/v1/inventory/stock/movements

Query parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `originStoreId` | UUID | No | Filter by origin |
| `destinationStoreId` | UUID | No | Filter by destination |
| `from` | ISO date | No | Start date |
| `to` | ISO date | No | End date |
| `type` | enum | No | Movement type filter (TRANSFER, RECONCILIATION, SALE, PURCHASE) |

Response `200 OK`:

```json
{
  "movements": [
    {
      "movementId": "uuid",
      "correlationId": "uuid",
      "productId": "uuid",
      "productName": "Servilleta 100u",
      "type": "TRANSFER",
      "quantity": 50,
      "originStoreId": "uuid",
      "originStoreName": "Bodega Central",
      "destinationStoreId": "uuid",
      "destinationStoreName": "Casino Colegio",
      "transferredBy": "uuid",
      "transferredByName": "Antonia",
      "transferredAt": "2026-05-18T12:00:00Z"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1
}
```

## Error Cases

| Condition | HTTP Status | Detail |
|-----------|-------------|--------|
| Insufficient stock at origin | 409 | `INSUFFICIENT_STOCK` with `available` and `requested` fields |
| Same origin and destination | 400 | `SAME_ORIGIN_DESTINATION` |
| Origin or destination store not found | 404 | `STORE_NOT_FOUND` |
| Product not found | 404 | `PRODUCT_NOT_FOUND` |
| Quantity <= 0 | 400 | `INVALID_QUANTITY` |
| Atomic operation fails mid-transfer | 500 | Rollback applied, compensating alert logged |

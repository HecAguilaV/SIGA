# Consolidated Stock View Specification

## Purpose

Provide Elizabeth (owner) with a unified, real-time view of stock quantities across all operational points — kiosks, casinos, warehouses, and central kitchen — with per-store breakdown and optional store filtering.

## Dependencies

None (foundational inventory capability).

## Requirements

| # | Requirement | Strength |
|---|------------|----------|
| 1 | The system MUST return total consolidated stock per product across all stores accessible to the user's tenant. | MUST |
| 2 | The system MUST include a per-store breakdown when requested (storeId, storeName, quantity, lastMovementAt). | MUST |
| 3 | The system MUST support filtering by store. | MUST |
| 4 | The system MUST respond in under 300ms for tenants with up to 10 stores. | MUST |
| 5 | The system MUST support pagination (page, size) when the product list exceeds 50 items. | MUST |
| 6 | The system MUST include the last movement timestamp per product-store combination. | MUST |
| 7 | The system MUST include a `security_requirements` section ensuring query parameters are sanitized against SQL injection. | MUST |

## Scenarios

### Scenario: Happy path — consolidated view across all stores

- GIVEN Elizabeth has 3 kiosks, 2 casinos, and 1 central kitchen
- AND product "Servilleta 100u" has: Kiosko Norte (20), Kiosko Sur (5), Bodega (200)
- WHEN she requests the consolidated stock endpoint
- THEN the response shows "Servilleta 100u" with totalStock: 225
- AND includes per-store breakdown with quantities and last movement timestamps

### Scenario: Filter by store

- GIVEN Elizabeth is viewing consolidated inventory
- WHEN she supplies `?storeId=kiosko-norte-uuid`
- THEN only products and quantities for Kiosko Norte are returned
- AND the response completes in under 300ms

### Scenario: Empty store (no stock)

- GIVEN a newly created store with no inventory movements
- WHEN the consolidated endpoint is called with `?storeId=new-store-uuid`
- THEN the response returns an empty products list with `storeId` in the metadata

## API Contracts

### GET /api/v1/inventory/stock/consolidated

Query parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `storeId` | UUID | No | Filter by specific store |
| `page` | int | No | Page number (default: 0) |
| `size` | int | No | Page size (default: 50, max: 200) |

Response `200 OK`:

```json
{
  "products": [
    {
      "productId": "uuid",
      "productName": "Servilleta 100u",
      "sku": "SER-001",
      "totalStock": 225,
      "stores": [
        {
          "storeId": "uuid",
          "storeName": "Kiosko Norte",
          "quantity": 20,
          "lastMovementAt": "2026-05-17T14:30:00Z"
        }
      ]
    }
  ],
  "page": 0,
  "size": 50,
  "totalElements": 1,
  "totalPages": 1
}
```

## Error Cases

| Condition | HTTP Status | Detail |
|-----------|-------------|--------|
| `storeId` does not exist | 404 | `{ "error": "STORE_NOT_FOUND", "message": "Store not found" }` |
| Invalid pagination values | 400 | `{ "error": "INVALID_PAGINATION", "message": "Page must be >= 0, size between 1 and 200" }` |
| Tenant has no stores | 200 | Empty products list with `totalElements: 0` |
| SQL injection attempt in query | 400 | Sanitized input, logged as security event |

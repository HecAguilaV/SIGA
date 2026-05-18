# Product Creation Flow Specification

## Purpose

Enable frictionless product creation for Héctor (inventory manager) with auto-SKU generation and fuzzy duplicate detection, preventing product proliferation and stock discrepancies.

## Dependencies

- `consolidated-stock-view` (for immediate post-creation visibility)

## Requirements

| # | Requirement | Strength |
|---|------------|----------|
| 1 | The system MUST auto-generate a SKU in format `{CATEGORY_PREFIX}-{SEQUENTIAL}` when the SKU field is empty on creation. | MUST |
| 2 | The system MUST prefix SKU sequentially per tenant to avoid cross-tenant conflicts. | MUST |
| 3 | The system MUST perform fuzzy duplicate detection on the product name before persisting. | MUST |
| 4 | When a potential duplicate is found, the system MUST warn the user with product name, SKU, and similarity score. | MUST |
| 5 | The system SHALL allow the user to proceed with creation despite the duplicate warning. | SHALL |
| 6 | The system MUST make the product available for sale and inventory operations immediately after creation. | MUST |
| 7 | The system MUST reject creation if the product name is empty or contains only whitespace. | MUST |
| 8 | The system MUST sanitize all input fields (name, description) against XSS before persistence. | MUST |

## Scenarios

### Scenario: Happy path — product created with auto-SKU

- GIVEN Héctor has INVENTORY_WRITE permission
- WHEN he creates a product with name "Galleta Surtida 250g" and leaves SKU empty
- THEN the system generates SKU "GAL-001" using category prefix + sequential counter
- AND the product is immediately available with status ACTIVE

### Scenario: Duplicate detection — similar name found

- GIVEN product "Galleta Surtida 250g" with SKU "GAL-001" already exists
- WHEN Héctor attempts to create "Galletas Surtidas 250g"
- THEN the system returns a 409 CONFLICT with duplicate warning
- AND the response includes the existing product ID and SKU for reference

### Scenario: User overrides duplicate warning

- GIVEN the duplicate warning for "Galletas Surtidas 250g" vs existing "Galleta Surtida 250g"
- WHEN Héctor includes `force: true` in the request body
- THEN the system creates the new product with a distinct SKU "GAL-002"
- AND logs the override in the audit trail

### Scenario: Validation failure — empty name

- GIVEN Héctor submits the creation form
- WHEN the product name is empty or whitespace-only
- THEN the system returns 400 with `{ "error": "VALIDATION_ERROR", "field": "name", "message": "Product name is required" }`

## API Contracts

### POST /api/v1/inventory/products

Request body:

```json
{
  "name": "Galleta Surtida 250g",
  "sku": null,
  "categoryId": "uuid",
  "description": "Galleta surtida paquete 250g",
  "unitType": "UNIDAD",
  "force": false
}
```

Response `201 Created`:

```json
{
  "productId": "uuid",
  "name": "Galleta Surtida 250g",
  "sku": "GAL-001",
  "categoryId": "uuid",
  "status": "ACTIVE",
  "createdAt": "2026-05-18T10:00:00Z"
}
```

Response `409 Conflict` (duplicate detected):

```json
{
  "error": "DUPLICATE_DETECTED",
  "message": "Similar product found",
  "existingProduct": {
    "productId": "uuid",
    "name": "Galleta Surtida 250g",
    "sku": "GAL-001"
  },
  "similarityScore": 0.92
}
```

### GET /api/v1/inventory/products/duplicate-check?name={name}

Response `200 OK`:

```json
{
  "duplicates": [
    {
      "productId": "uuid",
      "name": "Galleta Surtida 250g",
      "sku": "GAL-001",
      "similarityScore": 0.92
    }
  ]
}
```

## Error Cases

| Condition | HTTP Status | Detail |
|-----------|-------------|--------|
| Empty or whitespace name | 400 | `VALIDATION_ERROR` on field `name` |
| Invalid categoryId | 400 | `INVALID_CATEGORY` |
| Name exceeds 200 characters | 400 | `VALIDATION_ERROR` on field `name` |
| XSS-detected content in name | 400 | Sanitized, logged as security event |
| Duplicate detected (no force) | 409 | `DUPLICATE_DETECTED` with existing product reference |

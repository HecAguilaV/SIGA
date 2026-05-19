# Inventory Search Specification

## Purpose

Enable case/accent-insensitive partial product search that works for all personas — including non-digital-native users like Yesenia — returning results in under 500ms without requiring exact spelling or correct punctuation.

## Dependencies

- PostgreSQL `unaccent` extension MUST be enabled on the database.

## Requirements

| # | Requirement | Strength |
|---|------------|----------|
| 1 | The system MUST return search results in under 500ms for catalogs up to 10,000 products. | MUST |
| 2 | The system MUST be case-insensitive (e.g., "cafe" matches "Café"). | MUST |
| 3 | The system MUST be accent-insensitive using PostgreSQL `unaccent` (e.g., "cafe" matches "Café"). | MUST |
| 4 | The system MUST support partial matching on any substring of the product name (e.g., "galle" matches "Galleta", "Galletas"). | MUST |
| 5 | The system SHOULD rank results with exact prefix matches above substring matches. | SHOULD |
| 6 | The system MUST support pagination with page and size parameters. | MUST |
| 7 | The system MUST reject queries shorter than 2 characters. | MUST |
| 8 | The system MUST sanitize the query parameter against SQL injection via ILIKE + unaccent with parameterized queries. | MUST |

## Scenarios

### Scenario: Happy path — accent-insensitive partial match

- GIVEN product "Café Instantáneo 200g" exists
- WHEN Yesenia searches for "cafe instantaneo" (no accent, no tilde)
- THEN the system returns "Café Instantáneo 200g" in the results
- AND response time is under 500ms

### Scenario: Partial substring match

- GIVEN products "Galleta Surtida", "Galleta Salada", and "Galleta de Agua" exist
- WHEN Yesenia types "galle"
- THEN all three products are returned
- AND "Galleta de Agua" (exact prefix) ranks higher than partial matches

### Scenario: No results found

- GIVEN no products match the query "xyzzy"
- WHEN the search endpoint is called with `?q=xyzzy`
- THEN the response returns an empty list with `totalElements: 0`

### Scenario: Query too short

- WHEN the search endpoint is called with `?q=a`
- THEN the system returns 400 with `{ "error": "QUERY_TOO_SHORT", "message": "Search query must be at least 2 characters" }`

## API Contracts

### GET /api/v1/inventory/products/search?q={query}&page={page}&size={size}

Query parameters:

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `q` | string | Yes | Search query (min 2 characters) |
| `page` | int | No | Page number (default: 0) |
| `size` | int | No | Page size (default: 20, max: 100) |

Response `200 OK`:

```json
{
  "products": [
    {
      "productId": "uuid",
      "name": "Café Instantáneo 200g",
      "sku": "CAF-001",
      "categoryName": "Bebidas Calientes"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "responseTimeMs": 120
}
```

## Error Cases

| Condition | HTTP Status | Detail |
|-----------|-------------|--------|
| Query shorter than 2 characters | 400 | `QUERY_TOO_SHORT` |
| Invalid pagination values | 400 | `INVALID_PAGINATION` |
| SQL injection attempt in query | 400 | Parameterized query prevents injection; logged as security event |
| Database unaccent extension missing | 500 | `INTERNAL_ERROR` with reference to missing extension |

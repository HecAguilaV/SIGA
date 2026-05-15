# Spec: ui-bff

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-auth-flow

## Requirements

### Functional

- REQ-BFF-01: Cada ruta DEBE tener una `+page.server.ts` o `+layout.server.ts` que componga datos desde el gateway.
- REQ-BFF-02: El servidor DEBE autenticar cada request al gateway con el JWT de sesión (httpOnly cookie).
- REQ-BFF-03: El cliente (`.svelte`) DEBE recibir datos ya transformados — NUNCA hace fetch directo al gateway.
- REQ-BFF-04: La load function DEBE mapear errores del gateway a `error()` de SvelteKit (404, 403, 500).
- REQ-BFF-05: La load function DEBE soportar paginación server-side: recibe `page`/`pageSize` como URL params, los pasa al gateway.
- REQ-BFF-06: El BFF DEBE implementar un `fetchWithAuth()` wrapper que inyecta el token y maneja refresh automático (via `hooks.server.ts`).

### Contratos de Server Load Functions

| Ruta | Load Function | Endpoint Gateway | Transformaciones |
|------|--------------|-----------------|------------------|
| `/dashboard` | `loadDashboard` | `GET /api/v1/dashboard/insights` | Mapea métricas a `Insight[]`, agrega trends |
| `/products` | `loadProducts` | `GET /api/v1/inventory/products?page=&size=&search=` | Pagina, filtra, mapea a `ProductListItem[]` |
| `/products/[id]` | `loadProduct` | `GET /api/v1/inventory/products/{id}` | Mapea a `ProductDetail`, resuelve categoría/local |
| `/stores` | `loadStores` | `GET /api/v1/stores?page=&size=` | Pagina, mapea a `StoreListItem[]` |
| `/categories` | `loadCategories` | `GET /api/v1/categories?page=&size=` | Pagina, mapea a `CategoryListItem[]` |
| `/users` | `loadUsers` | `GET /api/v1/auth/users?page=&size=` | Pagina, filtra por tenant, mapea a `UserListItem[]` |

### Non-functional

- REQ-BFF-07: La load function DEBE resolverse en < 500ms (P95). Si el gateway excede, DEBE retornar una respuesta parcial con error por sección.

## Scenarios (GWT)

### Scenario: Load function exitosa
Given un usuario autenticado en `/products`
When la load function `loadProducts` se ejecuta
Then llama a `GET /api/v1/inventory/products?page=1&size=20`
Y retorna `{ items: ProductListItem[], total: number, page: number }`
Y el cliente renderiza la tabla sin fetch adicional

### Scenario: Gateway retorna 401 (token expirado)
Given un token JWT expirado
When la load function hace fetch al gateway
Then `fetchWithAuth()` intercepta el 401, dispara refresh token, reintenta el request
Si refresh falla → redirige a `/login`

### Scenario: Gateway retorna 500
Given el gateway caído o error interno
When la load function recibe 500
Then llama a `error(503, { message: 'Servicio no disponible' })`
Y la página muestra estado de error con opción de reintentar

### Scenario: Paginación server-side
Given el usuario cambia a página 3 en `/products`
When `loadProducts` recibe `?page=3&pageSize=20`
Then pasa `page=3&size=20` al gateway
Y retorna los items de la página 3

## Edge Cases
- REQ-BFF-08: Timeout de gateway — la load function DEBE tener timeout de 8s, luego fallback a datos cacheados o error parcial.
- REQ-BFF-09: Inyección de headers — la load function DEBE sanitizar URL params antes de pasarlos al gateway (evitar SSRF).

## Acceptance Criteria
- [ ] `fetchWithAuth()` wrapper implementado en `src/lib/server/gateway.ts`
- [ ] Cada ruta CRITICAL tiene su `+page.server.ts` con load function
- [ ] Cliente no tiene ningún `fetch()` apuntando al gateway
- [ ] Manejo de errores del gateway probado con mocks Vitest
- [ ] Paginación server-side funcional en al menos un CRUD

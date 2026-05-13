# Spec: ui-testing

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: none

## Requirements

### Functional

- REQ-TEST-01: El sistema DEBE configurar Vitest como framework de testing unitario con `@testing-library/svelte` para componentes.
- REQ-TEST-02: El sistema DEBE configurar Playwright para testing E2E en rutas críticas (login, CRUD products, chat A2UI).
- REQ-TEST-03: El equipo DEBE seguir RED-GREEN-REFACTOR: cada componente nuevo arranca con un test que falla (RED), luego se implementa (GREEN), luego se refactoriza.
- REQ-TEST-04: El sistema DEBE proporcionar un mock del BFF layer (`src/lib/server/gateway.ts`) que permita testear load functions sin conexión al gateway real.
- REQ-TEST-05: La cobertura DEBE ser ≥ 70% en todos los módulos del frontend (según `openspec/config.yaml`).
- REQ-TEST-06: El sistema DEBE ejecutar `vitest run` en CI (pre-commit hook) y fallar si los tests no pasan.
- REQ-TEST-07: El sistema DEBE ejecutar Playwright en CI contra un entorno de preview.

### Estructura de tests

```
src/
├── lib/
│   ├── components/
│   │   ├── CrudTable.test.ts      # Test de componente con mocks
│   │   └── ContextualAssistant.test.ts
│   ├── server/
│   │   └── gateway.test.ts        # Test del wrapper BFF
│   └── stores/
│       └── auth-store.test.ts     # Test de store con lógica de auth
├── routes/
│   ├── login/
│   │   └── +page.test.ts         # Test de load function
│   └── products/
│       └── +page.test.ts
└── e2e/
    ├── login.spec.ts              # Playwright: flujo login completo
    ├── crud-products.spec.ts      # Playwright: CRUD productos
    └── chat.spec.ts               # Playwright: chat A2UI
```

### Non-functional

- REQ-TEST-08: Los tests unitarios DEBEN ejecutarse en < 30s totales.
- REQ-TEST-09: Los tests E2E DEBEN ejecutarse en < 3 minutos totales.

## Scenarios (GWT)

### Scenario: RED-GREEN-REFACTOR en nuevo componente
Given un nuevo componente `ProductFilter.svelte` por implementar
When el desarrollador crea el test `ProductFilter.test.ts` primero
Then el test falla (RED) porque el componente no existe
Luego implementa el componente → test pasa (GREEN)
Luego refactoriza → test sigue pasando

### Scenario: Load function mockeada
Given un test de `+page.server.ts` de `/products`
When se ejecuta con el mock de gateway
Then `GET /api/v1/inventory/products` retorna datos mockeados
Y la load function transforma y retorna `ProductListItem[]`
Y no hay conexión real al gateway

### Scenario: Playwright login flow
Given Playwright navega a `/dashboard`
When no hay sesión
Then redirige a `/login?redirect=/dashboard`
Then completa credenciales y submit
Then redirige a `/dashboard`
Then verifica que el dashboard renderiza KPIs

### Scenario: Cobertura ≥ 70%
Given `vitest run --coverage` se ejecuta
When genera reporte de cobertura
Then cada módulo (components, server, stores) tiene ≥ 70% de cobertura
O si algún módulo está por debajo, el CI falla

## Edge Cases
- REQ-TEST-10: Tests de componentes con errores del gateway DEBEN mockearse para verificar estados de error y vacío.
- REQ-TEST-11: Playwright DEBE correr en modo headless en CI, con trace habilitado para debugging de fallos.

## Acceptance Criteria
- [ ] `vitest.config.ts` configurado con `@testing-library/svelte`
- [ ] `playwright.config.ts` configurado con rutas críticas
- [ ] Mock de gateway implementado y usado en tests
- [ ] Primer componente creado con RED-GREEN-REFACTOR
- [ ] `npx vitest run` pasa en CI
- [ ] Cobertura ≥ 70% verificable

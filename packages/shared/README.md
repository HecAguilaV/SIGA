# Shared — SIGA

**Estado**: En desarrollo 🏗️
**Stack**: TypeScript

## Propósito
Tipos, validadores y utilidades compartidas entre TODOS los frontends de SIGA. Evita duplicar contratos entre dashboard, admin-portal, customer-portal, landing y POS.

## Contenido

### Types (`types/`)
Contratos con el gateway, compartidos por todos los frontends:
- `auth.ts` — LoginRequest, LoginResponse, UserSession, PrincipalType
- `inventory.ts` — Product, ProductListItem, Category
- `stores.ts` — Store, StoreListItem
- `sales.ts` — Sale, SaleSummary
- `dashboard.ts` — Insight, KpiCard, TrendDirection
- `chat.ts` — ChatMessage, ChatStatus, SSEEvent

### Validators (`validators/`)
- `isEmail()`, `isRequired()`, `min()`, `max()`, `isPositive()`

### Formatters (`formatters/`)
- `formatCurrency()`, `formatDate()`, `truncate()`, `pluralize()`

### Utils (`utils/`)
- `debounce(fn, ms)`, `throttle(fn, ms)`

## Uso

```bash
pnpm add @siga/shared
```

```typescript
import { isEmail, formatCurrency } from '@siga/shared';
import type { Product } from '@siga/shared/types';
```

## Principios
- Zero dependencias externas
- Pure functions donde sea posible
- Tests obligatorios en cada utilidad

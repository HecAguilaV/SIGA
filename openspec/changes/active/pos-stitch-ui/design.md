# Design: POS UI & Integration (Stitch)

## UI/UX: Standard "Stitch" v2.4

### Visual Architecture (Isolated)
Para no romper el `tailwind.config.js` global, usaremos una estrategia de **CSS Variables Scoped** dentro de un contenedor `.pos-stitch-theme`.

#### Color Palette (Tailwind Mappings)
- `--pos-primary`: `#009579` (Teal)
- `--pos-bg`: `#fdf7ff` (Off-white)
- `--pos-surface`: `#ffffff`
- `--pos-dark`: `#070a61` (Navy)
- `--pos-error`: `#ba1a1a`

#### Typography
- Headlines: `Hanken Grotesk`
- Functional/SKU data: `JetBrains Mono`

### Components

#### 1. ShiftGuard (Apertura de Caja)
Un `Modal` que intercepta la vista del POS si `data.activeShift` es nulo.
- Input: `initialAmount`
- Action: `openShift()` -> redirección o refresco de `data`.

#### 2. ProductGrid
Reemplazo del actual grid por uno que use las clases Tailwind del mockup:
- Cards con `glass-panel`.
- Overlay de "+" en hover.
- Badges de stock con la paleta Stitch.

#### 3. CartSidebar
- Totals panel con `grand-total` destacado.
- Botón "Finalizar Venta" con `loading` state de escaneo.

---

## Backend Integration (BFF)

### API Proxies
El BFF (`hooks.server.ts` y `$lib/server/gateway.ts`) ya maneja la mayoría, pero necesitamos asegurar los endpoints de Sales:
- `GET /api/v1/sales/cash-shifts/active`
- `POST /api/v1/sales/cash-shifts`
- `POST /api/v1/sales/checkout`

### Real-time (SSE)
El POS se suscribirá al canal SSE existente (`/api/sse/events`) y filtrará eventos de tipo `STOCK_RESERVED` o `SALE_COMPLETED` para el `saleId` activo.

---

## Directory Structure
```
apps/dashboard/src/routes/(dashboard)/pos/
├── +page.svelte        # UI principal (Stitch)
├── +page.server.ts     # Load (Shift + Products)
├── +page.ts           # Client load (Events setup)
├── ShiftModal.svelte   # Componente aislado para turnos
└── pos.css             # Estilos Stitch aislados
```

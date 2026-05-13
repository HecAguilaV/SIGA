# Dashboard — SIGA

**Estado**: En desarrollo 🏗️
**Stack**: SvelteKit 5 + TypeScript + pnpm
**Para quién**: Dueños de PYME y sus empleados (admin, operador, cajero)
**Propósito**: Gestionar el negocio — productos, stock, ventas, chat A2UI, analytics

## Funcionalidades
- Login dual (Customer → User) con refresh token
- Dashboard con insights y KPIs
- CRUD de productos, locales, categorías, usuarios
- Chat A2UI con streaming SSE
- Analytics y gráficos
- POS (Punto de Venta) — integrado como ruta

## Desarrollo

```bash
cd apps/dashboard
pnpm install
pnpm dev
```

## Dependencias
- Gateway `:8080` para API
- `packages/ui-kit` para componentes compartidos
- `packages/shared` para types y validators

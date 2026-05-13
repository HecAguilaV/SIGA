# Admin Portal — SIGA

**Estado**: Por hacer 📋
**Stack**: SvelteKit 5 + TypeScript + pnpm
**Para quién**: Equipo SIGA (vos)
**Propósito**: Gestionar la plataforma SaaS — clientes (tenants), planes, precios, monitoreo

## Funcionalidades pendientes
- Gestión de tenants (altas, bajas, modificaciones)
- Planes y precios (CRUD de planes, asignación a tenants)
- Vencimientos y renovaciones
- Monitoreo de servicios (caídas, latencia, uptime)
- Métricas SaaS (clientes activos, MRR, etc.)
- Activación/desactivación de usuarios

## Stack compartido
- `packages/ui-kit` — design system
- `packages/shared` — types y validators
- Gateway `:8080` para API

# Customer Portal — SIGA

**ESTADO**: 🗄️ **Deprecado** — Unificado en `apps/dashboard/`

Este directorio es un relicto de la arquitectura anterior donde cada frontend era una app separada.

## Arquitectura actual

Todo converge en **`apps/dashboard/`** — un único frontend SvelteKit 5 con **dual-mode**:

| Modo | Qué ofrece |
|------|-----------|
| 🏛️ **Clásico** | Dashboard, CRUDs, Analytics — navegación fija por rutas |
| 🤖 **Agentivo (A2UI)** | El agente compone la UI dinámicamente vía protocolo A2UI de Google |

Los clientes acceden a su autogestión (facturas, estado de cuenta) desde el dashboard o conversando con el agente en modo A2UI.

## Stack

- `apps/dashboard/` — SvelteKit 5 + TypeScript + pnpm
- `packages/ui-kit/` — Design system compartido
- `packages/shared/` — Types y validators
- Gateway `:8080` para API

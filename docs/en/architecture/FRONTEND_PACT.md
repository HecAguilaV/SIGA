# SIGA Frontend Architecture & Repository Pact

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../../es/arquitectura/PACTO_FRONTEND.md)*

## Status: STIPULATED & DEFINED
**Date**: April 21, 2026
**Architecture Level**: Professional Microservices Ecosystem

---

## 1. Repository Strategy (Hybrid Monorepo)
The project remains a **Monorepo** managed via **PNPM Workspaces** to ensure atomic refactors and shared infrastructure.

### Shared Core (`/packages`)
- **`@siga/shared`**: The "Data Contract". Shared TypeScript types, validators, and utilities across all frontends (auth, inventory, sales, stores, dashboard).
- **`@siga/ui-kit`**: The "Visual DNA". Design system with atomic Svelte 5 components, native CSS tokens (colors, typography, glassmorphism), and light/dark mode.

---

## 2. Frontend Services Layout
Each frontend application is independent in its business logic but consumes the **Shared Core**.

| Frontend | Technology | Pattern | Logic Scope |
| :--- | :--- | :--- | :--- |
| **Dashboard** | SvelteKit 5 | Observer + Stores + BFF | Operations center: Inventory, Sales, AI Agent, Analytics |
| **Customer Portal** | SvelteKit 5 | — | SaaS subscriptions, payments, SSO access to Dashboard |
| **Admin Portal** | SvelteKit 5 | — | Internal platform administration |
| **Landing** | SvelteKit 5 | — | Public product presentation site |
| **POS** | SvelteKit 5 | — | In-store point-of-sale terminal |
| **Mobile** | — | — | Quick field execution *(future stage)* |

---

## 3. Mandatory Design Patterns
- **Pragmatic Atomic Design**: 
    - `Atoms/Molecules` -> Shared UI Kit.
    - `Organisms/Pages` -> Domain-specific components.
- **Container-Presentational**: 
    - Logic stays in Pages (`+page.svelte` / `Container.tsx`).
    - UI stays in Components (`$lib/components` / `components/`).
- **Adapter Pattern**: Mandatory for sanitizing AI Agent responses before rendering.

---

## 4. Roadmap
1. **Docker Sequential Boot**: Eureka -> Gateway -> Auth -> Inventory.
2. **Smoke Test**: Verify cross-service communication (Sales -> Inventory stock deduction).
3. **Package Integration**: Consolidate `@siga/shared` and `@siga/ui-kit` as standard consumption across all frontends.

---
> [!NOTE]
> This pact ensures that SIGA can grow to support thousands of stores with a maintainable, high-performance, and ethically compliant codebase.

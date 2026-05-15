# SIGA Frontend Architecture & Repository Pact

*Leer en otros idiomas: [![Español](https://img.shields.io/badge/Language-Espa%C3%B1ol-green)](../../es/arquitectura/PACTO_FRONTEND.md)*

## Status: STIPULATED & DEFINED
**Date**: April 21, 2026
**Architecture Level**: Professional Microservices Ecosystem

---

## 1. Repository Strategy (Hybrid Monorepo)
The project remains a **Monorepo** managed via **PNPM Workspaces** to ensure atomic refactors and shared infrastructure.

### Shared Core (`/packages`)
- **`@siga/api-client`**: The "Translator". Base Axios/Fetch configuration, JWT interceptors, and error handling for the API Gateway.
- **`@siga/types`**: The "Data Contract". Shared TypeScript interfaces, enums, and DTOs between all frontends and the backend.
- **`@siga/design-tokens`**: The "Visual DNA". CSS variables, Tailwind configuration, and brand assets for 100% visual consistency.

---

## 2. Frontend Services Layout
Each frontend application is independent in its business logic but consumes the **Shared Core**.

| Frontend | Technology | Pattern | Logic Scope |
| :--- | :--- | :--- | :--- |
| **Dashboard** | SvelteKit 5 | Observer + Stores + BFF | Admin Dashboard, Inventory, Sales, AI Agent, Landing, Portals. |

> **Note (May 2026)**: Legacy frontends (webapp, landing, customer-portal React, admin-portal, mobile) were declared legacy deprecated. The SvelteKit dashboard unifies all interfaces.

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

## 4. Next Session Roadmap
1. **Docker Sequential Boot**: Eureka -> Gateway -> Auth -> Inventory.
2. **Smoke Test**: Verify cross-service communication (Sales -> Inventory stock deduction).
3. **Packages Scaffold**: Create `@siga/api-client` and `@siga/types`.

---
> [!NOTE]
> This pact ensures that SIGA can grow to support thousands of stores with a maintainable, high-performance, and ethically compliant codebase.

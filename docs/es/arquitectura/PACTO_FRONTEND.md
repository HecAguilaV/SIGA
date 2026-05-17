# Arquitectura Frontend y Pacto de Repositorio SIGA

*Read this in other languages: [![English](https://img.shields.io/badge/Language-English-blue)](../../en/architecture/FRONTEND_PACT.md)*

## Estado: ESTIPULADO Y DEFINIDO
**Fecha:** 21 de Abril, 2026
**Nivel de Arquitectura:** Ecosistema de Microservicios Profesional

---

## 1. Estrategia de Repositorio (Monorepo Híbrido)
El proyecto se mantiene como un **Monorepo** gestionado mediante **PNPM Workspaces** para garantizar refactorizaciones atómicas e infraestructura compartida.

### Núcleo Compartido (`/packages`)
- **`@siga/shared`**: El "Contrato de Datos". Tipos TypeScript, validadores y utilidades compartidas entre todos los frontends (auth, inventory, sales, stores, dashboard).
- **`@siga/ui-kit`**: El "ADN Visual". Design system con componentes atómicos Svelte 5, tokens CSS nativos (colores, tipografía, glassmorphism) y modo claro/oscuro.

---

## 2. Disposición de Servicios Frontend
Cada aplicación frontend es independiente en su lógica de negocio pero consume el **Núcleo Compartido**.

| Frontend | Tecnología | Patrón | Alcance de Lógica |
| :--- | :--- | :--- | :--- |
| **Dashboard** | SvelteKit 5 | Observer + Stores + BFF | Centro operativo: Inventario, Ventas, Agente IA, Analytics |
| **Customer Portal** | SvelteKit 5 | — | Suscripciones SaaS, pagos, acceso SSO al Dashboard |
| **Admin Portal** | SvelteKit 5 | — | Administración interna de la plataforma |
| **Landing** | SvelteKit 5 | — | Sitio público de presentación |
| **POS** | SvelteKit 5 | — | Terminal de punto de venta en tienda |
| **Mobile** | — | — | Ejecución rápida en terreno *(etapa futura)* |

---

## 3. Patrones de Diseño Obligatorios
- **Atomic Design Pragmático**: 
    - `Atoms/Molecules` -> UI Kit compartido.
    - `Organisms/Pages` -> Componentes específicos de dominio.
- **Container-Presentational**: 
    - La lógica reside en las Páginas (`+page.svelte` / `Container.tsx`).
    - La UI reside en los Componentes (`$lib/components` / `components/`).
- **Patrón Adapter**: Obligatorio para sanitizar las respuestas del Agente de IA antes de su renderizado.

---

## 4. Hoja de Ruta
1. **Arranque Secuencial Docker**: Eureka -> Gateway -> Auth -> Inventory.
2. **Smoke Test**: Verificar comunicación cross-service (Ventas -> Deducción de stock en Inventory).
3. **Integración de Paquetes**: Consolidar `@siga/shared` y `@siga/ui-kit` como consumo estándar en todos los frontends.

---
> [!NOTE]
> Este pacto asegura que SIGA pueda crecer para soportar miles de tiendas con una base de código mantenible, de alto rendimiento y éticamente responsable.

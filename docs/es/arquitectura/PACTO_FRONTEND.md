# Arquitectura Frontend y Pacto de Repositorio SIGA

## Estado: ESTIPULADO Y DEFINIDO
**Fecha:** 21 de Abril, 2026
**Nivel de Arquitectura:** Ecosistema de Microservicios Profesional

---

## 1. Estrategia de Repositorio (Monorepo Híbrido)
El proyecto se mantiene como un **Monorepo** gestionado mediante **PNPM Workspaces** para garantizar refactorizaciones atómicas e infraestructura compartida.

### Núcleo Compartido (`/packages`)
- **`@siga/api-client`**: El "Traductor". Configuración base de Axios/Fetch, interceptores JWT y manejo de errores para el API Gateway.
- **`@siga/types`**: El "Contrato de Datos". Interfaces TypeScript compartidas, enums y DTOs entre todos los frontends y el backend.
- **`@siga/design-tokens`**: El "ADN Visual". Variables CSS, configuración de Tailwind y assets de marca para una consistencia visual del 100%.

---

## 2. Disposición de Servicios Frontend
Cada aplicación frontend es independiente en su lógica de negocio pero consume el **Núcleo Compartido**.

| Frontend | Tecnología | Patrón | Alcance de Lógica |
| :--- | :--- | :--- | :--- |
| **Comercial** | React | MVVM + Capa de Servicio | Auth, Facturación, Planes, Pagos. |
| **Webapp** | Svelte 5 | Observer + Stores | Dashboard Admin, Inventario, Ventas, Agente IA. |
| **Landing** | Svelte/HTML | Minimalista | Tracking, Contenido Estático, Conversión. |

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

## 4. Hoja de Ruta Próxima Sesión
1. **Arranque Secuencial Docker**: Eureka -> Gateway -> Auth -> Inventory.
2. **Smoke Test**: Verificar comunicación cross-service (Ventas -> Deducción de stock en Inventory).
3. **Estructura de Paquetes**: Crear `@siga/api-client` y `@siga/types`.

---
> [!NOTE]
> Este pacto asegura que SIGA pueda crecer para soportar miles de tiendas con una base de código mantenible, de alto rendimiento y éticamente responsable.

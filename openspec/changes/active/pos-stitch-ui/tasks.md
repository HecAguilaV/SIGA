# Tasks: POS UI with Stitch Design (Isolated)

## Phase 1: Infrastructure & Style Isolation (F1)
- [ ] **T1.1: Setup Isolated CSS**
    - Crear `apps/dashboard/src/routes/(dashboard)/pos/pos.css` con los tokens de Stitch.
    - Importar fuentes (`Hanken Grotesk`, `Material Symbols`) vía `@import` o CDN link.
- [ ] **T1.2: BFF Proxies**
    - Verificar y/o agregar métodos en `$lib/server/gateway.ts` para los endpoints de Sales (`cash-shifts`, `checkout`).
- [ ] **T1.3: Server Load**
    - Actualizar `+page.server.ts` para retornar `activeShift` y `initialProducts`.

## Phase 2: Shift Management (F2)
- [ ] **T2.1: ShiftModal Component**
    - Crear el modal de apertura de caja con la estética Stitch.
- [ ] **T2.2: Shift Logic**
    - Implementar la validación inicial: si no hay turno, el modal bloquea la UI.

## Phase 3: Stitch POS Core (F3)
- [ ] **T3.1: Stitch Header & Search**
    - Implementar la barra superior con el buscador y filtros de categoría del mockup.
- [ ] **T3.2: Stitch Product Grid**
    - Re-implementar las cards de productos con glassmorphism y efectos hover.
- [ ] **T3.3: Stitch Cart Sidebar**
    - Re-implementar el carrito con la nueva paleta y tipografía.

## Phase 4: Sales Flow (F4)
- [ ] **T4.1: Checkout Logic**
    - Implementar el envío de la venta al backend y el manejo del estado "Procesando" (Scan Animation).
- [ ] **T4.2: SSE Integration**
    - Suscribirse a los eventos del servidor para recibir la confirmación de la venta (SAGA completion).

## Phase 5: Verification (F5)
- [ ] **T5.1: Visual Audit**
    - Verificar que el diseño coincida con el mockup y no afecte al resto del dashboard.
- [ ] **T5.2: End-to-End Test**
    - Simular un flujo completo: Abrir Caja -> Buscar Producto -> Venta -> Confirmación SAGA.

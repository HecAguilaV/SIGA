# Spec: ui-a11y

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-theme

## Requirements

### Functional

- REQ-A11Y-01: El sistema DEBE rescatar e integrar el componente `A11yToolbar` del legacy que provee: alto contraste, escala de grises, fuente legible, subrayar enlaces.
- REQ-A11Y-02: La `A11yToolbar` DEBE persistir sus preferencias en `localStorage` y restaurarlas al cargar.
- REQ-A11Y-03: Todos los CRUDs (`CrudTable`, `CrudForm`) DEBEN ser completamente navegables por teclado (Tab, Enter, Escape, flechas).
- REQ-A11Y-04: Los modales y dialogs DEBEN manejar focus trapping: al abrir, el focus va al primer elemento interactivo; Escape cierra; Tab cicla dentro del modal.
- REQ-A11Y-05: Los componentes críticos DEBEN tener roles ARIA apropiados: `role="alert"` para errores, `role="dialog"` para modales, `aria-label` en iconos sin texto.
- REQ-A11Y-06: El sistema DEBE anunciar cambios dinámicos via `aria-live` regions (ej: "Producto creado exitosamente").
- REQ-A11Y-07: El sistema DEBE integrar `axe-core` en los tests E2E de Playwright para verificar WCAG AA en rutas principales (login, dashboard, products).
- REQ-A11Y-08: El sistema DEBE tener un skip-to-content link al inicio de cada página (`href="#main-content"`).

### Non-functional

- REQ-A11Y-09: Todos los componentes DEBEN pasar las reglas de axe-core nivel AA sin excepciones.
- REQ-A11Y-10: La navegación por teclado DEBE ser completa sin necesidad de mouse en rutas CRUD.

## Scenarios (GWT)

### Scenario: A11yToolbar activa alto contraste
Given un usuario con baja visión
When activa "Alto Contraste" en la A11yToolbar
Then se aplica clase `.high-contrast` al `<body>`
Y todos los colores aumentan su ratio de contraste (fondo negro, texto blanco, accent amarillo)
Y la preferencia se persiste en localStorage

### Scenario: Navegación por teclado en CrudTable
Given un usuario que solo usa teclado en `/products`
When presiona Tab repetidamente
Then el foco navega por: buscador → tabla fila 1 → botón editar → botón eliminar → paginador
Y Enter en "editar" abre el formulario de edición
Y Escape cierra el modal de confirmación de eliminación

### Scenario: Focus trapping en modal de confirmación
Given el modal de confirmación de eliminación está abierto
When el usuario presiona Tab estando en el último botón ("Cancelar")
Then el foco vuelve al primer botón ("Confirmar")
Y no puede tabular fuera del modal

### Scenario: axe-core en login
Given Playwright navega a `/login`
When ejecuta `axe-core` analysis
Then no hay violaciones de reglas AA
Y si hay, el test falla y reporta las violaciones específicas

### Scenario: Skip-to-content link
Given un usuario de lector de pantalla
When carga cualquier página
Then el primer elemento focusable es "Saltar al contenido principal"
Y al activarlo, el focus va a `<main id="main-content">`

## Edge Cases
- REQ-A11Y-11: La A11yToolbar DEBE funcionar incluso si `localStorage` no está disponible (fallback a preferencias por defecto).
- REQ-A11Y-12: El alto contraste DEBE anular cualquier tema (claro/oscuro) seleccionado — la accesibilidad prima sobre la estética.

## Acceptance Criteria
- [ ] A11yToolbar rescatada del legacy con todas sus funcionalidades
- [ ] Skip-to-content link en producción en todas las rutas
- [ ] Navegación por teclado completa en CRUDs verificada con Playwright
- [ ] Focus trapping en modales
- [ ] axe-core AA pasa en ruta login, dashboard, products
- [ ] `aria-live` regions para mensajes dinámicos

# Spec: ui-theme

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: none

## Requirements

### Functional

- REQ-THEME-01: El sistema DEBE definir design tokens como CSS custom properties en `:root` y `[data-theme="dark"]`.
- REQ-THEME-02: El sistema DEBE implementar glassmorphism (backdrop-filter blur + semitransparencia) en cards, modales y sidebar.
- REQ-THEME-03: El sistema DEBE soportar modo claro y oscuro detectado por `prefers-color-scheme` y toggle manual.
- REQ-THEME-04: El sistema DEBE persistir la preferencia de tema en `localStorage`.
- REQ-THEME-05: El sistema DEBE cumplir ratio de contraste WCAG AA (4.5:1 texto normal, 3:1 texto grande).
- REQ-THEME-06: El sistema DEBE usar tipografía Inter (sans-serif) y JetBrains Mono (mono/números).
- REQ-THEME-07: El sistema NO DEBE usar frameworks CSS (cero Tailwind, Bulma, Bootstrap).
- REQ-THEME-08: El sistema DEBE proporcionar variantes del color accent existente (`#5E6AD2`).

### Non-functional

- REQ-THEME-09: El toggle de tema DEBE responder en menos de 100ms (sin layout shift).
- REQ-THEME-10: Los tokens DEBEN estar documentados en un archivo de referencia (`design-tokens.md`).

## Scenarios (GWT)

### Scenario: Cambio de tema persistido
Given un usuario que selecciona modo oscuro manualmente
When recarga la página
Then el tema oscuro se aplica sin parpadeo (flash of incorrect theme)

### Scenario: Detección automática de tema
Given un usuario con preferencia `prefers-color-scheme: dark` en su SO
When visita el sitio por primera vez (sin localStorage)
Then el tema oscuro se aplica automáticamente

### Scenario: Glassmorphism en card
Given una card con clase `surface-glass`
When se renderiza
Then tiene `background: rgba(255,255,255,0.1)`, `backdrop-filter: blur(12px)`, y `border: 1px solid rgba(255,255,255,0.2)`

### Scenario: Contraste WCAG AA en modo claro
Given texto normal sobre fondo claro
When se verifica ratio de contraste
Then cumple mínimo 4.5:1

### Scenario: Contraste WCAG AA en modo oscuro
Given texto normal sobre fondo oscuro
When se verifica ratio de contraste
Then cumple mínimo 4.5:1

## Edge Cases
- REQ-THEME-11: El sistema DEBE manejar el caso donde `localStorage` no está disponible (incógnito, bloqueado por policy).

## Acceptance Criteria
- [ ] CSS custom properties definidas para color, surface, radius, shadow, spacing, typography
- [ ] Tema claro y oscuro con cobertura completa de tokens
- [ ] Toggle manual funcional con persistencia
- [ ] Glassmorphism aplicado en al menos 3 componentes críticos
- [ ] axe-core no reporta errores de contraste en rutas principales

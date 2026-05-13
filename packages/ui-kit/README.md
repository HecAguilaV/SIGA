# UI Kit — SIGA Design System

**Estado**: En desarrollo 🏗️
**Stack**: CSS nativo + Svelte 5 runes (independiente de framework)

## Propósito
Design system compartido entre TODOS los frontends de SIGA. Un cambio aquí afecta a dashboard, admin-portal, customer-portal, landing y POS.

## Contenido

### Tokens CSS (`tokens.css`)
Variables CSS en `:root` y `[data-theme="dark"]`:
- Colores: accent `#5E6AD2`, surface, background, text, success, error, warning
- Glassmorphism: `--surface-glass`, `--glass-blur`, `--glass-border`
- Tipografía: Inter, JetBrains Mono
- Radios, sombras, transiciones
- Modo claro/oscuro completo

### Componentes atómicos
- `Button` — variant (primary|secondary|ghost|danger), size, loading
- `Input` — type, label, error, aria
- `Card` — variant (default|glass), slots
- `Modal` — focus trap, aria-modal, Escape key
- `Toast` — success|error|info|warning, autoDismiss
- `Spinner` — size, variant
- `Badge` — variant (info|warning|danger|success)
- `Skeleton` — variant (text|card|table-row)

### Componentes de layout
- `ThemeToggle` — claro/oscuro
- `A11yToolbar` — accesibilidad (alto contraste, escala de grises, etc.)

## Uso

Cada frontend instala el paquete:

```bash
pnpm add @siga/ui-kit
```

```svelte
<script lang="ts">
  import { Button, Card, Modal } from '@siga/ui-kit';
</script>
```

## Principios
- Cero frameworks CSS — solo nativo + tokens
- WCAG AA como mínimo
- Modo claro/oscuro nativo
- Accesibilidad desde el diseño

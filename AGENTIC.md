# SIGA-WEBCOMERCIAL: Interfaz Agéntica

Este módulo gestiona la primera interacción del cliente con la inteligencia de SIGA.

## 💬 Chat Comercial
- **Propósito**: Convertir leads y explicar planes de suscripción.
- **Componentes**: `ChatBox.jsx` integrado con el servicio `chatComercial` de `api.js`.

## 🛠️ Modo Mantenimiento Agéntico
- El frontend está diseñado para ser "consciente" del estado del backend.
- Si el backend entra en migración o falla, se redirige a `MigrationPage.jsx` informando al usuario sobre el progreso estimado.

## 🎨 Diseño y UX
- Integración de micro-animaciones para indicar que "la IA está pensando".
- Fallback visual si el asistente no puede cargar.

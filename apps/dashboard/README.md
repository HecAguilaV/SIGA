# @siga/dashboard — Frontend del Ecosistema SIGA Pro

Este directorio contiene la implementación unificada de la interfaz de usuario (Frontend) y la capa BFF (Backend for Frontend) del ecosistema **SIGA Pro**. Está construido con **SvelteKit**, aplicando principios de desarrollo modernos, rendimiento excepcional, y cumplimiento legal de protección de datos (Ley Chilena 21.719).

---

## Tecnologías Clave

* **SvelteKit (v2.0+) & Svelte (v5.0+):** Framework reactivo basado en compilación previa en lugar de DOM virtual. Aporta una velocidad de carga inigualable.
* **CSS Nativo & Variables CSS:** Ausencia de utilidades pesadas; diseño limpio estructurado mediante propiedades personalizadas CSS encapsuladas.
* **Phosphor Svelte (v3.0.1):** Consistencia tipográfica completa y moderna. Se abolieron de forma definitiva los emojis de teclado para garantizar una estética industrial corporativa (*glassmorphic*).
* **Chart.js (v4.5.1):** Biblioteca analítica cargada dinámicamente de forma asíncrona en el cliente para eludir bloqueos con SSR.

---

## Estructura del Proyecto y Rutas

El ruteo de SvelteKit es puramente jerárquico basado en el sistema de archivos de `src/routes/`. Se implementaron **Grupos de Rutas** (carpetas encerradas entre paréntesis) para segmentar lógicamente las interfaces y aplicar layouts aislados sin alterar las URLs:

```text
src/routes/
├── (landing)/                # Cliente 1: Landing Page Pública
│   ├── +layout.svelte        # Layout limpio de conversión (sin barras laterales)
│   └── +page.svelte          # Presentación de marca, características y planes SaaS
│
├── (auth)/                   # Cliente 2: Portal de Seguridad
│   ├── login/                # Inicio de sesión de usuarios seudonimizados
│   ├── register/             # Formulario de alta para nuevos comercios (Tenants)
│   └── onboarding/           # Wizard de configuración inicial del comercio
│
├── (dashboard)/              # Cliente 3: Portal Transaccional de Inquilinos
│   ├── dashboard/            # Panel administrativo central con KPIs y métricas
│   ├── pos/                  # Interfaz de Punto de Venta ágil (POS)
│   └── analytics/predictive/ # Informes avanzados y anomalías detectadas por IA
│
├── assistant/                # Cliente 4: Asistente de IA (Interfaz A2UI)
│   └── +page.svelte          # Interfaz de conversación con el Agente SIGA Pro
│
├── chat-handler/stream/      # Canal SSE
│   └── +server.ts            # Endpoint de streaming (Server-Sent Events) para la IA
│
├── products/                 # CRUD de Productos (Gestión de SKUs)
├── categories/               # CRUD de Categorías de Inventario
├── stores/                   # CRUD de Sucursales (Monitoreo del Dueño y Multi-tenant)
└── users/                    # CRUD de Usuarios (Gestión de Staff y Permisos)
```

---

## Desarrollo Local

### Requisitos Previos
* **Node.js** (v20 o superior recomendado)
* **pnpm** (v10.33.0, configurado como Package Manager oficial del monorepo)

### Comandos de Terminal

Desde el directorio raíz del monorepo o dentro de `apps/dashboard/`, podés ejecutar:

```bash
# Instalar dependencias
pnpm install

# Iniciar servidor de desarrollo con Vite
pnpm dev

# Construir la aplicación para producción (compilación Node.js optimizada)
pnpm build

# Previsualizar el bundle de producción construido de forma local
pnpm preview

# Ejecutar el formateador de código y verificador de tipos de Svelte-Check
pnpm check

# Ejecutar suite de pruebas unitarias y de integración
pnpm test

# Ejecutar pruebas e2e (End-to-End) automatizadas con Playwright
pnpm test:e2e
```

---

## Decisiones de Diseño Técnico

### 1. BFF (Backend for Frontend) Integrado
SvelteKit permite que las rutas utilicen loaders y actions del lado del servidor (`+page.server.ts` y `+page.server.js`). En SIGA Pro, esto se utiliza como la primera línea de defensa de seguridad:
* **Seudonimización Directa:** Los identificadores de usuarios y transacciones se convierten a UUID v4 en el servidor SvelteKit antes de renderizarse, en concordancia con el Artículo 14 quáter de la Ley 21.719 de protección de datos comerciales.
* **Control de Cookies:** Los tokens JWT de sesión se almacenan bajo cookies con bandera `HttpOnly` y `Secure`, impidiendo que scripts maliciosos de terceros (XSS) capturen las credenciales del usuario.

### 2. Evitando el Bloqueo de Renderizado (SSR) con Librerías Gráficas
Dado que SvelteKit compila inicialmente la página en el servidor (SSR), el uso directo de librerías de gráficos basadas en `canvas` (como `chart.js`) causa fallos porque el objeto global `window` o `document` no existe fuera del navegador.
* **Solución de Arquitectura:** En `src/lib/components/charts/ChartWrapper.svelte` se realiza una carga diferida dinámica mediante `onMount`. El bundle de Chart.js se importa únicamente cuando la página ya se ha inicializado de forma segura en el cliente, optimizando el tiempo hasta que la página es completamente interactiva (FID).

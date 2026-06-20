# Aplicaciones Frontend — Ecosistema SIGA Pro

Este directorio `apps/` contiene las aplicaciones que componen la capa de interfaz de usuario (Frontend) y BFF (Backend for Frontend) de la plataforma multi-tenant **SIGA Pro**.

---

## Estructura de Directorios

Actualmente, el ecosistema frontend está unificado en un único workspace altamente optimizado:

```text
apps/
├── README.md               # Este archivo (Guía general del frontend)
└── dashboard/              # Aplicación de SvelteKit (Dashboard + Landing + POS + BFF)
    ├── package.json        # Gestión de scripts y dependencias (Vite, Vitest, Phosphor, Chart.js)
    ├── svelte.config.js    # Configuración de SvelteKit y adaptador Node.js
    ├── vite.config.ts      # Configuración del empaquetador y bundler Vite
    ├── tsconfig.json       # Configuración del compilador TypeScript
    ├── src/
    │   ├── app.html        # Plantilla HTML base
    │   ├── app.css         # Estilos globales y variables CSS de marca (Deep Teal, Cyan)
    │   ├── lib/            # Componentes reutilizables, utilidades y lógica BFF
    │   └── routes/         # Sistema de rutas basado en archivos (File-based Routing)
    └── static/             # Recursos estáticos de marca (Logos, Isotipos de la "S")
```

---

## Filosofía de un Único Directorio (Workspace Unificado)

La presencia de múltiples "clientes" (la landing page pública, el panel de administración transaccional, el punto de venta POS y las vistas de administración/monitoreo del dueño de la plataforma) dentro de una sola aplicación unificada de SvelteKit responde a decisiones arquitectónicas estratégicas para optimizar el rendimiento y la mantenibilidad:

1. **Aislamiento mediante Grupos de Rutas:** SvelteKit permite dividir layouts independientes para diferentes perfiles utilizando carpetas entre paréntesis:
   * `(landing)`: Layout liviano para visitantes sin autenticar, enfocado 100% en conversión y SEO.
   * `(auth)`: Layout de seguridad para los formularios de login, registro de comercios e inducción.
   * `(dashboard)`: Layout stateful con sidebar completo, encabezados, selectores de local y chequeos de seguridad activa para personal de comercios.
2. **Reutilización de Tokens de Diseño:** Todos los módulos comparten de forma nativa la paleta de colores oficial (`#03045e`, `#80ffdb`), la tipografía (`Hanken Grotesk`) y el radio de redondeo (`ROUND_EIGHT`), garantizando consistencia absoluta sin duplicación de código.
3. **Simplicidad en DevOps:** Un único contenedor Docker y una única tubería de Integración Continua (CI/CD) para todo el frontend. Esto reduce drásticamente el costo de servidores y simplifica la orquestación en Kubernetes.

---

## Desarrollo e Inicio Rápido

Para iniciar el entorno local del frontend de SIGA Pro, asegúrate de utilizar el gestor de paquetes oficial del monorepo (`pnpm`):

```bash
# Instalar todas las dependencias del monorepo (ejecutar en la raíz)
pnpm install

# Iniciar el servidor de desarrollo en modo watch (Vite)
pnpm --filter @siga/dashboard dev

# Ejecutar el análisis estático y tipado de Svelte-Check
pnpm --filter @siga/dashboard check

# Ejecutar pruebas unitarias con Vitest
pnpm --filter @siga/dashboard test

# Ejecutar pruebas de extremo a extremo (E2E) con Playwright
pnpm --filter @siga/dashboard test:e2e
```

Para más detalles específicos sobre la configuración técnica de la app de SvelteKit, ingresa a [apps/dashboard/README.md](./dashboard/README.md).

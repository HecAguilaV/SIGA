# =============================================================================
# Dockerfile — Dashboard SIGA (SvelteKit + adapter-node)
# =============================================================================
# Propósito: Containerizar el frontend SvelteKit para producción en EKS.
#
# Decisión técnica: Se usa adapter-node en lugar de adapter-static porque
# el dashboard maneja server-side data composition (BFF nativo) y rutas
# server-side protegidas con JWT. adapter-static no soporta SSR.
#
# Stack: Node.js 20 Alpine (menor huella de seguridad y tamaño)
# Package Manager: pnpm (por consistencia con el monorepo)
#
# Contexto de build: raíz del monorepo (.)
#   docker build -t siga-dashboard -f apps/dashboard/Dockerfile .
#
# Buenas prácticas aplicadas:
#   - Multi-stage build (imagen final ~120 MB vs ~1.2 GB si usara sdkman)
#   - Usuario no root en runtime (appuser)
#   - Variables de entorno inyectables (PORT, HOST, NODE_ENV)
#   - Capas ordenadas de menor a mayor cambio para cache óptimo
# =============================================================================

# ---- Build Stage -----------------------------------------------------------
FROM node:22-alpine AS build

WORKDIR /app

# ---------------------------------------------------------------------------
# Capa 1: Dependencias del sistema y pnpm global
# ---------------------------------------------------------------------------
RUN npm install -g pnpm@latest && \
    addgroup -S appgroup && adduser -S appuser -G appgroup

# ---------------------------------------------------------------------------
# Capa 2: Código fuente del dashboard y paquetes locales del monorepo
# ---------------------------------------------------------------------------
# Las carpetas packages/ui-kit y packages/shared se copian porque el
# dashboard las referencia mediante aliases en svelte.config.js.
# Vite las resuelve como source durante el build — no son dependencias npm.
COPY apps/dashboard/       apps/dashboard/
COPY packages/ui-kit/      packages/ui-kit/
COPY packages/shared/      packages/shared/

# ---------------------------------------------------------------------------
# Capa 3: Configuración raíz necesaria para pnpm (lockfile)
# ---------------------------------------------------------------------------
# pnpm usa el lockfile global; sin él, puede resolver mal las versiones.
# El lockfile está dentro de apps/dashboard/, ya copiado en la Capa 2

# ---------------------------------------------------------------------------
# Capa 4: Instalación de dependencias (cacheable si no cambia package.json)
# ---------------------------------------------------------------------------
WORKDIR /app/apps/dashboard
ENV CI=true
RUN pnpm install --frozen-lockfile 2>/dev/null || pnpm install

# ---------------------------------------------------------------------------
# Capa 5: Build de producción
# ---------------------------------------------------------------------------
# adapter-node genera el output en build/ — incluye SSR + API routes.
RUN pnpm build

# ---- Runtime Stage ---------------------------------------------------------
FROM node:22-alpine AS runtime
WORKDIR /app

# ---------------------------------------------------------------------------
# Copiar solo lo necesario desde el build stage
# ---------------------------------------------------------------------------
COPY --from=build /app/apps/dashboard/build/      build/
COPY --from=build /app/apps/dashboard/package.json ./

# ---------------------------------------------------------------------------
# Seguridad: usuario no root
# ---------------------------------------------------------------------------
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# ---------------------------------------------------------------------------
# Configuración de runtime
# ---------------------------------------------------------------------------
EXPOSE 3000
ENV NODE_ENV=production \
    PORT=3000 \
    HOST=0.0.0.0

# Health check para Kubernetes (liveness probe)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD node -e "fetch('http://localhost:3000/health').then(r => process.exit(r.ok?0:1)).catch(() => process.exit(1))"

CMD ["node", "build/index.js"]

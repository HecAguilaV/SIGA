## Exploration: Migración a Microservicios SIGA

### Current State
El sistema es actualmente un conjunto de servicios (monorepo) con alta dependencia en una única instancia de PostgreSQL 16 que contiene dos esquemas principales: `siga_comercial` y `siga_saas`.

### Affected Areas
- `services/backend/` — Necesita extracción de lógica de Auth, Ventas e Inventario.
- `services/gateway/` — Nuevo punto de entrada.
- `docker-compose.yml` — Orquestación de nuevos contenedores.

### Approaches
1. **Patrón Strangler Fig (Recomendado)** — Extraer servicios uno a uno usando el Gateway para redirigir tráfico.
   - Pros: Bajo riesgo, entrega continua.
   - Cons: Coexistencia temporal de dos arquitecturas.
   - Effort: Medium

2. **Big Bang** — Reescribir toda la comunicación entre servicios de una vez.
   - Pros: Limpieza inmediata.
   - Cons: Altísimo riesgo de rotura.
   - Effort: High

### Recommendation
Usar **Strangler Fig**. Comenzar por el servicio de `Auth` para centralizar la seguridad, seguido por `Inventario`.

### Database Strategy
- Mantener una instancia física de PostgreSQL por ahora.
- Mover de "Esquemas" a "Bases de Datos lógicas" independientes por servicio para facilitar futura separación física completa.

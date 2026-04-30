# OpenSpec: Estado de la Arquitectura SIGA (V3.0 - Hardening)

## Resumen del Sistema
SIGA se encuentra en fase de blindaje de seguridad y cumplimiento de la **Ley 21.719**. Se ha iniciado la migración masiva de identificadores secuenciales (Int) a identificadores universales (**UUID**) para garantizar la seudonimización y el no-rastreo.

## Mapa de Ecosistema

### Backends (Microservicios Kotlin/Spring)
- **Auth**: ✅ UUID Completo. Arnés de integración verde.
- **Inventory**: ✅ UUID Completo. Arnés de integración verde.
- **Sales**: 🔄 PENDIENTE. Siguiente objetivo de migración UUID.
- **Billing**: 🔄 PENDIENTE.
- **Gateway/Registry/Common**: Infraestructura de soporte.

### Frontends (UI/UX)
- **webapp**: La "estrella visual" del proyecto. Diseño premium y moderno.
- **mobile**: Aplicación nativa/híbrida para acceso móvil.
- **landing**: Sitio informativo y de captación.
- **commercial**: Frontend Legacy. **Misión crítica**: Debe ser adaptado a la nueva arquitectura.

## Estrategia Commercial & Pagos
El microservicio `commercial` (y su frontend asociado) será el responsable de gestionar las suscripciones de clientes.
1. **Pasarela de Pagos**: Se implementará una pasarela **ficticia** que cumpla con los estándares de **Transbank** y normativas del **SII**.
2. **Arquitectura Hexagonal**: Es OBLIGATORIO usar el patrón hexagonal para que el cambio de la pasarela ficticia a una real sea un simple cambio de adaptador.

## Decisiones Técnicas Recientes
1. **UUID Mandatory**: No se permiten IDs secuenciales en el nuevo esquema de persistencia.
2. **Integration Harness**: Cada microservicio backend DEBE tener su clase `BaseIntegrationTest` con soporte multiesquema en H2.
3. **Shift-Left Security**: Auditoría proactiva con Gitleaks y Semgrep tras cada hito.

## Próximos Objetivos
1. Migración UUID del microservicio `sales`.
2. Diseño de la capa de suscripciones en `commercial` (Backend).
3. Sincronización de tipos UUID en los frontends (`webapp`, `commercial`).

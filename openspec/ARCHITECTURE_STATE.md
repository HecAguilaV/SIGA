# OpenSpec: Estado de la Arquitectura SIGA (V3.0 - Hardening)

## Resumen del Sistema
SIGA se encuentra en fase de blindaje de seguridad y cumplimiento de la **Ley 21.719**. Se ha iniciado la migración masiva de identificadores secuenciales (Int) a identificadores universales (**UUID**) para garantizar la seudonimización y el no-rastreo.

## Mapa de Ecosistema

### Backends (Microservicios Kotlin/Spring)
- **Auth**: ✅ UUID Completo. Arnés de integración verde.
- **Inventory**: ✅ UUID Completo. Arnés de integración verde.
- **Sales**: 🔄 PENDIENTE. Siguiente objetivo de migración UUID.
- **Billing**: ✅ UUID Completo & Arquitectura Hexagonal. Integración con PaymentGateway (Transbank Ficticio).
- **Sales**: 🔄 PENDIENTE. Siguiente objetivo de migración UUID.

### Frontends (UI/UX)
- **webapp**: La "estrella visual" del proyecto. Diseño premium y moderno.
- **mobile**: Aplicación nativa/híbrida para acceso móvil.
- **landing**: Sitio informativo y de captación.
- **commercial**: Frontend Legacy. **Misión crítica**: Debe ser adaptado a la nueva arquitectura.

## Estrategia Commercial & Pagos
El microservicio `commercial` gestionará las suscripciones, apoyándose en la orquestación hexagonal de `billing`.
1. **Pasarela de Pagos**: Implementada como puerto (`PaymentGateway`) con adaptador ficticio.
2. **Arquitectura Hexagonal**: Estándar consolidado para permitir el cambio a pasarelas reales (Transbank/SII) sin afectar el dominio.

## Decisiones Técnicas Recientes
1. **UUID Mandatory**: No se permiten IDs secuenciales en el nuevo esquema de persistencia.
2. **Integration Harness**: Cada microservicio backend DEBE tener su clase `BaseIntegrationTest` con soporte multiesquema en H2.
3. **Shift-Left Security**: Auditoría proactiva con Gitleaks y Semgrep tras cada hito.
4. **Bilingual Mirroring**: Toda documentación estratégica debe existir en espejo (ES/EN).

## Próximos Objetivos
1. Migración UUID del microservicio `sales`.
2. Integración de reglas del SII en el adaptador de `billing`.
3. Sincronización de tipos UUID en los frontends (`webapp`, `commercial`).

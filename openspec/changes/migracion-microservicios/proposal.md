# Proposal: Arquitectura de Microservicios SIGA v2.0

## Intent
Descomponer el monolito Spring Boot en servicios independientes por Bounded Context. Resolver los huecos de la v1: ubicación de Webapp, servicio de Auth dedicado, y acoplamiento UsuarioComercial↔UsuarioSaas.

## Scope

### In Scope
- 5 microservicios backend + 1 Gateway + 3 clientes frontend.
- Servicio de Auth centralizado con JWT y `tenant_id`.
- Estrategia de Fallback SQL para el Asistente IA.
- Database-per-service con bases de datos lógicas aisladas.

## Capabilities
- `siga-gateway`: Ruteo dinámico, validación JWT.
- `siga-auth`: Gestión de acceso y tokens.
- `siga-asistente`: Chat con RAG de productos y fallback SQL.
- `siga-billing`: Facturación y planes.

## Affected Areas
- `services/backend/`
- `services/gateway/`
- `services/auth/`
- `docker-compose.yml`

## Risks
- Latencia inter-servicio (Mitigación: Caché y Circuit Breakers).
- Consistencia eventual (Mitigación: Sagas simples).

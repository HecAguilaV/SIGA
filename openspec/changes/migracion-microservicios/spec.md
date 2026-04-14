# Specifications: Arquitectura Microservicios SIGA

## siga-gateway Specification

### Requirement: Ruteo Dinámico
El Gateway MUST enrutar peticiones a los servicios internos según prefijo de ruta.

#### Scenario: Ruteo a Auth
- GIVEN una petición entrante a `/api/auth/**`
- WHEN el Gateway la recibe
- THEN debe redirigirla al servicio `siga-auth` usando Balanceo de Carga (Eureka).

#### Scenario: Validación JWT Global
- GIVEN una petición a un recurso protegido `/api/inventario/**`
- WHEN la petición no contiene un JWT válido
- THEN el Gateway debe retornar `401 Unauthorized`.

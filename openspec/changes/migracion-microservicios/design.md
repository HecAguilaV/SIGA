# Updated Design: Arquitectura Microservicios SIGA v2.2 (Eureka)

## Infrastructure
Se añade el componente **Service Registry (Netflix Eureka)**.

## Architecture Decisions

### Decision: Service Discovery con Netflix Eureka
**Choice**: Utilizar Spring Cloud Netflix Eureka.
**Rationale**: Elimina el hardcoding de IPs y permite escalabilidad dinámica.

### Decision: Dynamic Routing via Gateway
**Choice**: El Gateway utilizará `lb://` prefix para resolver instancias desde Eureka.

## File Changes
- `services/registry/` [NEW]
- `docker-compose.yml` [MODIFIED]
- Microservicios [MODIFIED: Discovery Client]

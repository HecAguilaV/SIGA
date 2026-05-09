# Gateway (siga-gateway)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Single entry point (API Gateway) for all platform traffic.

## Tech Stack
- **Framework**: Spring Cloud Gateway
- **Reactive**: Spring WebFlux
- **Security**: JWT Filter / Centralized CORS

## Key Functions
- **Dynamic Routing**: Forwards requests to microservices registered in Eureka.
- **Security**: JWT token validation before allowing access.
- **Resilience**: Circuit Breakers (Resilience4j).
- **Swagger Aggregation**: Centralized documentation for all services.

## Interconnections
- **Registry**: Queries `siga-registry` to find active instances.
- **Auth**: Delegates security validation.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

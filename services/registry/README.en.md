# Service Registry (siga-registry)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

Service discovery server for the microservices ecosystem.

## Tech Stack
- **Framework**: Spring Cloud Netflix Eureka Server

## Purpose
Allows microservices to locate each other using logical names (e.g. `siga-sales`) instead of static IP addresses, enabling horizontal scaling.

## Interconnections
- **Clients**: All SIGA microservices register with this server on startup.
- **Gateway**: The main client that queries this registry for traffic routing.

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

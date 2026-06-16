# Auth Service (siga-auth)

*Leer en otros idiomas: [![Español](README.md)](README.md)*

This service manages identity, security, and centralized access control for the **SIGA** platform.

## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot 3.4.3
- **Security**: Spring Security + JWT
- **DB**: PostgreSQL (Schema: `auth`)

## APIs & Contracts
- **Authentication**: `POST /api/v1/auth/login`
- **Registration**: `POST /api/v1/auth/register`
- **Validation**: `GET /api/v1/auth/validate`
- **Swagger**: `http://localhost:8081/swagger-ui.html`

## Interconnections
- **Consumed by**: `siga-gateway` (for token validation) and all microservices requiring user identity.
- **Service Registry**: Registers with `siga-registry` (Eureka).

## Architecture
- [ ] Hexagonal (Pending refactor)
- [x] UUID v4 (Law 21.719)
- [ ] SAGA (Not currently required)

---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

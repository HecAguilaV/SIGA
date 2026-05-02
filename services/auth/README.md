# Auth Service (siga-auth)

Este servicio gestiona la identidad, seguridad y control de acceso centralizado de la plataforma **SIGA**.

## Stack Tecnológico
- **Lenguaje**: Kotlin
- **Framework**: Spring Boot 3.2.x
- **Seguridad**: Spring Security + JWT
- **BD**: PostgreSQL (Esquema: `auth`)

## APIs & Contratos
- **Autenticación**: `POST /api/v1/auth/login`
- **Registro**: `POST /api/v1/auth/register`
- **Validación**: `GET /api/v1/auth/validate`
- **Swagger**: `http://localhost:8081/swagger-ui.html`

## Interrelaciones
- **Es consumido por**: `siga-gateway` (para validación de tokens) y todos los microservicios que requieran identidad de usuario.
- **Service Registry**: Se registra en `siga-registry` (Eureka).

## Arquitectura
- [x] Hexagonal
- [x] UUID v4 (Ley 21.719)
- [ ] SAGA (No requerido actualmente)


---
Héctor Aguila
`> Un Soñador con Poca RAM 👨🏻‍💻`

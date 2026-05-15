# Delta for database

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: customer-auth

## ADDED Requirements

### Requirement: Tabla refresh_tokens

El sistema DEBE almacenar refresh tokens en una tabla dedicada `refresh_tokens` para soportar rotación, revocación y detección de reuso.

#### Scenario: Creación de refresh_token

- GIVEN un login exitoso que emite refresh token
- WHEN se persiste en DB
- THEN se inserta en `refresh_tokens` con: `id` (UUID), `token_hash` (SHA-256), `principal_id` (UUID del Customer o User), `principal_type` (CUSTOMER | USER), `expires_at`, `created_at`, `revoked` (boolean, default false)

#### Scenario: Rotación marca anterior como revocado

- GIVEN un refresh token `RTv1` en uso
- WHEN se emite `RTv2` por rotación
- THEN `RTv1.revoked = true`
- AND `RTv2` se inserta como nuevo registro

#### Scenario: Detección de robo — revocación masiva

- GIVEN `RTv1` ya tiene `revoked=true`
- WHEN llega un request con `RTv1`
- THEN se ejecuta `UPDATE refresh_tokens SET revoked=true WHERE principal_id = <id>`
- AND todos los tokens activos del principal quedan revocados

#### Scenario: Limpieza de tokens expirados

- GIVEN tokens con `expires_at < NOW() - 30 days`
- WHEN corre el job de limpieza (diario)
- THEN se eliminan físicamente de la tabla

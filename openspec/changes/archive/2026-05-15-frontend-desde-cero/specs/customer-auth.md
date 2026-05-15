# Delta for customer-auth

**Change**: frontend-desde-cero
**Status**: Draft

## ADDED Requirements

### R9: Refresh Token Endpoint

`POST /api/v1/auth/refresh`. Recibe refresh token válido, rotación: invalida el anterior, emite nuevo par (JWT + refresh token).

El sistema DEBE implementar refresh token con rotación: cada vez que se usa un refresh token, se invalida y se emite uno nuevo. Si un refresh token ya usado se reenvía, el sistema DEBE revocar TODOS los refresh tokens del usuario (detección de robo).

#### Scenario: Refresh exitoso con rotación

- GIVEN un usuario con refresh token válido `RTv1`
- WHEN POST /api/v1/auth/refresh con `RTv1`
- THEN 200 + nuevo JWT + nuevo refresh token `RTv2`
- AND `RTv1` invalidado (no puede reusarse)

#### Scenario: Reuso de refresh token (robo detectado)

- GIVEN el refresh token `RTv1` ya fue usado y rotado a `RTv2`
- WHEN POST /api/v1/auth/refresh con `RTv1` (el viejo)
- THEN 401 Unauthorized
- AND TODOS los refresh tokens del usuario se revocan
- AND el usuario debe volver a login

#### Scenario: Refresh token expirado

- GIVEN un refresh token con más de 7 días de antigüedad
- WHEN POST /api/v1/auth/refresh
- THEN 401 Unauthorized
- AND el token se invalida

### R10: Refresh Token — Almacenamiento y Seguridad

El sistema DEBE almacenar refresh tokens hasheados (SHA-256) en la base de datos, asociados a `userId`/`customerId`, con `expiresAt` e `issuedAt`.

#### Scenario: Almacenamiento hasheado

- GIVEN un refresh token emitido
- WHEN se persiste en DB
- THEN solo el hash SHA-256 del token se guarda, nunca el token en texto plano

## MODIFIED Requirements

No se modifican requirements existentes. Se agregan R9 y R10.

## Acceptance Criteria
- [ ] `POST /api/v1/auth/refresh` implementado con rotación
- [ ] Reuso detectado → revocación total de tokens del usuario
- [ ] Refresh tokens almacenados como hash SHA-256
- [ ] Expiración de refresh token a los 7 días

# Spec: ui-auth-flow

**Change**: frontend-desde-cero
**Status**: Draft
**Depends on**: ui-bff, customer-auth (refresh token endpoint)

## Requirements

### Functional

- REQ-AUTH-01: El sistema DEBE autenticar usuarios vía email/password con login dual: prueba primero como Customer, luego como User.
- REQ-AUTH-02: El JWT DEBE almacenarse como httpOnly cookie (server-managed). El cliente NUNCA accede al token.
- REQ-AUTH-03: El sistema DEBE refrescar el token automáticamente vía `POST /api/v1/auth/refresh` desde `hooks.server.ts` antes de que expire.
- REQ-AUTH-04: El sistema DEBE redirigir al usuario a la ruta que intentaba acceder después del login exitoso (redirect query param).
- REQ-AUTH-05: El sistema DEBE proteger rutas mediante route guards en `hooks.server.ts` que verifican la cookie JWT.
- REQ-AUTH-06: El sistema DEBE implementar logout que elimina la cookie de sesión y redirige a `/login`.
- REQ-AUTH-07: El sistema DEBE mantener la sesión activa durante la vida de la página (persiste en httpOnly cookie, no localStorage).

### Flujo completo

```
1. Usuario visita /dashboard (protegida)
2. hooks.server.ts verifica cookie JWT → no existe
3. Redirige a /login?redirect=/dashboard
4. Usuario ingresa credenciales → POST /api/v1/auth/login
5. Servidor recibe JWT + refresh_token, setea httpOnly cookie
6. Redirige a /dashboard (preservando redirect)
7. hooks.server.ts verifica cookie JWT → OK, carga dashboard
8. hooks.server.ts verifica expiración → si próximo a expirar, refresca
```

### Non-functional

- REQ-AUTH-08: El refresh automático DEBE ocurrir antes de que expire el token (umbral: 5 minutos antes de expiración).
- REQ-AUTH-09: El login DEBE responder en < 2s (P95). El refresh DEBE ser < 500ms.

## Scenarios (GWT)

### Scenario: Login Customer exitoso
Given un Customer activo con credenciales válidas en `/login?redirect=/dashboard`
When completa el formulario de login
Then `POST /api/v1/auth/login` retorna JWT + refresh_token
Y se setea httpOnly cookie
Y redirige a `/dashboard`

### Scenario: Login User exitoso
Given un User activo de un tenant con credenciales válidas
When completa el formulario de login
Then `POST /api/v1/auth/login` retorna JWT con `principalType=user` y `rol` en claims
Y redirige según su rol (dashboard admin, caja, etc.)

### Scenario: Credenciales inválidas
Given cualquier principal registrado
When ingresa password incorrecto
Then `POST /api/v1/auth/login` retorna 401
Y se muestra mensaje genérico "Credenciales inválidas" (sin disclosure de existencia)

### Scenario: Refresh token exitoso
Given un usuario con JWT próximo a expirar (< 5 min)
When `hooks.server.ts` detecta expiración inminente
Then llama `POST /api/v1/auth/refresh` con refresh_token
Y setea nueva httpOnly cookie con nuevo JWT
Y el request original continúa sin interrupción

### Scenario: Refresh token falla (expirado)
Given un usuario con refresh token expirado
When `hooks.server.ts` intenta refrescar
Then recibe 401 del gateway
Y elimina la cookie de sesión
Y redirige a `/login?redirect=<ruta_original>`

### Scenario: Ruta protegida sin sesión
Given un usuario no autenticado
When intenta acceder a `/products`
Then `hooks.server.ts` redirige a `/login?redirect=/products`

### Scenario: Logout
Given un usuario autenticado
When hace clic en "Cerrar sesión"
Then se elimina la httpOnly cookie
Y el token se invalida en backend (refresh token revocado)
Y redirige a `/login`

### Scenario: Post-login redirect preservado
Given un usuario no autenticado que intenta acceder a `/products/42/edit`
When se loguea exitosamente
Then redirige a `/products/42/edit` (no al dashboard default)

## Edge Cases
- REQ-AUTH-10: Si el refresh y un request concurrente ocurren simultáneamente, el sistema DEBE evitar race conditions (cola de refrescos, no disparar múltiples refresh).
- REQ-AUTH-11: El sistema DEBE rechazar refresh token reutilizado (rotación: al refrescar, el refresh_token anterior se invalida).

## Acceptance Criteria
- [ ] Login dual Customer/User funcional
- [ ] httpOnly cookie implementada y verificada
- [ ] Refresh automático antes de expiración
- [ ] Route guards protegen rutas protegidas
- [ ] Redirect post-login preserva ruta original
- [ ] Logout invalida sesión completa

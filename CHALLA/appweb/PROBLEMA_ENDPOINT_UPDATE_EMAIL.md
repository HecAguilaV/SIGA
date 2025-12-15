# Problema: Endpoint update-email no disponible (404)

## Descripción del problema

El endpoint `PUT /api/comercial/auth/update-email` está retornando **404 Not Found**, aunque según `FUENTE_VERDAD_BACKEND.md` debería estar implementado y funcionando.

## Errores observados

### Error 1: Endpoint update-email no encontrado
```
PUT https://siga-backend-production.up.railway.app/api/comercial/auth/update-email 404 (Not Found)
Error: Endpoint no encontrado (404): https://siga-backend-production.up.railway.app/api/comercial/auth/update-email
```

### Error 2: Problema con email en checkout
```
Failed to load resource: the server responded with a status of 404 ()
```

**Contexto:** Usuario se registró correctamente, pero al intentar procesar el pago, el sistema indica que no tiene email registrado.

## Endpoint según documentación

Según `FUENTE_VERDAD_BACKEND.md` (línea 25):
- ✅ `PUT /api/comercial/auth/update-email` - Actualizar email (requiere auth + contraseña)
- ⚠️ Método: `PUT` (no POST)

## Comportamiento esperado

1. El endpoint `PUT /api/comercial/auth/update-email` debería existir y funcionar
2. Debería permitir actualizar el email del usuario comercial autenticado
3. Requiere autenticación + contraseña actual

## Comportamiento actual

- El endpoint retorna **404 Not Found**
- No es posible actualizar el email desde el perfil
- Hay problemas con el email en el flujo de checkout

## Impacto

- **Web Comercial:** Los usuarios no pueden actualizar su email desde el perfil
- **Checkout:** Problemas al procesar pagos porque el sistema no reconoce el email del usuario registrado

## Acción requerida del backend

1. **Verificar que el endpoint esté implementado:**
   - `PUT /api/comercial/auth/update-email`
   - Verificar que esté desplegado en producción

2. **Verificar el flujo de registro:**
   - Asegurar que el email se guarde correctamente durante el registro
   - Verificar que el email esté disponible para el checkout

3. **Verificar Swagger:**
   - Confirmar que el endpoint aparezca en: `https://siga-backend-production.up.railway.app/swagger-ui.html`

## Fecha del reporte

2025-01-XX

## Notas adicionales

- El usuario se registró correctamente con un email
- El problema aparece al intentar procesar el pago (checkout)
- El problema también aparece al intentar actualizar el email desde el perfil

# 🚨 Error SSO: Backend rechaza intercambio de token operativo

**Fecha:** 2025-01-XX  
**Equipo:** WebComercial → Backend/WebApp  
**Prioridad:** 🔴 ALTA

---

## 📋 RESUMEN

Al acceder a la WebApp desde el portal comercial mediante SSO, el backend está rechazando el intercambio del token de acceso comercial por un token operativo, retornando **401 (Unauthorized)**.

---

## 🔍 FLUJO ACTUAL

1. **Portal Comercial (WebComercial)** genera token SSO exitosamente
2. **Portal Comercial** redirige a WebApp con token en URL: `https://siga-appweb.vercel.app?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. **WebApp** recibe el token y lo envía al backend para intercambiarlo
4. **Backend** rechaza el intercambio con **401 (Unauthorized)**

---

## ❌ ERRORES OBSERVADOS

### Error Principal
```
POST https://siga-backend-production.up.railway.app/api/comercial/auth/obtener-token-operativo
401 (Unauthorized)
Error: No autorizado. Por favor, inicia sesión nuevamente.
```

### Logs de la WebApp
```
🔵 API Request: https://siga-backend-production.up.railway.app/api/saas/locales GET
🔵 API Request: https://siga-backend-production.up.railway.app/api/saas/productos GET
🔵 API Request: https://siga-backend-production.up.railway.app/api/saas/categorias GET
🔵 API Request: https://siga-backend-production.up.railway.app/api/saas/stock GET
🔵 API Request: https://siga-backend-production.up.railway.app/api/comercial/auth/obtener-token-operativo POST
POST .../obtener-token-operativo 401 (Unauthorized)
❌ API Error: Error: No autorizado. Por favor, inicia sesión nuevamente.
Error al intercambiar token operativo: Error: No autorizado. Por favor, inicia sesión nuevamente.
Error en SSO: Error: No autorizado. Por favor, inicia sesión nuevamente.
```

### Request Body Enviado por WebApp
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzaWdhLWJhY2tlbmQiLCJzdWIiOiIxIiwiZW1haWwiOiJhZG1pbi50ZXN0QHNpZ2EuY29tIiwidHlwZSI6ImFjY2VzcyIsInJvbCI6IkFETUlOSVNUUkFET1IiLCJleHAiOjE3NjU3ODkzODQsImlhdCI6MTc2NTcwMjk4NH0.bZOAgN9xvOOrJAvSxhVYiTZR5QWIt-x0sa9in051avg"
}
```

### Config de Request
```javascript
{
  requireAuth: false,
  method: 'POST',
  body: '{"token":"..."}',
  headers: {
    'Content-Type': 'application/json'
  }
}
```

---

## ✅ FLUJO CORRECTO DEL PORTAL COMERCIAL

El portal comercial está funcionando correctamente:

1. **Usuario autenticado** en portal comercial
2. **Verificación de suscripción activa** ✅
3. **Llamada a `obtenerTokenOperativo()`** ✅
   - Endpoint: `POST /api/comercial/auth/obtener-token-operativo`
   - Headers: `Authorization: Bearer {accessToken}` (token comercial)
   - Body: (vacío, el token va en el header)
4. **Backend responde con token operativo** ✅
5. **Portal comercial redirige a WebApp** con token en URL ✅

### Código del Portal Comercial
```javascript
// src/services/api.js
export async function obtenerTokenOperativo() {
  return apiRequest('/comercial/auth/obtener-token-operativo', {
    method: 'POST',
    // El token comercial se envía automáticamente en el header Authorization
  });
}

// src/pages/PerfilPage.jsx
const ssoResponse = await obtenerTokenOperativo();
const tokenOperativo = ssoResponse.accessToken || ssoResponse.data?.accessToken;
window.location.href = `${webAppUrl}?token=${tokenOperativo}`;
```

---

## 🔍 PROBLEMA IDENTIFICADO

### Diferencia en el uso del endpoint

**Portal Comercial (CORRECTO):**
- Envía el token comercial en el **header Authorization**: `Authorization: Bearer {token}`
- Body vacío o sin token

**WebApp (INCORRECTO):**
- Envía el token en el **body**: `{"token": "..."}`
- No envía el token en el header Authorization
- `requireAuth: false` (no agrega header Authorization)

### Posible Causa

El endpoint `/api/comercial/auth/obtener-token-operativo` probablemente espera:
1. El token comercial en el **header Authorization** (como lo hace el portal comercial)
2. O el token en el body, pero con validación diferente

La WebApp está enviando el token en el body, pero el backend puede estar esperando:
- El token en el header Authorization, O
- Un formato diferente en el body, O
- Validación adicional que falta

---

## 🎯 ACCIONES REQUERIDAS

### Para el Equipo Backend

1. **Verificar el endpoint `/api/comercial/auth/obtener-token-operativo`**
   - ¿Qué formato espera? ¿Header Authorization o body?
   - ¿Qué validaciones hace?
   - ¿Por qué está rechazando el token que envía la WebApp?

2. **Revisar logs del backend**
   - ¿Qué token está recibiendo?
   - ¿Por qué falla la validación?
   - ¿El token es válido pero falta alguna validación?

3. **Estandarizar el formato**
   - Si el endpoint acepta token en body, documentarlo
   - Si solo acepta en header, la WebApp debe ajustarse
   - Asegurar que ambos formatos funcionen o documentar cuál usar

### Para el Equipo WebApp

1. **Revisar cómo se envía el token**
   - Actualmente envía: `{"token": "..."}` en el body
   - Considerar enviar en header Authorization como el portal comercial

2. **Ajustar el request**
   - Si el backend espera header: `Authorization: Bearer {token}`
   - Si el backend espera body: verificar formato exacto requerido

---

## 📊 INFORMACIÓN TÉCNICA

### Token Enviado (ejemplo)
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzaWdhLWJhY2tlbmQiLCJzdWIiOiIxIiwiZW1haWwiOiJhZG1pbi50ZXN0QHNpZ2EuY29tIiwidHlwZSI6ImFjY2VzcyIsInJvbCI6IkFETUlOSVNUUkFET1IiLCJleHAiOjE3NjU3ODkzODQsImlhdCI6MTc2NTcwMjk4NH0.bZOAgN9xvOOrJAvSxhVYiTZR5QWIt-x0sa9in051avg
```

### Payload del Token (decodificado)
```json
{
  "iss": "siga-backend",
  "sub": "1",
  "email": "admin.test@siga.com",
  "type": "access",
  "rol": "ADMINISTRADOR",
  "exp": 1765789384,
  "iat": 1765702984
}
```

### Endpoint Afectado
- `POST /api/comercial/auth/obtener-token-operativo`

### Headers Enviados por WebApp
```
Content-Type: application/json
(No incluye Authorization header)
```

### Headers Enviados por Portal Comercial (funciona)
```
Content-Type: application/json
Authorization: Bearer {accessToken}
```

---

## 🔄 FLUJO ESPERADO

1. **Portal Comercial** genera token SSO ✅
2. **Portal Comercial** redirige a WebApp con token en URL ✅
3. **WebApp** recibe token de la URL ✅
4. **WebApp** envía token al backend para intercambio ⚠️ (falla aquí)
5. **Backend** valida token y retorna token operativo ❌
6. **WebApp** usa token operativo para acceder a `/api/saas/*` ❌

---

## 📝 RECOMENDACIONES

### Opción 1: WebApp envía token en header (recomendado)
```javascript
// WebApp debería hacer:
fetch('/api/comercial/auth/obtener-token-operativo', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}` // Token de la URL
  }
});
```

### Opción 2: Backend acepta token en body
Si el backend debe aceptar token en body, verificar:
- Formato exacto esperado
- Validaciones requeridas
- Por qué está rechazando el token actual

### Opción 3: Estandarizar ambos formatos
El backend podría aceptar token en:
- Header Authorization (como portal comercial)
- Body `{"token": "..."}` (como WebApp actualmente)

---

## 🐛 DEBUGGING

### Información Necesaria del Backend

1. **Logs del endpoint `/api/comercial/auth/obtener-token-operativo`**
   - ¿Qué token está recibiendo?
   - ¿En qué formato (header o body)?
   - ¿Qué validaciones está haciendo?
   - ¿Por qué falla la validación?

2. **Validación del token**
   - ¿El token es válido?
   - ¿El token está expirado?
   - ¿Falta alguna validación de suscripción activa?
   - ¿El usuario tiene permisos para obtener token operativo?

3. **Comparación con portal comercial**
   - ¿Por qué funciona desde portal comercial pero no desde WebApp?
   - ¿Qué diferencia hay en las requests?

---

## 📞 CONTACTO

**Equipo WebComercial** está disponible para:
- Proporcionar más logs
- Probar diferentes formatos de request
- Coordinar la solución

---

## ✅ ESTADO ACTUAL

- **Portal Comercial:** ✅ Funcionando correctamente
- **Generación de token SSO:** ✅ Funcionando
- **Redirección a WebApp:** ✅ Funcionando
- **Intercambio de token (WebApp → Backend):** ✅ **CORREGIDO** - Ahora envía token en header Authorization

---

## 🔍 ANÁLISIS ADICIONAL - PROBLEMAS IDENTIFICADOS

### Problema 1: No reconoce admin
**Descripción:** El endpoint `/api/saas/usuarios/1/permisos` retorna 200, pero la WebApp no interpreta correctamente los permisos de admin.

**Análisis:**
- El endpoint retorna 200 correctamente
- La WebApp tiene lógica para asumir que `ADMINISTRADOR` tiene todos los permisos
- **No es un problema crítico** porque el frontend ya maneja esto correctamente:
  - Si `rol === 'ADMINISTRADOR'`, la función `tienePermiso()` siempre retorna `true`
  - El store de permisos ignora los permisos del backend para ADMINISTRADOR

**Acción WebApp:**
- ✅ Ya implementado: El frontend asume acceso total para ADMINISTRADOR
- ✅ Mejorado: Agregado logging detallado para ver qué retorna el backend

**Acción Backend (opcional):**
- El backend podría retornar un array vacío o un array con todos los permisos para ADMINISTRADOR
- No es crítico porque el frontend ignora estos permisos de todas formas

---

### Problema 2: "No se pudo determinar la empresa"
**Descripción:** El token operativo generado en login directo no incluye `nombreEmpresa` en el payload.

**Análisis:**
- El token solo tiene: `iss, sub, email, type, rol` — falta `nombreEmpresa` (o `empresaId`)
- El backend necesita la empresa para filtrar datos en endpoints `/api/saas/*`
- Este es un **problema del backend**, no del frontend

**Solución Backend:**
El backend debe incluir `nombreEmpresa` (o `empresaId`) en el payload del token operativo cuando se genera en:
1. ✅ Login directo (`POST /api/auth/login`)
2. ✅ Intercambio SSO (`POST /api/comercial/auth/obtener-token-operativo`)

**Acción WebApp:**
- ✅ Mejorado: Agregado logging para extraer `empresaId` del token si está disponible
- ✅ Mejorado: Mensajes de error más claros cuando falta empresa

---

## 📋 RESUMEN DE ACCIONES

### Para el Equipo Backend

1. **Incluir `nombreEmpresa` en token operativo:**
   - Agregar `nombreEmpresa` (o `empresaId`) al payload del token JWT en:
     - `POST /api/auth/login` (login directo)
     - `POST /api/comercial/auth/obtener-token-operativo` (SSO)
   - Esto permitirá que los endpoints `/api/saas/*` determinen la empresa automáticamente

2. **Permisos de ADMINISTRADOR (opcional):**
   - El backend puede retornar permisos vacíos o todos los permisos para ADMINISTRADOR
   - No es crítico porque el frontend ignora estos permisos de todas formas

### Para el Equipo WebApp

1. ✅ **Permisos de admin:** Ya implementado correctamente
2. ✅ **Logging mejorado:** Agregado para diagnosticar problemas
3. ✅ **Mensajes de error:** Mejorados para el problema de empresa

---

---

## ✅ ACTUALIZACIÓN - PROBLEMAS RESUELTOS POR BACKEND

**Fecha:** 2025-01-XX  
**Estado:** ✅ **RESUELTO**

### Cambios Realizados por el Backend

1. **Token JWT incluye `usuario_comercial_id` y `nombre_empresa`**
   - El token operativo ahora incluye estos campos en el payload
   - Disponible tanto en login directo como en SSO

2. **Endpoint `obtener-token-operativo` acepta token en header o body**
   - ✅ Funciona con `Authorization: Bearer {token}` (Portal Comercial)
   - ✅ Funciona con `{"token": "..."}` en body (WebApp SSO)
   - Ambos formatos son soportados

3. **SecurityUtils mejorado**
   - `SecurityUtils.getUsuarioComercialId()` lee del token primero (más rápido)
   - Los claims del token están disponibles en SecurityUtils
   - `JwtAuthenticationFilter` incluye claims en detalles

### Cambios Realizados en WebApp

1. ✅ **Actualizado `getCurrentUser()`** para extraer nuevos campos:
   - `nombreEmpresa` (desde `nombre_empresa` o `nombreEmpresa`)
   - `usuarioComercialId` (desde `usuario_comercial_id` o `usuarioComercialId`)

2. ✅ **Mantenida compatibilidad** con campos legacy (`empresaId`, `companyId`, etc.)

3. ✅ **Logging mejorado** para debugging (solo en desarrollo)

---

**Última actualización:** 2025-01-XX  
**Estado:** 
- 🔵 SSO: ✅ **RESUELTO** - Token en header funciona correctamente
- 🟡 Permisos Admin: ✅ **FUNCIONANDO** - Logging mejorado
- 🟢 Empresa no determinada: ✅ **RESUELTO** - Backend incluye `nombre_empresa` en token

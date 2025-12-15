# 🔐 Errores de Permisos y Suscripción

## 📊 Situación Actual

Has hecho login con un usuario **ADMINISTRADOR** del SaaS, pero estás viendo estos errores:

1. **Locales**: `❌ Error: No static resource api/saas/locales`
2. **Categorías**: `❌ Error: No static resource api/saas/categorias`
3. **Productos**: `❌ Error: Se requiere una suscripción activa`

---

## 🔍 Análisis

### Error "No static resource"

Este error es **sospechoso**. Puede significar:

1. **SvelteKit está interceptando las rutas**:
   - SvelteKit tiene su propio sistema de rutas `/api/`
   - Puede estar intentando buscar estos endpoints como archivos estáticos
   - **Solución**: Asegurar que las URLs sean completas (con `https://`)

2. **El backend está retornando un error mal formateado**:
   - El backend podría estar retornando HTML en lugar de JSON
   - **Solución**: Mejorar el manejo de errores

3. **Problema de CORS o routing**:
   - El request no está llegando al backend
   - **Solución**: Verificar en Network tab

### Error "Se requiere una suscripción activa"

Este es un **error válido del backend**. Según la documentación:

> Todos los endpoints (excepto los de autenticación) requieren:
> 1. Token JWT válido ✅ (tienes esto)
> 2. **Suscripción activa** ❌ (esto falta)

**Esto significa**:
- ✅ El request SÍ está llegando al backend
- ✅ El token JWT está siendo enviado correctamente
- ✅ El usuario tiene rol ADMINISTRADOR
- ❌ **El usuario no tiene una suscripción activa en la base de datos**

---

## ✅ Soluciones

### 1. Verificar en Network Tab (CRÍTICO)

1. Abre DevTools (F12)
2. Ve a **Network**
3. Filtra por "Fetch/XHR"
4. Intenta los endpoints que fallan
5. **Revisa cada request**:

   **Para Locales/Categorías**:
   - **Request URL**: ¿Es `https://siga-backend-production.up.railway.app/api/saas/locales`?
   - **Request Method**: ¿Es `GET`?
   - **Request Headers**: ¿Incluye `Authorization: Bearer ...`?
   - **Response**: ¿Qué respuesta exacta viene? (código HTTP, body)

   **Si la URL NO es completa** (empieza con `/api/` en lugar de `https://`):
   - ❌ Hay un problema con cómo se construyen las URLs
   - 🔧 Revisar `api-client.js` y `config.js`

   **Si la URL SÍ es completa pero da error**:
   - ✅ El código está bien
   - ❌ El problema es del backend o de la suscripción

### 2. Activar Suscripción del Usuario

El error de productos indica que necesitas una **suscripción activa**.

**Opciones**:

1. **Contactar al equipo backend**:
   - Pedir que activen una suscripción de prueba para tu usuario admin
   - O crear un usuario de prueba con suscripción activa

2. **Verificar en la base de datos** (si tienes acceso):
   ```sql
   -- Verificar suscripción del usuario
   SELECT u.id, u.email, u.rol, s.activa, s.fecha_inicio, s.fecha_fin
   FROM siga_comercial.USUARIOS u
   LEFT JOIN siga_comercial.SUSCRIPCIONES s ON u.suscripcion_id = s.id
   WHERE u.email = 'tu-email@example.com';
   ```

3. **Crear suscripción de prueba** (si tienes acceso al backend):
   - Insertar registro en `siga_comercial.SUSCRIPCIONES`
   - Asociar al usuario

### 3. Verificar Permisos del Usuario

Aunque el usuario sea ADMINISTRADOR, necesita:
- ✅ Token JWT válido (tienes esto)
- ✅ Suscripción activa (falta esto)

**Los permisos de rol (ADMINISTRADOR) son para**:
- Crear/editar/eliminar productos (POST, PUT, DELETE)
- **NO para** leer productos (GET) - eso requiere suscripción activa

---

## 🔧 Cambios Realizados

### Mejoras en `api-client.js`:

1. **Manejo de errores mejorado**:
   - Detecta errores específicos (401, 403, 402, 404, 500+)
   - Mensajes más claros según el código de estado
   - Detecta el error "No static resource" y da un mensaje más útil

2. **Logging mejorado**:
   - Muestra URL completa de cada request
   - Facilita el debugging

---

## 📝 Próximos Pasos

### Paso 1: Verificar Network Tab

**CRÍTICO**: Esto te dirá exactamente qué está pasando.

1. Abre DevTools → Network
2. Intenta los endpoints
3. Revisa:
   - ¿La URL es correcta? (debe ser `https://siga-backend-production...`)
   - ¿El header Authorization está presente?
   - ¿Qué respuesta exacta viene del backend?

### Paso 2: Contactar al Equipo Backend

Comparte esta información:
- ✅ Login funciona con usuario ADMINISTRADOR
- ❌ Locales: Error "No static resource" (verificar en Network tab)
- ❌ Categorías: Error "No static resource" (verificar en Network tab)
- ❌ Productos: Error "Se requiere una suscripción activa"
- 📋 Screenshot del Network tab
- 📋 Request URL, Headers, Response

### Paso 3: Activar Suscripción

Una vez que el backend active la suscripción:
- ✅ Los endpoints deberían funcionar
- ✅ Podrás probar la integración completa

---

## 🎯 Interpretación de Resultados

### Si en Network tab ves URLs completas (`https://...`):
- ✅ El código está bien
- ❌ El problema es del backend o de la suscripción
- 🔧 Contactar al equipo backend

### Si en Network tab ves URLs relativas (`/api/...`):
- ❌ Hay un problema con cómo se construyen las URLs
- 🔧 Revisar `api-client.js` línea 102

### Si no hay header Authorization:
- ❌ El token no se está enviando
- 🔧 Verificar que el login guardó el token en localStorage

---

## ✅ Checklist

- [ ] Revisar Network tab en DevTools
- [ ] Verificar que las URLs sean completas (`https://...`)
- [ ] Verificar que el header Authorization esté presente
- [ ] Verificar la respuesta exacta del backend
- [ ] Contactar al equipo backend para activar suscripción
- [ ] Probar nuevamente después de activar suscripción

---

**El problema principal es la suscripción activa. Una vez resuelto, los endpoints deberían funcionar.** 🔐

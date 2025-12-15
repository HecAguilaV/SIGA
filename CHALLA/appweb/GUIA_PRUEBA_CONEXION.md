# 🧪 Guía Paso a Paso - Probar Conexión con Backend

## ✅ Resultados de Pruebas Automáticas

He probado la conexión y estos son los resultados:

### 1. Health Check ✅
- **Estado**: ✅ **FUNCIONANDO**
- **URL**: `https://siga-backend-production.up.railway.app/health`
- **Respuesta**: `{"status":"healthy","timestamp":"..."}`
- **Código HTTP**: 200 OK

### 2. Endpoint de Login ✅
- **Estado**: ✅ **ACCESIBLE**
- **URL**: `https://siga-backend-production.up.railway.app/api/auth/login`
- **Respuesta**: El endpoint responde correctamente (requiere credenciales válidas)

### 3. Configuración ✅
- **Archivo `.env.local`**: ✅ Creado con `VITE_API_BASE_URL`
- **Configuración**: Lista para usar

---

## 🚀 Cómo Probar la Conexión Tú Mismo

### Paso 1: Verificar que el servidor esté corriendo

Abre una terminal en la raíz del proyecto y ejecuta:

```bash
npm run dev
```

Deberías ver algo como:
```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: use --host to expose
```

### Paso 2: Abrir la página de prueba

Abre tu navegador y ve a:

```
http://localhost:5173/prueba-conexion
```

### Paso 3: Verificar Health Check

Al cargar la página, automáticamente se probará el health check. Deberías ver:

- ✅ **Estado**: `exito` (verde)
- ✅ **Mensaje**: "✅ Backend accesible"
- ✅ **Datos**: `{"status":"healthy","timestamp":"..."}`

Si ves esto, el backend está accesible.

### Paso 4: Probar Login

1. **Necesitas credenciales válidas** del backend. Si no las tienes:
   - Contacta al equipo backend para obtener credenciales de prueba
   - O crea un usuario usando el endpoint de registro

2. **Ingresa las credenciales** en el formulario:
   - Email: `tu-email@example.com`
   - Contraseña: `tu-contraseña`

3. **Haz clic en "🔐 Probar Login"**

4. **Resultado esperado**:
   - ✅ **Estado**: `exito` (verde)
   - ✅ **Mensaje**: "✅ Login exitoso"
   - ✅ **Datos**: Información del usuario y confirmación de token guardado

### Paso 5: Probar Endpoints Autenticados

Una vez que el login sea exitoso, automáticamente se probarán:

1. **Locales** (`GET /api/saas/locales`)
   - Debería mostrar: "✅ X locales obtenidos"
   - Puedes expandir "Ver datos" para ver la lista

2. **Categorías** (`GET /api/saas/categorias`)
   - Debería mostrar: "✅ X categorías obtenidas"
   - Puedes expandir "Ver datos" para ver la lista

3. **Productos** (`GET /api/saas/productos`)
   - Debería mostrar: "✅ X productos obtenidos"
   - Puedes expandir "Ver datos" para ver la lista

### Paso 6: Revisar el Resumen

Al final de la página hay una **tabla resumen** que muestra el estado de todas las pruebas:

| Endpoint | Estado | Mensaje |
|----------|--------|---------|
| Health Check | ✅ exito | ✅ Backend accesible |
| Login | ✅ exito | ✅ Login exitoso |
| Locales | ✅ exito | ✅ X locales obtenidos |
| Categorías | ✅ exito | ✅ X categorías obtenidas |
| Productos | ✅ exito | ✅ X productos obtenidos |

---

## ⚠️ Si Algo No Funciona

### Problema: Health Check falla

**Síntomas**:
- Estado: `error` (rojo)
- Mensaje: "❌ Error de conexión: ..."

**Soluciones**:
1. Verifica tu conexión a internet
2. Verifica que la URL del backend sea correcta en `.env.local`
3. Verifica que el backend esté corriendo (contacta al equipo backend)

### Problema: Login falla

**Síntomas**:
- Estado: `error` (rojo)
- Mensaje: "❌ Error: Credenciales inválidas"

**Soluciones**:
1. Verifica que las credenciales sean correctas
2. Verifica que el usuario exista en el backend
3. Contacta al equipo backend para obtener credenciales válidas

### Problema: Endpoints autenticados fallan (401)

**Síntomas**:
- Estado: `error` (rojo)
- Mensaje: "❌ Error: HTTP 401" o "Sesión expirada"

**Soluciones**:
1. Verifica que el login haya sido exitoso
2. Verifica en las DevTools del navegador (F12) → Application → Local Storage
   - Debe haber `accessToken` y `refreshToken`
3. Si no hay tokens, vuelve a hacer login
4. Si hay tokens pero aún falla, el token puede estar expirado (el refresh debería funcionar automáticamente)

### Problema: CORS Error

**Síntomas**:
- Error en consola del navegador sobre CORS
- "Access-Control-Allow-Origin"

**Soluciones**:
1. Esto es un problema del backend, no del frontend
2. Contacta al equipo backend para configurar CORS
3. El backend debe permitir requests desde `http://localhost:5173`

---

## 📊 Interpretación de Resultados

### ✅ Todo Funciona (Verde)
Si todos los endpoints muestran estado `exito`:
- ✅ Backend accesible
- ✅ Autenticación funcionando
- ✅ Endpoints respondiendo correctamente
- **🎉 Listo para migrar componentes**

### ⚠️ Algunos Fallan (Amarillo/Rojo)
Si algunos endpoints fallan:
- Anota cuáles fallan
- Revisa los mensajes de error
- Expande "Ver detalles" para más información
- Comparte con el equipo backend

---

## 🔍 Ver Detalles de Cada Prueba

Cada prueba tiene un botón **"Ver detalles"** que muestra:
- Respuesta completa del servidor
- Datos recibidos
- Información de debug

Esto es útil para:
- Entender qué datos retorna el backend
- Debuggear problemas
- Verificar el formato de los datos

---

## 📝 Notas para el Equipo Backend

Si encuentras problemas:

1. **Anota el error exacto**:
   - Mensaje de error
   - Código HTTP
   - Endpoint que falla

2. **Captura la respuesta**:
   - Usa "Ver detalles" en la página
   - O revisa la consola del navegador (F12)

3. **Información útil**:
   - URL del endpoint
   - Request que envías
   - Response que recibes
   - Headers enviados

4. **Comparte con el equipo**:
   - Esto ayuda a identificar conflictos rápidamente

---

## ✅ Checklist de Pruebas

Marca cada prueba cuando la completes:

- [ ] Health Check: ✅ Funciona
- [ ] Login: ✅ Funciona (con credenciales válidas)
- [ ] Locales: ✅ Funciona
- [ ] Categorías: ✅ Funciona
- [ ] Productos: ✅ Funciona

Si todas están marcadas: **🎉 ¡Listo para migrar componentes!**

---

## 🚀 Siguiente Paso

Una vez que todas las pruebas pasen:

1. ✅ Backend accesible
2. ✅ Autenticación funcionando
3. ✅ Endpoints respondiendo

**Proceder con la migración de componentes** usando los servicios implementados en `src/lib/services/`.

---

**¡Éxito con las pruebas!** 🚀

# ✅ Checklist de Verificación - Integración Backend

## 🎯 Objetivo

Verificar que la integración con el backend funciona correctamente antes de migrar componentes.

---

## 📍 Paso 1: Verificar que el Servidor Está Corriendo

1. Abre tu navegador
2. Ve a: `http://localhost:5173`
3. **Deberías ver**: La página principal de SIGA (Inventario)

✅ **Si ves la página**: Servidor funcionando correctamente  
❌ **Si no carga**: Revisa la terminal por errores

---

## 📍 Paso 2: Acceder a la Página de Prueba

1. En el navegador, ve a: `http://localhost:5173/prueba-conexion`
2. **Deberías ver**: Una página con título "🔌 Prueba de Conexión - Backend SIGA"

✅ **Si ves la página**: Todo correcto  
❌ **Si da 404**: Verifica que el archivo existe en `src/routes/prueba-conexion/+page.svelte`

---

## 📍 Paso 3: Verificar Health Check (Automático)

Al cargar la página, automáticamente se prueba el health check.

**Qué verificar**:
- ✅ **Estado**: Debe mostrar `exito` (verde)
- ✅ **Mensaje**: "✅ Backend accesible"
- ✅ **Datos**: Debe mostrar `{"status":"healthy","timestamp":"..."}`

**Si falla**:
- ❌ Verifica tu conexión a internet
- ❌ Verifica que la URL del backend sea correcta en `.env.local`
- ❌ Verifica que el backend esté corriendo

---

## 📍 Paso 4: Probar Login

**Necesitas credenciales válidas del backend**. Si no las tienes:
- Contacta al equipo backend
- O crea un usuario usando el endpoint de registro

**Pasos**:
1. Ingresa **Email** en el campo correspondiente
2. Ingresa **Contraseña** en el campo correspondiente
3. Haz clic en **"🔐 Probar Login"**

**Qué verificar**:
- ✅ **Estado**: Debe mostrar `exito` (verde)
- ✅ **Mensaje**: "✅ Login exitoso"
- ✅ **Datos**: Debe mostrar información del usuario y `tokenGuardado: true`

**Si falla**:
- ❌ Verifica que las credenciales sean correctas
- ❌ Verifica que el usuario exista en el backend
- ❌ Revisa el mensaje de error específico

---

## 📍 Paso 5: Verificar Endpoints Autenticados (Automático)

Después de un login exitoso, automáticamente se prueban:

### 5.1 Locales
- ✅ **Estado**: `exito` (verde)
- ✅ **Mensaje**: "✅ X locales obtenidos" (donde X es el número)
- ✅ **Datos**: Array de locales

**Si falla**:
- ❌ Verifica que el login haya sido exitoso
- ❌ Verifica que haya locales en el backend
- ❌ Revisa el mensaje de error

### 5.2 Categorías
- ✅ **Estado**: `exito` (verde)
- ✅ **Mensaje**: "✅ X categorías obtenidas"
- ✅ **Datos**: Array de categorías

**Si falla**: Similar a locales

### 5.3 Productos
- ✅ **Estado**: `exito` (verde)
- ✅ **Mensaje**: "✅ X productos obtenidos"
- ✅ **Datos**: Array de productos

**Si falla**: Similar a locales

---

## 📍 Paso 6: Revisar Tabla Resumen

Al final de la página hay una **tabla resumen** con el estado de todas las pruebas.

**Qué verificar**:
- ✅ Todas las pruebas deben estar en verde (`exito`)
- ✅ Todos los mensajes deben ser positivos
- ✅ No debe haber errores (rojo)

**Si hay errores**:
- Anota cuáles fallan
- Revisa los mensajes de error
- Expande "Ver detalles" para más información

---

## 📍 Paso 7: Verificar en DevTools (Opcional pero Recomendado)

1. Abre las **DevTools** del navegador (F12)
2. Ve a la pestaña **Application** → **Local Storage** → `http://localhost:5173`
3. **Deberías ver**:
   - ✅ `accessToken`: Token JWT (string largo)
   - ✅ `refreshToken`: Refresh token (string largo)

**Si no hay tokens**:
- ❌ El login no guardó los tokens correctamente
- ❌ Revisa el código de `api-client.js`

---

## ✅ Checklist Final

Marca cada verificación cuando la completes:

- [ ] Servidor corriendo en `http://localhost:5173`
- [ ] Página de prueba accesible en `/prueba-conexion`
- [ ] Health Check: ✅ Éxito
- [ ] Login: ✅ Éxito (con credenciales válidas)
- [ ] Locales: ✅ Éxito
- [ ] Categorías: ✅ Éxito
- [ ] Productos: ✅ Éxito
- [ ] Tokens guardados en Local Storage
- [ ] Tabla resumen: Todo en verde

---

## 🎉 Si Todo Está Verde

**¡Felicidades!** La integración funciona correctamente.

**Próximos pasos**:
1. ✅ Backend accesible
2. ✅ Autenticación funcionando
3. ✅ Endpoints respondiendo
4. **→ Proceder con la migración de componentes**

---

## ⚠️ Si Algo Falla

1. **Anota el error exacto**:
   - Mensaje de error
   - Código HTTP (si aplica)
   - Endpoint que falla

2. **Revisa "Ver detalles"** en la página de prueba

3. **Revisa la consola del navegador** (F12 → Console)

4. **Comparte con el equipo backend**:
   - URL del endpoint
   - Request que envías
   - Response que recibes
   - Código de error

---

**¡Éxito con las verificaciones!** 🚀

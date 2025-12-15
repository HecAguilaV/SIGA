# 🔐 Guía de Autenticación - Desarrollo vs Producción

## 📋 Contexto

SIGA AppWeb tiene **dos flujos de autenticación**:

1. **Desarrollo/Pruebas**: Login directo en la AppWeb
2. **Producción**: Login desde la web comercial, luego redirección a AppWeb

---

## 🛠️ Para Desarrollo (Tu Caso Actual)

### Opción 1: Página de Login Dedicada ✅

**Ruta**: `/login`

**Pasos**:
1. Ve a `http://localhost:5173/login`
2. Ingresa tus credenciales del backend:
   - Email: `admin@siga.com` (o el que uses)
   - Contraseña: (tu contraseña)
3. Haz clic en "Iniciar Sesión"
4. Serás redirigido automáticamente a la página principal

**Ventajas**:
- ✅ Interfaz simple y clara
- ✅ Redirección automática después del login
- ✅ Manejo de errores visible

---

### Opción 2: Página de Prueba de Conexión

**Ruta**: `/prueba-conexion`

**Pasos**:
1. Ve a `http://localhost:5173/prueba-conexion`
2. Ingresa email y contraseña en los campos
3. Haz clic en "🔐 Probar Login"
4. Una vez autenticado, puedes usar la AppWeb normalmente

**Ventajas**:
- ✅ Muestra información detallada de la conexión
- ✅ Prueba múltiples endpoints
- ✅ Útil para debugging

---

## 🌐 Para Producción (Flujo Real)

### Flujo Esperado:

```
1. Usuario → Web Comercial
2. Usuario hace login en Web Comercial
3. Web Comercial genera token JWT
4. Web Comercial redirige a AppWeb con token
5. AppWeb recibe token (vía URL params, cookies, o localStorage compartido)
6. AppWeb guarda token y permite acceso
```

### Implementación Pendiente:

**Necesitas implementar**:
- [ ] Recibir token desde la web comercial (URL params, cookies, o postMessage)
- [ ] Guardar token automáticamente
- [ ] Redirigir a página principal

**Ejemplo de código** (para implementar después):

```javascript
// src/routes/+layout.svelte o src/routes/+page.svelte
import { onMount } from 'svelte';
import { page } from '$app/stores';
import { apiClient } from '$lib/api-client.js';

onMount(() => {
  // Si viene de la web comercial con token
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get('token');
  
  if (token) {
    // Guardar token
    apiClient.setTokens(token, null); // refreshToken si está disponible
    
    // Limpiar URL
    window.history.replaceState({}, '', '/');
    
    // Redirigir si es necesario
    if (!$page.url.pathname.startsWith('/')) {
      goto('/');
    }
  }
});
```

---

## 🔧 Configuración Actual

### Token Storage

El token se guarda en:
- **LocalStorage**: `accessToken` y `refreshToken`
- **Clave**: `localStorage.getItem('accessToken')`

### Verificación de Autenticación

```javascript
import { isAuthenticated } from '$lib/services/auth.js';

if (isAuthenticated()) {
  // Usuario autenticado
} else {
  // Redirigir a login
  goto('/login');
}
```

---

## 🚀 Uso Rápido para Desarrollo

### 1. Iniciar Servidor

```bash
pnpm run dev
```

### 2. Abrir Login

```
http://localhost:5173/login
```

### 3. Ingresar Credenciales

- **Email**: `admin@siga.com` (o el usuario que uses)
- **Contraseña**: (tu contraseña del backend)

### 4. Usar la AppWeb

Una vez autenticado, puedes:
- ✅ Ver inventario
- ✅ Crear productos
- ✅ Usar el asistente IA
- ✅ Gestionar stock

---

## ⚠️ Problemas Comunes

### Error: "Debes iniciar sesión para usar el asistente"

**Causa**: No hay token en localStorage

**Solución**:
1. Ve a `/login`
2. Haz login con tus credenciales
3. Verifica en DevTools → Application → LocalStorage que existe `accessToken`

### Error: "401 Unauthorized"

**Causa**: Token expirado o inválido

**Solución**:
1. Ve a `/login`
2. Haz login nuevamente
3. El sistema debería refrescar el token automáticamente

### Error: "No fue posible conectar con SIGA"

**Causa**: Error 500 en el servidor (endpoint `/api/chat`)

**Solución**:
1. Verifica que estés autenticado
2. Revisa la consola del servidor (terminal)
3. Verifica que el backend esté accesible

---

## 📝 Notas Importantes

1. **Para desarrollo**: Usa `/login` directamente
2. **Para producción**: Implementa el flujo de redirección desde web comercial
3. **Token**: Se guarda en localStorage, persiste entre recargas
4. **Refresh**: El sistema intenta refrescar el token automáticamente si expira

---

## ✅ Checklist

- [x] Página de login creada (`/login`)
- [x] Servicio de autenticación funcionando
- [x] Token se guarda en localStorage
- [ ] Protección de rutas (pendiente)
- [ ] Redirección desde web comercial (pendiente - producción)

---

**Para desarrollo, usa `/login` directamente. No necesitas pasar por la web comercial.** 🚀

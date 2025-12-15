# 🔌 Guía de Prueba de Conexión - Backend SIGA

## 📍 Acceso a la Página de Prueba

Una vez que el servidor esté corriendo, accede a:

```
http://localhost:5173/prueba-conexion
```

## 🧪 Pruebas a Realizar

### 1. Health Check (Automático)
- Se ejecuta automáticamente al cargar la página
- Verifica que el backend esté accesible
- Endpoint: `GET /health`

### 2. Login (Manual)
- Ingresa credenciales válidas de un usuario del backend
- Verifica autenticación JWT
- Guarda tokens en localStorage
- Endpoint: `POST /api/auth/login`

### 3. Endpoints Autenticados (Automático después de login)
Una vez autenticado, se pueden probar:
- **Locales**: `GET /api/saas/locales`
- **Categorías**: `GET /api/saas/categorias`
- **Productos**: `GET /api/saas/productos`

## ✅ Qué Verificar

### Health Check
- ✅ Debe responder con estado 200
- ✅ Debe mostrar información del backend

### Login
- ✅ Debe aceptar credenciales válidas
- ✅ Debe guardar tokens en localStorage
- ✅ Debe mostrar información del usuario

### Endpoints Autenticados
- ✅ Deben funcionar con el token JWT
- ✅ Deben retornar datos (arrays o objetos)
- ✅ No deben mostrar errores 401 (no autorizado)

## ⚠️ Posibles Problemas

### Error de CORS
**Síntoma**: Error en consola sobre CORS  
**Solución**: Verificar configuración CORS en el backend

### Error 401 Unauthorized
**Síntoma**: Endpoints autenticados fallan  
**Solución**: 
- Verificar que el login fue exitoso
- Verificar que el token se guardó correctamente
- Verificar formato del token en headers

### Error de Conexión
**Síntoma**: No se puede conectar al backend  
**Solución**:
- Verificar que `VITE_API_BASE_URL` esté configurada
- Verificar que el backend esté corriendo
- Verificar URL del backend

### Error 404 Not Found
**Síntoma**: Endpoint no existe  
**Solución**: Verificar que la ruta del endpoint sea correcta

## 📊 Interpretación de Resultados

### ✅ Éxito (Verde)
- Endpoint funciona correctamente
- Datos recibidos correctamente
- Listo para usar en producción

### ❌ Error (Rojo)
- Revisar mensaje de error
- Verificar logs del backend
- Contactar al equipo backend si es necesario

### ⏳ Probando (Azul)
- Endpoint en proceso de prueba
- Esperar resultado

## 🔍 Información Adicional

### Ver Detalles
Cada prueba tiene un botón "Ver detalles" que muestra:
- Respuesta completa del servidor
- Datos recibidos
- Información de debug

### Reintentar
Cada prueba tiene un botón para reintentar en caso de error.

## 📝 Notas para el Equipo Backend

Si encuentras problemas durante las pruebas:

1. **Anota el error exacto** (mensaje, código HTTP)
2. **Captura la respuesta del servidor** (usando "Ver detalles")
3. **Verifica los logs del backend**
4. **Comparte la información** con el equipo para resolver conflictos

## 🚀 Siguiente Paso

Una vez que todas las pruebas pasen:
- ✅ Health Check: OK
- ✅ Login: OK
- ✅ Locales: OK
- ✅ Categorías: OK
- ✅ Productos: OK

**Proceder con la migración de componentes** usando los servicios implementados.

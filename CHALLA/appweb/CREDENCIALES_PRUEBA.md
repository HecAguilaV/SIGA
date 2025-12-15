# 🔐 Credenciales de Prueba - Backend SIGA

## 📋 Credenciales Disponibles

### Usuario Administrador
- **Email**: `admin@siga.com`
- **Rol**: ADMINISTRADOR
- **Estado**: ✅ Disponible
- **Nota**: Requiere suscripción activa para acceder a endpoints

---

## 🔍 Obtener Más Credenciales

### Opción 1: Contactar al Equipo Backend

**Pregunta al equipo backend**:
- ¿Hay otros usuarios de prueba disponibles?
- ¿Qué credenciales puedo usar para testing?
- ¿Hay usuarios con diferentes roles (OPERADOR, ADMINISTRADOR)?
- ¿Hay usuarios con suscripción activa para probar endpoints?

**Información útil para compartir**:
- Necesitas credenciales para probar la integración frontend
- Necesitas usuarios con suscripción activa
- Necesitas usuarios con diferentes roles para probar permisos

---

### Opción 2: Crear Usuario de Prueba

Puedes usar el endpoint de registro para crear un usuario:

**Endpoint**: `POST /api/auth/register`

**Request**:
```json
{
  "email": "test@example.com",
  "password": "test123456",
  "nombre": "Usuario",
  "apellido": "Prueba",
  "rol": "OPERADOR"
}
```

**Nota**: Este usuario también necesitará una suscripción activa para acceder a endpoints.

---

### Opción 3: Verificar en Swagger UI

1. Ve a: `https://siga-backend-production.up.railway.app/swagger-ui/index.html`
2. Busca ejemplos de requests/responses
3. Puede haber información sobre usuarios de prueba

---

## ⚠️ Importante

### Suscripción Activa

Todos los usuarios (incluso ADMINISTRADOR) necesitan:
- ✅ Token JWT válido (se obtiene con login)
- ❌ **Suscripción activa** (esto es lo que falta)

**Solución**: Contactar al equipo backend para:
- Activar suscripción para `admin@siga.com`
- O crear un usuario de prueba con suscripción activa

---

## 📝 Roles Disponibles

Según la documentación:

1. **ADMINISTRADOR**:
   - Acceso completo
   - Puede crear/editar/eliminar productos
   - Puede ver todos los locales

2. **OPERADOR**:
   - Solo puede actualizar stock
   - Solo puede crear ventas
   - Solo en sus locales asignados

---

## 🔧 Para Probar Diferentes Escenarios

### Escenario 1: Usuario con Suscripción Activa
- **Necesitas**: Usuario con suscripción activa
- **Para probar**: Endpoints autenticados (locales, categorías, productos)

### Escenario 2: Usuario OPERADOR
- **Necesitas**: Usuario con rol OPERADOR
- **Para probar**: Permisos limitados, solo stock y ventas

### Escenario 3: Usuario ADMINISTRADOR
- **Tienes**: `admin@siga.com`
- **Falta**: Suscripción activa
- **Para probar**: Permisos completos (una vez activada la suscripción)

---

## ✅ Checklist

- [ ] Tienes credenciales de `admin@siga.com` ✅
- [ ] Contactaste al equipo backend para más credenciales
- [ ] Obtuviste usuario con suscripción activa
- [ ] Probaste login con diferentes usuarios
- [ ] Probaste endpoints con diferentes roles

---

## 📞 Contacto

**Equipo Backend**: 
- Preguntar sobre credenciales de prueba
- Solicitar activación de suscripción
- Solicitar creación de usuarios de prueba

---

**Nota**: Las credenciales reales no deben estar en este documento por seguridad. Este documento es solo una guía de cómo obtenerlas.

# 🐛 Problemas Backend Reportados - WebApp

**Fecha:** 2025-01-XX  
**Equipo:** WebApp  
**Prioridad:** Alta

---

## 📋 Resumen

Este documento contiene problemas identificados en el backend que requieren atención del equipo backend para el correcto funcionamiento de la WebApp.

---

## 1. ❌ Error 503 en Asistente IA (`POST /api/saas/chat`)

### Descripción
El asistente IA retorna error `503 (Service Unavailable)` cuando se intenta realizar operaciones CRUD desde el chat.

### Contexto
- **Endpoint:** `POST /api/saas/chat`
- **Request:** Mensaje del usuario solicitando operaciones CRUD (ej: "AGREGA 10 BEBIDA FANTA AL STOCK")
- **Response:** `503 Service Unavailable`
- **Mensaje:** "El backend está teniendo problemas al procesar tu solicitud"

### Logs del Frontend
```
POST https://siga-backend-production.up.railway.app/api/saas/chat 503 (Service Unavailable)
Error: ⚠️ El backend está teniendo problemas al procesar tu solicitud. 
Error: Error al procesar la solicitud con el asistente IA. Por favor, intente más tarde.
```

### Impacto
- **Alto:** El asistente IA es una funcionalidad crítica del MVP
- Los usuarios no pueden realizar operaciones CRUD desde el chat
- Interrumpe el flujo de trabajo del usuario

### Acción Requerida
1. Verificar logs del backend para identificar la causa del 503
2. Confirmar que el servicio de Gemini/IA está operativo
3. Verificar que el endpoint `/api/saas/chat` está correctamente implementado
4. Proporcionar información sobre el estado del servicio

---

## 2. ❌ Usuarios de Otras Empresas Visibles en Gestión de Usuarios

### Descripción
En la página de gestión de usuarios (`/usuarios`), los administradores pueden ver usuarios de otras empresas/tenants.

### Contexto
- **Endpoint:** `GET /api/saas/usuarios`
- **Problema:** No se está filtrando por empresa/tenant del usuario autenticado
- **Impacto:** Violación de seguridad y privacidad de datos

### Comportamiento Esperado
- Un administrador solo debe ver usuarios de su propia empresa
- El filtrado debe ser automático basado en el token JWT del usuario autenticado
- No debe ser necesario enviar parámetros adicionales en el request

### Comportamiento Actual
- Se muestran usuarios de todas las empresas
- No hay filtrado por tenant/empresa

### Impacto
- **Crítico:** Violación de seguridad y privacidad
- Los administradores pueden ver y potencialmente modificar usuarios de otras empresas
- Incumplimiento de multi-tenancy

### Acción Requerida
1. Implementar filtrado automático por empresa/tenant en `GET /api/saas/usuarios`
2. El filtrado debe basarse en el token JWT del usuario autenticado
3. Verificar que todos los endpoints de usuarios respeten el multi-tenancy:
   - `GET /api/saas/usuarios`
   - `POST /api/saas/usuarios`
   - `PUT /api/saas/usuarios/{id}`
   - `DELETE /api/saas/usuarios/{id}`
   - `GET /api/saas/usuarios/{id}/permisos`
   - `POST /api/saas/usuarios/{id}/permisos`
   - `DELETE /api/saas/usuarios/{id}/permisos`

---

## 3. ⚠️ Endpoint `POST /api/saas/stock` No Documentado

### Descripción
El endpoint `POST /api/saas/stock` para actualizar stock no está documentado en `FUENTE_VERDAD_BACKEND.md`, pero el frontend lo está utilizando según `API_FRONTEND_APPWEB.md`.

### Contexto
- **Documentación Oficial (`FUENTE_VERDAD_BACKEND.md`):** Solo menciona `GET /api/saas/stock`
- **Documentación Frontend (`API_FRONTEND_APPWEB.md`):** Menciona `POST /api/saas/stock`
- **Estado:** El endpoint existe pero retorna `400 Bad Request` con "Error de validación"

### Request Actual
```json
{
  "productoId": 1,
  "localId": 1,
  "cantidad": 23,
  "cantidadMinima": 0
}
```

### Acción Requerida
1. Confirmar si `POST /api/saas/stock` está implementado
2. Si está implementado:
   - Documentar el formato exacto esperado en `FUENTE_VERDAD_BACKEND.md`
   - Verificar que el formato del request del frontend coincida
   - Proporcionar mensajes de error más descriptivos (actualmente solo dice "Error de validación")
3. Si no está implementado:
   - Implementar el endpoint según la especificación en `API_FRONTEND_APPWEB.md`
   - Documentar en `FUENTE_VERDAD_BACKEND.md`

---

## 📝 Notas Adicionales

- Todos los problemas reportados han sido identificados durante pruebas en producción
- El frontend está enviando los datos según la documentación disponible
- Se recomienda revisar los logs del backend para obtener más detalles sobre los errores

---

## 🔗 Referencias

- `FUENTE_VERDAD_BACKEND.md` - Documento oficial del backend
- `API_FRONTEND_APPWEB.md` - Documentación de API para frontend
- Logs de consola del navegador (disponibles en producción)
